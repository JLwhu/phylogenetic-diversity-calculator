package phyloGeneticAnalysis.community.diversity;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;

import pal.tree.Node;
import pal.tree.Tree;
import phyloGeneticAnalysis.community.math.Combination;
import weka.core.Instance;

public class SorensenIndexDistance extends
		CommunityDiversityDistanceStrategy {

	public SorensenIndexDistance(Tree phylotree) {
		super(phylotree);
		// TODO Auto-generated constructor stub
	}

	@Override
	public double distance(Set<String> A,
			Set<String> B) {
		// TODO Auto-generated method stub
		Set intersection = new HashSet<Node>();

		intersection.clear();
		intersection.addAll(A);
		intersection.retainAll(B);
		double interSize = intersection.size();

		return interSize*2/(A.size()+B.size());
	}

	@Override
	public double distance(Instance first, Instance second) {
		Tree tree = this.getPhylogeneticTree();
		Set<String> A = getNodeNameSetFromInstance(tree,first);
		Set<String> B = getNodeNameSetFromInstance(tree,second);
		double distance = distance(A,B);
		
		return distance;
	}

	@Override
	public double distance(Set<String> A, Set<String> B, HashMap abundanceMap) {
		double distance = distance(A,B);
		
		return distance;
	}

}
