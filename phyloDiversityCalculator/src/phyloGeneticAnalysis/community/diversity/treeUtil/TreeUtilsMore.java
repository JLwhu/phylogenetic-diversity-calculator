/*
 * By Jing Liu 
 */
package phyloGeneticAnalysis.community.diversity.treeUtil;

import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.Queue;
import java.util.Set;

import pal.tree.Node;
import pal.tree.SimpleTree;
import pal.tree.Tree;
import weka.core.Instance;


// TODO: Auto-generated Javadoc
/**
 * The Class TreeUtilsMore.
 */
public class TreeUtilsMore {
	
	/**
	 * Gets the brachlength sum.
	 *
	 * @param tree the tree
	 * @return the brachlength sum
	 */
	public static double getBrachlengthSum(Tree tree){
		double sum=0;
		int inNodeCount = tree.getInternalNodeCount();
		for (int i=0;i<inNodeCount;i++){
			sum += tree.getInternalNode(i).getBranchLength();
		}
		
		int exNodeCount = tree.getExternalNodeCount();
		for (int i=0;i<exNodeCount;i++){
			sum += tree.getExternalNode(i).getBranchLength();
		}
		sum -= tree.getRoot().getBranchLength();
		return sum;
	}	

	

	/**
	 * Sets the brachlength to one.
	 *
	 * @param tree the new brachlength to one
	 */
	public static void setBrachlengthToOne(Tree tree){
	//	double sum=0;
		int inNodeCount = tree.getInternalNodeCount();
		for (int i=0;i<inNodeCount;i++){
			tree.getInternalNode(i).setBranchLength(1);
		}
		
		int exNodeCount = tree.getExternalNodeCount();
		for (int i=0;i<exNodeCount;i++){
			tree.getExternalNode(i).setBranchLength(1);
		}
	}	

	
	
	
	/**
	 * returns the number of nodes of set under node.
	 *
	 * @param node the node
	 * @param set the set
	 * @return the common node number under node
	 */
	public static double getCommonNodeNumberUnderNode(Node node, Set<Node> set){
		int ret = 0;
		Set<Node> externalnodeSet = new HashSet<Node>();
		
		Queue<Node> queue = new LinkedList<Node>();
		queue.add(node);
		while (!queue.isEmpty()){
			Node curNode = queue.remove();
			if (curNode.isLeaf()){
				externalnodeSet.add(curNode);
			}else{
				for (int i = 0; i < node.getChildCount(); i++) {
					queue.add(curNode.getChild(i));
				}
			}
		}
		
		Set intersection = new HashSet<Node>();
		
		intersection.clear();
		intersection.addAll(externalnodeSet);
		intersection.retainAll(set);
		ret = intersection.size();
		
		return ret;
	}
	
	/**
	 * returns the set of intersection of internal nodes Set A and B.
	 *
	 * @param Set<Node> A
	 * @param Set<Node> B
	 * @return the common node number under node
	 */
	public static Set getCommonInternalNodeIDSet(Set<Node> A,Set<Node> B){
		Set<Integer> internaleNodeSetA = new HashSet<Integer>();
		Set<Integer> internaleNodeSetB = new HashSet<Integer>();
		
		Iterator it = A.iterator();
		while(it.hasNext()){
			Node node = (Node) it.next();
			while (!node.isRoot()){
				node = node.getParent();
				internaleNodeSetA.add(node.getNumber());
			}
		}
		
		it = B.iterator();
		while(it.hasNext()){
			Node node = (Node) it.next();
			while (!node.isRoot()){
				node = node.getParent();
				internaleNodeSetB.add(node.getNumber());
			}
		}
		
		Set intersection = new HashSet<Integer>();
		
		intersection.clear();
		intersection.addAll(internaleNodeSetA);
		intersection.retainAll(internaleNodeSetB);
		
		return intersection;
	}
	
	/**
	 * returns the set of internal nodes Set A and B.
	 *
	 * @param Set<Node> A
	 * @return the common node number under node
	 */
	public static Set getInternalNodeIDSet(Set<Node> A){
		Set<Integer> internaleNodeSetA = new HashSet<Integer>();
		
		Iterator it = A.iterator();
		while(it.hasNext()){
			Node node = (Node) it.next();
			while (!node.isRoot()){
				node = node.getParent();
				internaleNodeSetA.add(node.getNumber());
			}
		}		

		return internaleNodeSetA;
	}
	
