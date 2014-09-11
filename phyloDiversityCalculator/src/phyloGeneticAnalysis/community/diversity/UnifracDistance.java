package phyloGeneticAnalysis.community.diversity;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;
import java.util.Random;
import java.util.Set;

import pal.tree.Node;
import pal.tree.SimpleTree;
import pal.tree.Tree;
import pal.tree.TreeUtils;
import phyloGeneticAnalysis.Util.RootedTreeSubtreeUtils;
import phyloGeneticAnalysis.community.diversity.instanceUtil.InstanceSetUtils;
import phyloGeneticAnalysis.community.diversity.statisticsUtil.IntersectionExpectedProb;
import phyloGeneticAnalysis.community.diversity.treeUtil.TreeIterator;
import phyloGeneticAnalysis.community.diversity.treeUtil.TreeUtilsMore;
import weka.core.Attribute;
import weka.core.FastVector;
import weka.core.Instance;
import weka.core.Instances;

// TODO: Auto-generated Javadoc
/**
 * The Class UnifracDistance.
 */
public class UnifracDistance extends CommunityDiversityDistanceStrategy {
	// option used to determine which variants of unifrac is going to be used
	/** The option. */
	private int option = UNIFRAC_VARIANTS.DU;
	private int numIteration = 100;

	/**
	 * The Class UNIFRAC_VARIANTS.
	 */
	public class UNIFRAC_VARIANTS {

		/** The Constant DU. */
		public static final int DU = 1;

		/** The Constant DW. */
		public static final int DW = 2;

		/** The Constant D0. */
		public static final int D0 = 3;

		/** The Constant DALPHA. */
		public static final int DALPHA = 4;

		/** The Constant DVAW. */
		public static final int DVAW = 5;
	}

	/** The expected value adjust. */
	protected boolean expectedValueAdjust = false;

	/** The max adjust value. */
	protected boolean maxAdjustValue = true;

	/** The alpha. */
	private double alpha = 0.5;

	// Used to store the distance results for speeding up
	/** The distance map. */
	private HashMap distanceMap = new HashMap();

	// Parameters used in the calculation of unifrac
	/** The branch set. */
	private Set[] branchSet;

	/** The asize. */
	private int asize;

	/** The bsize. */
	private int bsize = 0;

	/** The union size. */
	private int unionSize = 0;

	/**
	 * Instantiates a new unifrac distance.
	 * 
	 * @param phylotree
	 *            the phylotree
	 */
	public UnifracDistance(Tree phylotree) {
		super(phylotree);
	}

	public void setOption(int option) {
		this.option = option;
	}

	public void setAlpha(double alpha) {
		this.alpha = alpha;
	}

