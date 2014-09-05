package phyloGeneticAnalysis.community.discovery;

import java.util.Enumeration;
import java.util.HashMap;
import java.util.Random;
import java.util.Vector;

import phyloGeneticAnalysis.community.diversity.CommunityDiversityDistanceStrategy;

import weka.classifiers.rules.DecisionTableHashKey;
import weka.clusterers.NumberOfClustersRequestable;
import weka.clusterers.RandomizableClusterer;
import weka.core.Attribute;
import weka.core.Capabilities;
import weka.core.DistanceFunction;
import weka.core.EuclideanDistance;
import weka.core.Instance;
import weka.core.Instances;
import weka.core.ManhattanDistance;
import weka.core.Option;
import weka.core.RevisionUtils;
import weka.core.Utils;
import weka.core.WeightedInstancesHandler;
import weka.core.Capabilities.Capability;
import weka.filters.Filter;
import weka.filters.unsupervised.attribute.ReplaceMissingValues;

public class SimpleKMeansExtensionVirtualCenterGreedy extends SimpleKMeansExtension {
	protected double[] moveCentroid(int centroidIndex, Instances members,
			boolean updateClusterInfo) {
		double[] vals = new double[members.numAttributes()];

		// used only for Manhattan Distance
		Instances sortedMembers = null;
		int middle = 0;
		boolean dataIsEven = false;

		if (m_DistanceFunction instanceof ManhattanDistance) {
			middle = (members.numInstances() - 1) / 2;
			dataIsEven = ((members.numInstances() % 2) == 0);
			if (m_PreserveOrder) {
				sortedMembers = members;
			} else {
				sortedMembers = new Instances(members);
			}
		}

		
		for (int j = 0; j < members.numAttributes(); j++) {

			// in case of Euclidian distance the centroid is the mean point
			// in case of Manhattan distance the centroid is the median point
			// in both cases, if the attribute is nominal, the centroid is the
			// mode
			if (m_DistanceFunction instanceof EuclideanDistance
					|| members.attribute(j).isNominal()) {
				vals[j] = members.meanOrMode(j);
			} else if (m_DistanceFunction instanceof ManhattanDistance) {
				// singleton special case
				if (members.numInstances() == 1) {
					vals[j] = members.instance(0).value(j);
				} else {
					vals[j] = sortedMembers.kthSmallestValue(j, middle + 1);
					if (dataIsEven) {
						vals[j] = (vals[j] + sortedMembers.kthSmallestValue(j,
								middle + 2)) / 2;
					}
				}
			} else {  //to deal with the community distance added by Jing Liu 8/19/2013
				vals[j] = members.meanOrMode(j);
			}

			if (updateClusterInfo) {
				m_ClusterMissingCounts[centroidIndex][j] = members
						.attributeStats(j).missingCount;
				m_ClusterNominalCounts[centroidIndex][j] = members
						.attributeStats(j).nominalCounts;
				if (members.attribute(j).isNominal()) {
					if (m_ClusterMissingCounts[centroidIndex][j] > m_ClusterNominalCounts[centroidIndex][j][Utils
							.maxIndex(m_ClusterNominalCounts[centroidIndex][j])]) {
						vals[j] = Instance.missingValue(); // mark mode as
															// missing
					}
				} else {
					if (m_ClusterMissingCounts[centroidIndex][j] == members
							.numInstances()) {
						vals[j] = Instance.missingValue(); // mark mean as
															// missing
					}
				}
			}
		}		

		findOptimalCenter(vals, members);
		
		if (updateClusterInfo)
			m_ClusterCentroids.add(new Instance(1.0, vals));
		return vals;
	}
	
	

	private Instance findOptimalCenter(double[] vals, Instances members){
		double delta = 0.001;
		double diameter;
		double maxDia=0;
		
		double[] difference = new double[members.numAttributes()];
		Instance center = new Instance(1.0, vals);
		do {
			diameter = maxDia;
			maxDia=0;
			
			// calculate the ditances within the cluster added by Jing Liu
			// 8/25/2013
			double[] distance = new double[members.numInstances()];
			int idx = 0;
			for (int j = 0; j < members.numInstances(); j++) {
				distance[j] = m_DistanceFunction.distance(center,
						members.instance(j));
				if (distance[j] > maxDia) {
					maxDia = distance[j];
					idx = j;
				}
			}
			if (maxDia < diameter) {
				double maxDif = 0;
				int attIdx = 0;
				for (int j = 0; j < members.numAttributes(); j++) {
					difference[j] = members.instance(idx).value(j)
							- center.value(j);
					if (difference[j] > maxDif) {
						maxDif = difference[j];
						attIdx = j;
					}
				}

				for (int j = 0; j < members.numAttributes(); j++) {
					vals[j] += difference[j] * delta;
				}
			}
		} while (maxDia < diameter);
		for (int j = 0; j < members.numAttributes(); j++) {
			vals[j] -= difference[j] * delta;
		}
		return center;
	}
	
	
}