	/**
	 * returns the number of nodes of set under node.
	 *
	 * @param tree the tree
	 * @param node the node
	 * @param instance the instance
	 * @return the common node number under node
	 */
	public static int[] getCommonNodeIdxUnderNode(Tree tree, Node node, Instance instance){
		Set<Node> externalnodeSet = new HashSet<Node>();
		
		Queue<Node> queue = new LinkedList<Node>();
		queue.add(node);
		while (!queue.isEmpty()){
			Node curNode = queue.remove();
			if (curNode.isLeaf()){
				externalnodeSet.add(curNode);
			}else{
				for (int i = 0; i < node.getChildCount(); i++) {
					queue.add(curNode.getChild(i));
				}
			}
		}

		Iterator it = externalnodeSet.iterator();
		int[] ret = new int[tree.getExternalNodeCount()];
		while (it.hasNext()){
			Node curnode = (Node) it.next();
			if (instance.value(curnode.getNumber())==1)
				ret[curnode.getNumber()]=1;
		}
		
		return ret;
	}
	
	
	
	/**
	 * Gets the sorted branch weight.
	 *
	 * @param tree the tree
	 * @return the sorted branch weight
	 */
	public static double[] getSortedBranchWeight(Tree tree){

		int inNodeCount = tree.getInternalNodeCount();
		int exNodeCount = tree.getExternalNodeCount();
		double[] branches = getBranchWeight(tree);
		
		for (int i = 0; i < inNodeCount + exNodeCount - 1; i++)
			for (int j = 0; j < inNodeCount + exNodeCount - i - 1; j++)
				if (branches[j + 1] < branches[j]) {
					double t = branches[j + 1];
					branches[j + 1] = branches[j];
					branches[j] = t;
				}
		
		return branches;
	}
	
	/**
	 * Gets the branch weight.
	 *
	 * @param tree the tree
	 * @return the branch weight
	 */
	public static double[] getBranchWeight(Tree tree){

		int inNodeCount = tree.getInternalNodeCount();
		int exNodeCount = tree.getExternalNodeCount();
		double[] branches = new double[inNodeCount+exNodeCount];
		int k=0;
		
		for (int i=0;i<inNodeCount;i++){
			branches[k] = tree.getInternalNode(i).getBranchLength();
			k++;
		}		

		for (int i=0;i<exNodeCount;i++){
			branches[k] = tree.getExternalNode(i).getBranchLength();
			k++;
		}
		
		return branches;
	}
	
	/**
	 * Gets the tree balance value.
	 *
	 * @param tree the tree
	 * @return the tree balance value
	 */
	public static double getTreeBalanceValue(Tree tree) {
		Set[] leftNodesSet = new Set[tree.getInternalNodeCount()];
		Set[] rightNodesSet = new Set[tree.getInternalNodeCount()];

		for (int i = 0; i < tree.getExternalNodeCount(); i++) {
			Node node = tree.getExternalNode(i);
			Node idxNode = node.getParent();
			while (!node.isRoot()) {
				if (node.isLeaf()) {
					if (idxNode.getChild(0).equals(node)){
						Set nodeset = new HashSet();
						nodeset.add(node);
						leftNodesSet[idxNode.getNumber()]=nodeset;
					}
					else if (idxNode.getChild(1).equals(node)){
						Set nodeset = new HashSet();
						nodeset.add(node);
						rightNodesSet[idxNode.getNumber()]=nodeset;
					}
				} else {
					if (idxNode.getChild(0).equals(node)) {
						if (leftNodesSet[idxNode.getNumber()]==null){
							Set nodeset = new HashSet();
							leftNodesSet[idxNode.getNumber()]=nodeset;
						}
						if (leftNodesSet[node.getNumber()]!=null)
							leftNodesSet[idxNode.getNumber()].addAll(leftNodesSet[node
								.getNumber()]);
						if (rightNodesSet[node.getNumber()]!=null)
							leftNodesSet[idxNode.getNumber()]
								.addAll(rightNodesSet[node.getNumber()]);
					} else if (idxNode.getChild(1).equals(node)) {
						if (rightNodesSet[idxNode.getNumber()]==null){
							Set nodeset = new HashSet();
							rightNodesSet[idxNode.getNumber()]=nodeset;
						}
						if (leftNodesSet[node.getNumber()]!=null)
							rightNodesSet[idxNode.getNumber()]
								.addAll(leftNodesSet[node.getNumber()]);
						if (rightNodesSet[node.getNumber()]!=null)
							rightNodesSet[idxNode.getNumber()]
								.addAll(rightNodesSet[node.getNumber()]);
					}
				}
				node = idxNode;
				idxNode = idxNode.getParent();
				if (node.equals(idxNode)){
					int j=0;
					j++;
				}
			}

		}
		
		double sum = 0;

		for (int i = 0; i < tree.getInternalNodeCount(); i++) {
			double left = 0;
			double right = 0;
			if (leftNodesSet[tree.getInternalNode(i).getNumber()]!=null)
				left = leftNodesSet[tree.getInternalNode(i).getNumber()].size();
			if (rightNodesSet[tree.getInternalNode(i).getNumber()]!=null)
				right = rightNodesSet[tree.getInternalNode(i).getNumber()].size();
			sum+=Math.abs(Math.log(left/right)/Math.log(2));
		}
		return sum/tree.getInternalNodeCount();
	}
	
	
	/**
	 * Gets the tree weighted balance value.
	 *
	 * @param tree the tree
	 * @return the tree weighted balance value
	 */
	
