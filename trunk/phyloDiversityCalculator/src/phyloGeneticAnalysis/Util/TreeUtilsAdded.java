/*
 * @author JingLiu
 * @version 1.0
 * @date Jan 7, 2013
 */

package phyloGeneticAnalysis.Util;

import pal.tree.Node;
import pal.tree.NodeUtils;
import pal.tree.Tree;

// TODO: Auto-generated Javadoc
/**
 * The Class TreeUtilsAdded.
 */
public class TreeUtilsAdded {

	/**
	 * Compute diversity of the given tree.
	 *
	 * @param tree the tree
	 * @return diversity diversity of the tree
	 */
	public static double computeDiversity(Tree tree) throws Exception{
		double div = 0;
		
		Node node = tree.getRoot();
		Node root = node;
		do {
			node = NodeUtils.postorderSuccessor(node);
			div = div+ node.getBranchLength();
		} while (node != root);

		return div;
	}

	

}
