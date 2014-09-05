package phyloGeneticAnalysis.community.diversity;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;



import pal.tree.Node;
import pal.tree.SimpleTree;
import pal.tree.Tree;
import pal.tree.TreeUtils;
import phyloGeneticAnalysis.Util.RootedTreeSubtreeUtils;
import phyloGeneticAnalysis.community.diversity.treeUtil.TreeUtilsMore;
import weka.core.Instance;

public class PhyloSorDistance extends CommunityDiversityDistanceStrategy{

	public PhyloSorDistance(Tree phylotree) {
		super(phylotree);
		// TODO Auto-generated constructor stub
	}

	@Override
	public double distance(Set<String> A,
			Set<String> B) {	
		Tree tree = this.getPhylogeneticTree();
		Set<Node> nodesetA = getNodeSetFromNameSet(tree, A);
		Set<Node> nodesetB = getNodeSetFromNameSet(tree, B);
		
		Set intersect = new HashSet<Node>();
		intersect.clear();
		intersect.addAll(nodesetA);
		intersect.retainAll(nodesetB);	
		
		RootedTreeSubtreeUtils rtsu = new RootedTreeSubtreeUtils();
		
		double intersectSum = 0;
		int incount = tree.getInternalNodeCount();
		for (int i=0;i<incount;i++){
			Node node = tree.getInternalNode(i);
			double anum = TreeUtilsMore.getCommonNodeNumberUnderNode(node, nodesetA);
			double bnum = TreeUtilsMore.getCommonNodeNumberUnderNode(node, nodesetB);
			if (anum>0 && bnum>0)
				intersectSum += node.getBranchLength();
		}
		
		Iterator it = intersect.iterator();
		while (it.hasNext()){
			Node node  = (Node)it.next();
			intersectSum += node.getBranchLength();
		}
		
		SimpleTree subtreeA = (SimpleTree) rtsu.getSpecifiedSubTree(
				this.getPhylogeneticTree(), A);
		SimpleTree subtreeB = (SimpleTree) rtsu.getSpecifiedSubTree(
				this.getPhylogeneticTree(), B);
		double sumA = TreeUtilsMore.getBrachlengthSum(subtreeA);
		double sumB = TreeUtilsMore.getBrachlengthSum(subtreeB);

		return 2 * intersectSum / (sumA + sumB);

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
		// TODO Auto-generated method stub
		return 0;
	}	

}
