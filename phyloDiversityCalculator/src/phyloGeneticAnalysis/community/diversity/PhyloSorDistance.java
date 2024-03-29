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
			Set<String> B)  throws Exception{	
		Tree tree = this.getPhylogeneticTree();
		Set<Node> nodesetA = getNodeSetFromNameSet(tree, A);
		Set<Node> nodesetB = getNodeSetFromNameSet(tree, B);
		
		Set intersect = new HashSet<Node>();
		intersect.clear();
		intersect.addAll(nodesetA);
		intersect.retainAll(nodesetB);	
		
		RootedTreeSubtreeUtils rtsu = new RootedTreeSubtreeUtils();
		
		double intersectSum = 0;
		Set internalNodes = TreeUtilsMore.getCommonInternalNodeIDSet(nodesetA,nodesetB);
		Iterator it = internalNodes.iterator();
		while(it.hasNext()){
			Integer nodeID = (Integer) it.next();
			Node node = tree.getInternalNode(nodeID.intValue());
			intersectSum+=node.getBranchLength();
		}
		
		it = intersect.iterator();
		while(it.hasNext()){
			Node node = (Node) it.next();
			intersectSum+=node.getBranchLength();
		}
		
	/*	int incount = tree.getInternalNodeCount();
		for (int i=0;i<incount;i++){ // incount
			Node node = tree.getInternalNode(i);
			if (!node.isRoot()) {
				double anum = TreeUtilsMore.getCommonNodeNumberUnderNode(node,
						nodesetA);
				double bnum = TreeUtilsMore.getCommonNodeNumberUnderNode(node,
						nodesetB);
				if (anum > 0 && bnum > 0)
					intersectSum += node.getBranchLength();
			}
		}
		
				SimpleTree subtreeA = (SimpleTree) rtsu.getSpecifiedSubTree(
				this.getPhylogeneticTree(), A);
				SimpleTree subtreeB = (SimpleTree) rtsu.getSpecifiedSubTree(
				this.getPhylogeneticTree(), B);
				
				double sumA = TreeUtilsMore.getBrachlengthSum(subtreeA);
				double sumB = TreeUtilsMore.getBrachlengthSum(subtreeB);
		
		*/

		double sumA = 0;
		double sumB = 0;

		Set internalNodesA = TreeUtilsMore.getInternalNodeIDSet(nodesetA);
		Set internalNodesB = TreeUtilsMore.getInternalNodeIDSet(nodesetB);
		it = internalNodesA.iterator();
		while(it.hasNext()){
			Integer nodeID = (Integer) it.next();
			Node node = tree.getInternalNode(nodeID.intValue());
			sumA+=node.getBranchLength();
		}
		it = nodesetA.iterator();
		while(it.hasNext()){
			Node node = (Node) it.next();
			sumA+=node.getBranchLength();
		}

		it = internalNodesB.iterator();
		while(it.hasNext()){
			Integer nodeID = (Integer) it.next();
			Node node = tree.getInternalNode(nodeID.intValue());
			sumB+=node.getBranchLength();
		}
		it = nodesetB.iterator();
		while(it.hasNext()){
			Node node = (Node) it.next();
			sumB+=node.getBranchLength();
		}
		return 2 * intersectSum / (sumA + sumB);

	}

	@Override
	public double distance(Instance first, Instance second) {
		Tree tree = this.getPhylogeneticTree();
		Set<String> A = getNodeNameSetFromInstance(tree,first);
		Set<String> B = getNodeNameSetFromInstance(tree,second);
		double distance=0;
		try {
			distance = distance(A,B);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		return distance;
	}

	@Override
	public double distance(Set<String> A, Set<String> B, HashMap abundanceMap)  throws Exception{
		double distance = distance(A,B);
		
		return distance;
	}	

}
