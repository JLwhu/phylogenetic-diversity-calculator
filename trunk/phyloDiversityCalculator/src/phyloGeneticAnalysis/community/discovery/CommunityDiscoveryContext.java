package phyloGeneticAnalysis.community.discovery;

import java.util.Set;

import edu.uci.ics.jung.graph.Graph;

import pal.tree.Tree;
import phyloGeneticAnalysis.community.diversity.CommunityDiversityDistanceStrategy;
import weka.core.Instances;

public class CommunityDiscoveryContext<V,E> {
	CommunityDiscoveryStrategy strategy;

	public CommunityDiscoveryContext(CommunityDiscoveryStrategy strategy, CommunityDiversityDistanceStrategy distanceStrategy){
		this.strategy = strategy;
		strategy.setDiversityStrategy(distanceStrategy);
	}

	public String commnityDiscovery(Tree tree, int[][] regionSpecies){
		return strategy.commnityDiscovery(tree, regionSpecies);
	}
	
	public String commnityDiscovery(Tree tree, Instances regionSpecies){
		return strategy.commnityDiscovery(tree, regionSpecies);
	}
	
	public double[][] distanceMatrix(Tree tree, int[][] regionSpecies){
		return strategy.distanceMatrix(tree, regionSpecies);
	}

	public double commnityDiscoveryEvaluation(Tree tree, int[][] regionSpecies, int[] membership){
		return strategy.commnityDiscoveryEvaluation(tree, regionSpecies, membership);
	}
}
