package phyloGeneticAnalysis.community.diversity;

import java.util.HashMap;
import java.util.Set;

import pal.tree.Tree;
import weka.core.Instance;

public class CommunityDiversityContext {
	CommunityDiversityDistanceStrategy strategy;

	public CommunityDiversityContext(CommunityDiversityDistanceStrategy strategy){
		this.strategy = strategy;
	}
	public double communityDiversity(Set A, Set B) throws Exception{
		return strategy.distance(A, B);
	}
	
	public double communityDiversity(Set A, Set B, HashMap abundanceMap) throws Exception{
		return strategy.distance(A, B, abundanceMap);
	}
	
	
	public double communityDiversity(Instance A, Instance B){
		return strategy.distance(A, B);
	}

}