	@Override
	public double distance(Set<String> A, Set<String> B, HashMap abundanceMap) {

		Tree tree = this.getPhylogeneticTree();

		Instance instanceA = getInstanceFromNodeNameSet(tree, A, abundanceMap);
		Instance instanceB = getInstanceFromNodeNameSet(tree, B, abundanceMap);

		boolean isANonEmpty = false;
		boolean isBNonEmpty = false;
		for (int i = 0; i < instanceA.numAttributes(); i++) {
			if (instanceA.value(i) > 0) {
				isANonEmpty = true;
				break;
			}
		}
		for (int i = 0; i < instanceB.numAttributes(); i++) {
			if (instanceB.value(i) > 0) {
				isBNonEmpty = true;
				break;
			}
		}

		if (!isANonEmpty || !isBNonEmpty)
			return 0;

		// double unifrac = getTreeUnifrac(tree, instanceA, instanceB,option);
		// // subtree
		double unifrac = distance(instanceA, instanceB);

		return unifrac;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * phyloGeneticAnalysis.community.diversity.CommunityDiversityDistanceStrategy
	 * #distance(java.util.Set, java.util.Set)
	 */
	@Override
	public double distance(Set<String> A, Set<String> B) {
		Tree tree = this.getPhylogeneticTree();

		if (A.isEmpty() || B.isEmpty())
			return 0;

		Instance instanceA = getInstanceFromNodeNameSet(tree, A);
		Instance instanceB = getInstanceFromNodeNameSet(tree, B);

		// double unifrac = getTreeUnifrac(tree, instanceA, instanceB,option);
		// // subtree
		double unifrac = distance(instanceA, instanceB);

		return unifrac;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * phyloGeneticAnalysis.community.diversity.CommunityDiversityDistanceStrategy
	 * #distance(weka.core.Instance, weka.core.Instance)
	 */
	@Override
	public double distance(Instance first, Instance second) {

		boolean isANonEmpty = false;
		boolean isBNonEmpty = false;
		for (int i = 0; i < first.numAttributes(); i++) {
			if (first.value(i) > 0) {
				isANonEmpty = true;
				break;
			}
		}
		for (int i = 0; i < second.numAttributes(); i++) {
			if (second.value(i) > 0) {
				isBNonEmpty = true;
				break;
			}
		}

		if (!isANonEmpty || !isBNonEmpty)
			return 0;
		
		double unifrac = 0;
		try {
			Tree tree = this.getPhylogeneticTree();

			double expectBound = 1;
			if (expectedValueAdjust) {

				expectBound = calculateExpectedBound(first, second);

				// System.out.println(expectBound);
			}

			// if (distanceMap.get(first) == null && distanceMap.get(second) ==
			// null) {
			unifrac = getTreeUnifrac(tree, first, second, option);
			// unifrac = 1;
			if (expectedValueAdjust)
				unifrac /= expectBound;
			// HashMap instanceMap = new HashMap();
			// instanceMap.put(second, unifrac);
			// distanceMap.put(first, instanceMap);
			/*
			 * } else if (distanceMap.get(first) != null) { HashMap instanceMap
			 * = (HashMap) distanceMap.get(first); if (instanceMap.get(second)
			 * != null) unifrac = (double) instanceMap.get(second); // unifrac =
			 * 1; else { unifrac = getTreeUnifrac(tree, first, second, option);
			 * // unifrac = 1; if (expectedValueAdjust) unifrac /= expectBound;
			 * instanceMap.put(second, unifrac); } } else if
			 * (distanceMap.get(second) != null) { HashMap instanceMap =
			 * (HashMap) distanceMap.get(second); if (instanceMap.get(first) !=
			 * null) unifrac = (double) instanceMap.get(first); // unifrac = 1;
			 * else { unifrac = getTreeUnifrac(tree, first, second, option); //
			 * unifrac = 1; if (expectedValueAdjust) unifrac /= expectBound;
			 * instanceMap.put(first, unifrac); } }
			 */
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return unifrac;
	}

	/**
	 * Gets the tree unifrac.
	 * 
	 * @param tree
	 *            the tree
	 * @param instanceA
	 *            the instance a
	 * @param instanceB
	 *            the instance b
	 * @param option
	 *            the option
	 * @return the tree unifrac
	 */
	private double getTreeUnifrac(Tree tree, Instance instanceA,
			Instance instanceB, int option) throws Exception {
		// long time = 0;
		// time = System.currentTimeMillis();

		preProcess(tree, instanceA, instanceB);

		int internalCount = tree.getInternalNodeCount();
		int externalCount = tree.getExternalNodeCount();

		double sum = 0;
		double distanceScaleFactor = 0;
		for (int i = 0; i < branchSet.length; i++) {
			double nodesum = 0;
			if (branchSet[i] != null) {
				double branchlength = 0;
				if (i < externalCount)
					branchlength = tree.getExternalNode(i).getBranchLength();
				else
					branchlength = tree.getInternalNode(i - externalCount)
							.getBranchLength();

				double avalue = 0;
				double bvalue = 0;
				Set nodeset = branchSet[i];
				Iterator it = nodeset.iterator();
				while (it.hasNext()) {
					Node node = (Node) it.next();
					if (instanceA.value(node.getNumber()) != 0)
						avalue += instanceA.value(node.getNumber());
					if (instanceB.value(node.getNumber()) != 0)
						bvalue += instanceB.value(node.getNumber());
				}
				avalue /= asize;
				bvalue /= bsize;

				switch (option) {
				case UNIFRAC_VARIANTS.DU:
					if (avalue > 0)
						avalue = 1;
					if (bvalue > 0)
						bvalue = 1;
					nodesum = (avalue - bvalue) * branchlength;
					nodesum = Math.abs(nodesum);
					distanceScaleFactor += branchlength;
					break;
				case UNIFRAC_VARIANTS.DW:
					nodesum = (avalue - bvalue) * branchlength;
					nodesum = Math.abs(nodesum);
					distanceScaleFactor += (avalue + bvalue) * branchlength;
					break;
				case UNIFRAC_VARIANTS.D0:
					nodesum = (avalue - bvalue) / (avalue + bvalue)
							* branchlength;
					nodesum = Math.abs(nodesum);
					distanceScaleFactor += branchlength;
					break;
				case UNIFRAC_VARIANTS.DALPHA:
					nodesum = (avalue - bvalue) / (avalue + bvalue)
							* branchlength * Math.pow(avalue + bvalue, alpha);
					nodesum = Math.abs(nodesum);
					distanceScaleFactor += branchlength
							* Math.pow(avalue + bvalue, alpha);
					break;
				case UNIFRAC_VARIANTS.DVAW: // ??? this part need consideration:
											// /(unionSize*(unionSize-branchSet[i].size()))
					nodesum = (avalue - bvalue) * branchlength
							/ (unionSize * (unionSize - branchSet[i].size()));
					nodesum = Math.abs(nodesum);
					distanceScaleFactor += (avalue + bvalue) * branchlength
							/ (unionSize * (unionSize - branchSet[i].size()));
					break;
				}
				sum += nodesum;
			}
		}

		// System.out.print("internal nodes:"+String.valueOf(System.currentTimeMillis()-time)+"\r\n");
		// time = System.currentTimeMillis();
		return sum / distanceScaleFactor;
	}

	/*
	 * Go up the tree to the root from the leaf nodes of each community and
	 * label all the nodes along the path.
	 */
	/**
	 * Populate labels.
	 * 
	 * @param tree
	 *            the tree
	 * @param instanceA
	 *            the instance a
	 * @param instanceB
	 *            the instance b
	 */
	private void populateLabels(Tree tree, Instance instanceA,
			Instance instanceB) throws Exception {
		int internalCount = tree.getInternalNodeCount();
		int externalCount = tree.getExternalNodeCount();
		Node root = tree.getRoot();
		asize = 0;
		bsize = 0;
		unionSize = 0;
		branchSet = new Set[internalCount + externalCount];
		for (int i = 0; i < instanceA.numValues(); i++) {
			if (instanceA.value(i) > 0 && instanceB.value(i) > 0)
				unionSize--;
			if (instanceA.value(i) > 0) {
				asize++;
				Node node = tree.getExternalNode(i);
				Set nodeset = new HashSet();
				nodeset.add(node);
				branchSet[i] = nodeset;
				Node idxNode = node.getParent();
				if (idxNode != null)
					while (idxNode != root) {
						if (branchSet[idxNode.getNumber() + externalCount] == null) { //
							nodeset = new HashSet();
							nodeset.add(node);
							branchSet[idxNode.getNumber() + externalCount] = nodeset;
							idxNode = idxNode.getParent();
						} else {
							nodeset = branchSet[idxNode.getNumber()
									+ externalCount];
							nodeset.add(node);
							idxNode = idxNode.getParent();
						}
					}
			}
			if (instanceB.value(i) > 0) {
				bsize++;
				Node node = tree.getExternalNode(i);
				Set nodeset = new HashSet();
				nodeset.add(node);
				branchSet[i] = nodeset;
				Node idxNode = node.getParent();
				if (idxNode != null)
					while (idxNode != root) {
						if (branchSet[idxNode.getNumber() + externalCount] == null) { //
							nodeset = new HashSet();
							nodeset.add(node);
							branchSet[idxNode.getNumber() + externalCount] = nodeset;
							idxNode = idxNode.getParent();
						} else {
							nodeset = branchSet[idxNode.getNumber()
									+ externalCount];
							nodeset.add(node);
							idxNode = idxNode.getParent();
						}
					}
			}
		}

	}

	/*
	 * Find the lowest common ancester node of the two communities. Delete the
	 * labels of the nodes on the path from the lowest common ancester to the
	 * root
	 */
	/**
	 * Delete redundent tree.
	 * 
	 * @param tree
	 *            the tree
	 * @param instanceA
	 *            the instance a
	 * @param instanceB
	 *            the instance b
	 */
	private void deleteRedundentTree(Tree tree, Instance instanceA,
			Instance instanceB) throws Exception {
		int internalCount = tree.getInternalNodeCount();
		int externalCount = tree.getExternalNodeCount();
		Node root = tree.getRoot();
		Node nearestCommonAncester = root;
		unionSize = unionSize + asize + bsize;
		do {
			Node leftchild = nearestCommonAncester.getChild(0);
			Node rightchild = nearestCommonAncester.getChild(1);
			Set nodesetleft = null;
			if (leftchild.isLeaf())
				nodesetleft = branchSet[leftchild.getNumber()];
			else
				nodesetleft = branchSet[leftchild.getNumber() + externalCount];
			Set nodesetright = null;
			if (rightchild.isLeaf())
				nodesetright = branchSet[rightchild.getNumber()];
			else
				nodesetright = branchSet[rightchild.getNumber() + externalCount];
			if (nodesetleft != null) {
				if (nodesetleft.size() < unionSize) {
					// nearestCommonAncester = leftchild.getParent();
					branchSet[nearestCommonAncester.getNumber() + externalCount] = null;
					break;
				} else if (nodesetleft.size() == unionSize) {
					// if (!nearestCommonAncester.isLeaf()) {
					branchSet[nearestCommonAncester.getNumber() + externalCount] = null;
					nearestCommonAncester = leftchild;
					// }
				}
			} else if (nodesetright != null) {
				if (nodesetright.size() < unionSize) {
					// nearestCommonAncester = rightchild.getParent();
					branchSet[nearestCommonAncester.getNumber() + externalCount] = null;
					break;
				} else if (nodesetright.size() == unionSize) {
					branchSet[nearestCommonAncester.getNumber() + externalCount] = null;
					nearestCommonAncester = rightchild;
				}
			}
		} while (!nearestCommonAncester.isLeaf());
	}

	/**
	 * Pre process.
	 * 
	 * @param tree
	 *            the tree
	 * @param instanceA
	 *            the instance a
	 * @param instanceB
	 *            the instance b
	 */
	private void preProcess(Tree tree, Instance instanceA, Instance instanceB)
			throws Exception {
		populateLabels(tree, instanceA, instanceB);
		deleteRedundentTree(tree, instanceA, instanceB);
	}

	/**
	 * Calculate the expected bound.
	 * 
	 * @param first
	 *            the first
	 * @param second
	 *            the second
	 * @return the expected bound
	 */
	private double calculateExpectedBound(Instance first, Instance second)
			throws Exception {
		IntersectionExpectedProb iep = new IntersectionExpectedProb();
		int unionSize = InstanceSetUtils.getUnionSize(first, second);
		int smallSetSize = InstanceSetUtils.getNonZeroValuesNum(first);
		int largeSetSize = InstanceSetUtils.getNonZeroValuesNum(second);

		if (smallSetSize > largeSetSize) {
			int temp = smallSetSize;
			smallSetSize = largeSetSize;
			largeSetSize = temp;
		}

		double sum = 0;
		int startidx = largeSetSize + smallSetSize - unionSize;
		if (startidx <= 0)
			startidx = 1;

		double[] sums;
		if (maxAdjustValue)
			sums = getOrderedBranchesSum(first, second);
		else
			sums = getRandomOrderBranchesSum(first, second);

		double[] funcValues = new double[smallSetSize + 1];
		for (int i = startidx; i <= smallSetSize; i++) {
			funcValues[i] = sums[2 * i];
		}
		double expectBound = iep.expectedProb(first, second, funcValues);

		for (int i = startidx; i <= smallSetSize; i++) {
			funcValues[i] = i;
		}
		double normalizer = iep.expectedProb(first, second, funcValues);
		expectBound /= (2 * unionSize - 1);
		// expectBound /= normalizer;

		return expectBound;
	}

	/**
	 * Gets the ordered branches sum.
	 * 
	 * @param first
	 *            the first
	 * @param second
	 *            the second
	 * @return the ordered branches sum
	 */
	private double[] getOrderedBranchesSum(Instance first, Instance second)
			throws Exception {
		Tree tree = this.getPhylogeneticTree();
		double[] branches = TreeUtilsMore.getSortedBranchWeight(tree);
		double[] sums = new double[branches.length];
		double sum = 0;
		for (int i = 0; i < branches.length; i++) {
			sum += branches[branches.length - i - 1];
			sums[branches.length - i - 1] = sum;
		}
		return sums;
	}

	/**
	 * Gets the random order branches sum.
	 * 
	 * @param first
	 *            the first
	 * @param second
	 *            the second
	 * @return the random order branches sum
	 */
	private double[] getRandomOrderBranchesSum(Instance first, Instance second)
			throws Exception {
		Tree tree = this.getPhylogeneticTree();
		double[] branches = TreeUtilsMore.getBranchWeight(tree);
		List branchWeightList = new ArrayList();
		for (int i = 0; i < branches.length; i++) {
			branchWeightList.add(branches[i]);
		}
		long seed = System.nanoTime();
		Collections.shuffle(branchWeightList, new Random(seed));
		for (int i = 0; i < branches.length; i++) {
			branches[i] = ((Double) branchWeightList.get(i)).doubleValue();
		}
		double[] sums = new double[branches.length];
		double sum = 0;
		for (int i = 0; i < branches.length; i++) {
			sum += branches[branches.length - i - 1];
			sums[branches.length - i - 1] = sum;
		}
		return sums;
	}

	// @Override
	public double distance1(Instance first, Instance second) throws Exception {// zscoreDistance

		boolean isANonEmpty = false;
		boolean isBNonEmpty = false;
		for (int i = 0; i < first.numAttributes(); i++) {
			if (first.value(i) > 0) {
				isANonEmpty = true;
				break;
			}
		}
		for (int i = 0; i < second.numAttributes(); i++) {
			if (second.value(i) > 0) {
				isBNonEmpty = true;
				break;
			}
		}

		if (!isANonEmpty || !isBNonEmpty)
			return 0;

		double distance = distance1(first, second);
		;
		double mean = 0;
		double std = 0;
		double[] distanceVec = new double[this.numIteration];
		Instance copyFirst = new Instance(first);
		Instance copySecond = new Instance(second);
		long seed = System.nanoTime();
		Random random = new Random(seed);
		for (int i = 0; i < this.numIteration; i++) {
			shuffleInstance(copyFirst, random);
			shuffleInstance(copySecond, random);
			distanceVec[i] = distance1(copyFirst, copySecond);
			// System.out.println(distanceVec[i]);
		}
		for (int i = 0; i < this.numIteration; i++) {
			mean += distanceVec[i];
		}
		mean /= this.numIteration;
		// System.out.println(distance);
		// System.out.println(mean);
		for (int i = 0; i < this.numIteration; i++) {
			std += Math.pow(distanceVec[i] - mean, 2);
		}
		std /= this.numIteration;
		std = Math.sqrt(std);
		// System.out.println(std);
		System.out.println((distance - mean) / std);
		return (distance - mean) / std;
	}

	private void shuffleInstance(Instance instance, Random random)
			throws Exception {
		List list = new ArrayList();
		for (int j = 0; j < instance.numValues(); j++) {
			if (instance.value(j) != 0)
				list.add(instance.value(j));
		}
		Collections.shuffle(list, random);
		// System.out.println(list.size());
		for (int j = 0; j < instance.numValues(); j++) {
			instance.setValue(j, 0);
		}
		for (int j = 0; j < list.size(); j++) {
			int pos = (int) Math.round(random.nextDouble()
					* (instance.numValues() - 1));
			instance.setValue(pos, ((Double) list.get(j)).doubleValue());
		}
	}

}
