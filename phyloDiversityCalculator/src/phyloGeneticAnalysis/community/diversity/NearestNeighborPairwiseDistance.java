package phyloGeneticAnalysis.community.diversity;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Set;

import pal.tree.Node;
import pal.tree.Tree;
import pal.tree.TreeUtils;
import weka.core.Instance;

public class NearestNeighborPairwiseDistance  extends CommunityDiversityDistanceStrategy{

	public NearestNeighborPairwiseDistance(Tree phylotree) {
		super(phylotree);
		// TODO Auto-generated constructor stub
	}

	@Override
	public double distance(Set<String> A,
			Set<String> B) {
		// TODO Auto-generated method stub
		double sum = 0;
		Tree tree = this.getPhylogeneticTree();
		
		Set<Node> nodesetA = getNodeSetFromNameSet(tree, A);
		Set<Node> nodesetB = getNodeSetFromNameSet(tree, B);
		
		sum = distanceNodeSets(nodesetA,nodesetB);
		return sum;
	}

	@Override
	public double distance(Instance first, Instance second) {
		double sum = 0;
		Tree tree = this.getPhylogeneticTree();
		
		Set<Node> nodesetA = getNodeSetFromInstance(tree, first);
		Set<Node> nodesetB = getNodeSetFromInstance(tree, second);
		
		sum = distanceNodeSets(nodesetA,nodesetB);
		return sum;
	}
	
	public double distanceNodeSets(Set<Node> nodesetA, Set<Node> nodesetB) {
		double sum = 0;
		Tree tree = this.getPhylogeneticTree();
		
		double sumA = 0;
		double sumB= 0;
		double[][] distanceMatrix = new double[tree.getExternalNodeCount()][] ;
		Iterator itA = nodesetA.iterator();
		int i=0;
		double[] dist;
		while (itA.hasNext()){
			Node nodeA = (Node) itA.next();
	//		System.out.println(nodeA.getIdentifier().getName());
	//		System.out.println(tree.getExternalNode(nodeA.getNumber()).getIdentifier().getName());
			dist = new double[tree.getExternalNodeCount()];
			double[] idist = new double[tree.getInternalNodeCount()];
			TreeUtils.computeAllDistances(tree, nodeA.getNumber(), dist, idist, false, 0.0001);			
			distanceMatrix[nodeA.getNumber()] = dist;
			i++;
			Iterator itB = nodesetB.iterator();
			double distsum = 0;
			while (itB.hasNext()) {
				Node nodeB = (Node) itB.next();
				distsum += dist[nodeB.getNumber()];
			}
			sumA += distsum/nodesetB.size();			
		}
		
		Iterator itB = nodesetB.iterator();
		while (itB.hasNext()){
			Node nodeB = (Node) itB.next();	
			Iterator it = nodesetA.iterator();
			double distsum = 0;
			while (it.hasNext()) {
				Node nodeA = (Node) it.next();
				distsum += distanceMatrix[nodeA.getNumber()][nodeB.getNumber()];
			}
			sumB += distsum/nodesetA.size();			
		}		
		
		sum = (sumA + sumB)/2;
		return sum;
	}

	@Override
	public double distance(Set<String> A, Set<String> B, HashMap abundanceMap) {
		// TODO Auto-generated method stub
		return 0;
	}

}
