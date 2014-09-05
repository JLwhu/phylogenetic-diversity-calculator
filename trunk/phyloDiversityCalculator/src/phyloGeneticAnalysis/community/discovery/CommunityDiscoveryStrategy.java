package phyloGeneticAnalysis.community.discovery;

import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

import pal.tree.Tree;
import phyloGeneticAnalysis.community.diversity.CommunityDiversityDistanceStrategy;
import phyloGeneticAnalysis.community.evaluation.Modularity;
import weka.core.Attribute;
import weka.core.FastVector;
import weka.core.Instance;
import weka.core.Instances;

import edu.uci.ics.jung.graph.Graph;


public abstract class CommunityDiscoveryStrategy {
	private CommunityDiversityDistanceStrategy distanceStrategy;
	public void setDiversityStrategy(CommunityDiversityDistanceStrategy distanceStrategy){
		this.distanceStrategy = distanceStrategy;
	};
	
	public CommunityDiversityDistanceStrategy getDiversityStrategy(){
		return this.distanceStrategy;
	};
	public Instances createRegionInstances(Tree tree, int regionCount){
		String relationName = "species";
		// Create vectors to hold information temporarily.
		FastVector attributes = new FastVector();

		for (int i = 0; i < tree.getExternalNodeCount(); i++) {
			attributes.addElement(new Attribute(tree.getExternalNode(i)
					.getIdentifier().getName(), attributes.size()));
		}		
		Instances regionInstances = new Instances(relationName, attributes, regionCount);
		return regionInstances;
	}
	
	public Instances createRegionInstances(Tree tree, int[][] regionSpecies){
		double weight =1;
		int regionCount = regionSpecies.length;
		int speciesCount = tree.getExternalNodeCount();		

		Instances regionInstances = createRegionInstances(tree, regionCount);
		for (int i = 0; i < regionCount; i++) {
			double[] attValue = new double[speciesCount];
			for (int j=0;j<speciesCount;j++){
				attValue[j] = regionSpecies[i][j];
			}
			Instance instance = new Instance(weight, attValue);
			regionInstances.add(instance);
		}
		return regionInstances;
	}
	
	public double[][] distanceMatrix(Tree tree, int[][] regionSpecies){
		int regionCount = regionSpecies.length;
		Instances instances = createRegionInstances(tree,regionSpecies);
		double[][] distanceMatrix = new double[regionCount][regionCount];
		
		for (int i = 0; i < regionCount; i++) {
			for (int j = 0; j < regionCount; j++) {
				distanceMatrix[i][j] = distanceStrategy.distance(instances.instance(i), instances.instance(j));
			}
		}
		return distanceMatrix;
	}
	
	public double commnityDiscoveryEvaluation(Tree tree, int[][] regionSpecies, int[] membership){
		double modularity = 0;
		double[][] distanceMatrix = distanceMatrix(tree, regionSpecies);
		Modularity mod = new Modularity();
		modularity = mod.getGraphModularity(distanceMatrix, membership);
		return modularity;
	}
	
	abstract public String commnityDiscovery(Tree tree, int[][] regionSpecies);
	abstract public String commnityDiscovery(Tree tree, Instances regionSpecies);

}
