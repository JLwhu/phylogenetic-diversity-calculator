/*
 * @author JingLiu
 * @version 1.0
 * @date Jan 7, 2013
 */



package phyloGeneticAnalysis.Util;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import pal.tree.Node;

import pal.tree.Tree;
import pal.tree.TreeUtils;


// TODO: Auto-generated Javadoc
/**
 * The Class RootedTreeSubtreeUtils.
 */
public class RootedTreeSubtreeUtils {
	
	/**
	 * Gets the specified sub tree.
	 *
	 * @param tree the tree
	 * @param nameList the name list
	 * @return the specified sub tree
	 */
	public Tree getSpecifiedSubTree(Tree tree, List nameList) throws Exception{
		Tree subtree = null;
		int i,count;

		//Hashtable nametable = new Hashtable();
		//RootedTreeUtils.collectTaxa(tree.getRoot(),nametable);

		List<Node> nodeList = new ArrayList<Node>();
		Node node;
		String nodename;
		
		i = 0;
		count = nameList.size();
		
		subtree = tree.getCopy();

		while (i<count){
			node = TreeUtils.getNodeByName(subtree.getRoot(),(String)nameList.get(i));
			if (node!=null) nodeList.add(node);
			i++;
		}

		for (i=0;i<tree.getExternalNodeCount();i++){
			Node remNode = subtree.getExternalNode(i);
			if (nodeList.indexOf(remNode)==-1){
				removeLeafNode(subtree,remNode);
			}
		}
		subtree.createNodeList();
		for (i=0;i<subtree.getExternalNodeCount();i++){
			Node remNode = subtree.getExternalNode(i);
			removeSingleChildInternalNode(subtree,remNode);
	
		}
		subtree.createNodeList();
	//	System.out.println("SubTree Size:"+subtree.getExternalNodeCount());
		return subtree;
		
	}
	
	/**
	 * Gets the specified sub tree.
	 *
	 * @param tree the tree
	 * @param NodeSet the Node name Set
	 * @return the specified sub tree
	 */
	public Tree getSpecifiedSubTree(Tree tree, Set<String> nameSet)  throws Exception{
		Tree subtree = null;

		List<String> namelist = new ArrayList<String>();
		for (int i=0;i<tree.getExternalNodeCount();i++){
			String nodeName = tree.getExternalNode(i).getIdentifier().getName();
			if (nameSet.contains(nodeName))
				namelist.add(nodeName);
		}
		subtree = getSpecifiedSubTree(tree, namelist);
		return subtree;
		
	}
	
	/**
	 * Gets the specified sub tree.
	 *
	 * @param tree the tree
	 * @param IDXSet the index Set
	 * @return the specified sub tree
	 */
	public Tree getSpecifiedSubTreeByIDXSet(Tree tree, Set<Integer> IDXSet)  throws Exception{
		Tree subtree = null;

		List<String> namelist = new ArrayList<String>();
		for (int i=0;i<tree.getExternalNodeCount();i++){
			Node node = subtree.getExternalNode(i);
			if (IDXSet.contains(node.getNumber()))
				namelist.add(node.getIdentifier().getName());
		}
		subtree = getSpecifiedSubTree(tree, namelist);
		return subtree;
		
	}
	
	/**
	 * Gets the specified sub tree.
	 *
	 * @param tree the tree
	 * @param NodeSet the Node Set
	 * @return the specified sub tree
	 */
	public Tree getSpecifiedSubTreeByNodeSet(Tree tree, Set<Node> NodeSet)  throws Exception{
		Tree subtree = null;

		List<String> namelist = new ArrayList<String>();
		for (int i=0;i<tree.getExternalNodeCount();i++){
			Node node = tree.getExternalNode(i);
			if (NodeSet.contains(node))
				namelist.add(node.getIdentifier().getName());
		}
		subtree = getSpecifiedSubTree(tree, namelist);
		return subtree;
		
	}	

	
	/**
	 * Removes the leaf node. Remove the parent of the node if it contains no child anymore.
	 *
	 * @param tree the tree
	 * @param node the node to be removed.
	 */
	public void removeLeafNode(Tree tree, Node node) {
		Node parent;
		int i;

		if (!node.isRoot()) {
			parent = node.getParent();
			for (i = 0; i < parent.getChildCount(); i++) {
				if (node.equals(parent.getChild(i))){
					parent.removeChild(i);
					node.setParent(null);
				}
			}

			if (parent.getChildCount() == 0) {
				removeLeafNode(tree, parent);
			}
		} else {
			Node newRoot = node.getChild(0);
			tree.setRoot(newRoot);
		}

	}
	
	/**
	 * Removes the single child internal node. Process is started from the leaf node upward towards the root.
	 * All internal nodes along the way with only one child node are removed. Branch length are updated.
	 *
	 * @param tree the tree
	 * @param node the external leaf node to start the remove process with
	 */
	public void removeSingleChildInternalNode(Tree tree, Node node)  throws Exception{
		Node parent,parparent;
		int i;
		
		if (!node.isRoot()) {
			parent = node.getParent();
			if (!parent.isRoot()) {
				parparent = parent.getParent();
				if (parent.getChildCount() == 1) {
					node.setParent(parparent);
					node.setBranchLength(node.getBranchLength()+parent.getBranchLength());
					for (i = 0; i < parparent.getChildCount(); i++) {
						if (parent.equals(parparent.getChild(i)))
							parparent.setChild(i, node);
					}
					parent.setParent(null);
					removeSingleChildInternalNode(tree, node);
				} else  {//parent have more than one child, go up
					removeSingleChildInternalNode(tree, parent);
				}
			}else if (parent.getChildCount()==1){  //root has only one child, remove it and make a new root
				tree.setRoot(node);
				node.setParent(null);
			}
		}
			
	}

}
