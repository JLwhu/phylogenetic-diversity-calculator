package phyloGeneticAnalysis.community.diversity;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;

import pal.tree.Node;
import pal.tree.Tree;
import phyloGeneticAnalysis.community.math.Combination;
import weka.core.Instance;

public class SpeciesBetaDiversityDistanceExpectedFrac extends CommunityDiversityDistanceStrategy{
	public SpeciesBetaDiversityDistanceExpectedFrac(Tree phylotree) {
		super(phylotree);
		// TODO Auto-generated constructor stub
	}

	@Override
	public double distance(Set<String> A, Set<String> B) {
		// TODO Auto-generated method stub
		Set intersection = new HashSet<Node>();
		
		intersection.clear();
		intersection.addAll(A);
		intersection.retainAll(B);
		int interSize = intersection.size();
		Tree phylotree = super.getPhylogeneticTree();
		int total = phylotree.getExternalNodeCount();
		Set union = new HashSet<Node>();
		union.clear();
		union.addAll(A);
		union.addAll(B);
		int unionSize = union.size();
		int smallSetSize;
		int largeSetSize;
		if (A.size()>B.size()){
			largeSetSize = A.size();
			smallSetSize =  B.size();
		}else{
			largeSetSize = B.size();
			smallSetSize =  A.size();
		}
		
		double sum =0;
		int startidx = largeSetSize+smallSetSize-unionSize;
		if (startidx<=0) startidx = 1;
		for (int i=startidx;i<=smallSetSize;i++){
			double combin = Combination.getCnm(largeSetSize, i) * Combination.getCnm(unionSize-largeSetSize, smallSetSize-i);
			sum = sum +i
					* combin;
		}
		
		double denominator = Combination.getCnm(unionSize, smallSetSize);
		
		double ret = sum/denominator;		
		ret = interSize/ret;

		return ret;
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