	public static double getTreeWeightedBalanceValue(Tree tree) {
		double[] leftNodesWeight = new double[tree.getInternalNodeCount()];
		double[] rightNodesWeight = new double[tree.getInternalNodeCount()];

		for (int i = 0; i < tree.getExternalNodeCount(); i++) {
			Node node = tree.getExternalNode(i);
			Node idxNode = node.getParent();
			while (!node.isRoot()) {
				if (node.isLeaf()) {
					if (idxNode.getChild(0).equals(node)){
						leftNodesWeight[idxNode.getNumber()]=node.getBranchLength();
					}
					else if (idxNode.getChild(1).equals(node)){
						rightNodesWeight[idxNode.getNumber()]=node.getBranchLength();
					}
				} else {
					if (idxNode.getChild(0).equals(node)) {
						leftNodesWeight[idxNode.getNumber()] = node
								.getBranchLength()
								+ leftNodesWeight[node.getNumber()]
								/ 2
								+ rightNodesWeight[node.getNumber()] / 2;			
					} else if (idxNode.getChild(1).equals(node)) {
						rightNodesWeight[idxNode.getNumber()] = node
								.getBranchLength()
								+ leftNodesWeight[node.getNumber()]
								/ 2
								+ rightNodesWeight[node.getNumber()] / 2;	
					}
				}
				node = idxNode;
				idxNode = idxNode.getParent();
				if (node.equals(idxNode)){
					int j=0;
					j++;
				}
			}

		}
		
		double sum = 0;

		for (int i = 0; i < tree.getInternalNodeCount(); i++) {
			double left = 0;
			double right = 0;
			left = leftNodesWeight[tree.getInternalNode(i).getNumber()];
			right = rightNodesWeight[tree.getInternalNode(i).getNumber()];
			if (right == 0 && left == 0) {
				sum += 0.0;
			}else
				sum+=Math.abs(Math.log(left/right)/Math.log(2));
		}
		return sum/tree.getInternalNodeCount();
	}
	
	/**
	 * Gets the tree weighted balance value cascade.
	 *
	 * @param tree the tree
	 * @return the tree weighted balance value cascade
	 */
	public static double getTreeWeightedBalanceValueCascade(Tree tree) {
		double sum = getNodeWeightedBalanceValue(tree.getRoot());
		return sum;
	}
	
	
	/**
	 * Gets the node weighted balance value.
	 *
	 * @param node the node
	 * @return the node weighted balance value
	 */
	public static double getNodeWeightedBalanceValue(Node node){
		if (node.isLeaf()) 
			return node.getBranchLength();
		else{
			double sum = 0;
			sum += node.getBranchLength()+ getNodeWeightedBalanceValue(node.getChild(0))/2;
			sum += getNodeWeightedBalanceValue(node.getChild(1))/2;
			return sum;
		}			
	}
	
	
	


	/**
	 * Gets the tree root to leaf path lengths variance.
	 *
	 * @param tree the tree
	 * @return the tree root to leaf path lengths variance
	 */
	public static double getTreeRootToLeafPathLengthsVariance(Tree tree) {
		double[] pathLengths = new double[tree.getExternalNodeCount()];

		for (int i = 0; i < tree.getExternalNodeCount(); i++) {
			Node node = tree.getExternalNode(i);
			double pathlength = node.getBranchLength();
			Node idxNode = node.getParent();
			while (!node.isRoot()) {
				pathlength += idxNode.getBranchLength();
				node = idxNode;
				idxNode = idxNode.getParent();
			}
			pathLengths[node.getNumber()] = pathlength;
		}
		
		double mean = 0;

		for (int i = 0; i < tree.getExternalNodeCount(); i++) {
			mean+=pathLengths[i];
		}
		mean/=tree.getExternalNodeCount();
		
		double variance = 0;
		for (int i = 0; i < tree.getExternalNodeCount(); i++) {
			variance+=Math.pow((pathLengths[i]-mean),2);
		}
		return variance/(tree.getExternalNodeCount()-1);
	}
	
	
	/*public static double getNodeLeftChildNum(Node node){
		if (node.isLeaf())
			return 0;
		else if(node.getChildCount()>=1){
			return getNodeLeftChildNum(node.getChild(0))+getNodeRightChildNum(node.getChild(0));
		}
		
		
	}
	public static double getNodeRightChildNum(Node node){
		if (node.isLeaf())
			return 0;
		else if(node.getChildCount()==1)
			return 0;
		else if(node.getChildCount()>1){
			return getNodeLeftChildNum(node.getChild(1))+getNodeRightChildNum(node.getChild(1));
		}
		
	}*/

}
