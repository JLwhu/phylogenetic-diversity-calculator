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

public class SimpleKMeansExtensionNonVirtualCenter extends SimpleKMeansExtension {

	/**
	 * Move the centroid to it's new coordinates. Generate the centroid
	 * coordinates based on it's members (objects assigned to the cluster of the
	 * centroid) and the distance function being used.
	 * 
	 * @param centroidIndex
	 *            index of the centroid which the coordinates will be computed
	 * @param members
	 *            the objects that are assigned to the cluster of this centroid
	 * @param updateClusterInfo
	 *            if the method is supposed to update the m_Cluster arrays
	 * @return the centroid coordinates
	 */
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

		//calculate the distances within the cluster  added by Jing Liu 8/25/2013
		double[][] distance = new double[members.numInstances()][members
				.numInstances()];
		for (int j = 0; j < members.numInstances(); j++) {
			distance[j][j] = 0;
			for (int k = j + 1; k < members.numInstances(); k++) {
				distance[j][k] = m_DistanceFunction.distance(
						members.instance(j), members.instance(k));
				distance[k][j] = distance[j][k];
			}
		}
		
		//calculate the minimum cover diameter of the cluster. 
		//1.caculate the maximum distance among any two nodes within the cluster  added by Jing Liu 8/25/2013
		double[] rowMax = new double[members.numInstances()];
		for (int j = 0; j < members.numInstances(); j++) {
			rowMax[j]=0;
			for (int k = 0; k < members.numInstances(); k++) {
				if (distance[j][k]>rowMax[j])
					rowMax[j] = distance[j][k];
			}
		}
		//2. calculate the minimum diameter added by Jing Liu 8/25/2013
		int newCenterIdx = 0;
		double diameter = rowMax[0];
		for (int j = 0; j < members.numInstances(); j++) {
			if (rowMax[j] < diameter) {
				diameter = rowMax[j];
				newCenterIdx = j;
			}
		}
		
		//update the center using the node of minimun cover added by Jing Liu 8/25/2013
		for (int j = 0; j < members.numAttributes(); j++){
			vals[j] = members.instance(newCenterIdx).value(j);
		}
		
		if (updateClusterInfo)
			m_ClusterCentroids.add(new Instance(1.0, vals));
		return vals;
	}

	

}
