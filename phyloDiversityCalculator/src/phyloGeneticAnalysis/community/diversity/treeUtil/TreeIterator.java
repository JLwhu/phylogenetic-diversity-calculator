package phyloGeneticAnalysis.community.diversity.treeUtil;

import java.util.ArrayList;
import java.util.List;

import pal.tree.Node;
import pal.tree.Tree;

public class TreeIterator {
	private List<Node> nodelist;
	public void postOrderIterator(Node root){
		if (!root.isLeaf()) {
			for (int i = 0; i < root.getChildCount(); i++) {
				postOrderIterator(root.getChild(i));
			}
		}
		System.out.println(root.getIdentifier().getName());
		nodelist.add(root);
	}
	
	public void preOrderIterator(Node root){
		System.out.println(root.getIdentifier().getName());
		nodelist.add(root);
		if (!root.isLeaf()) {
			for (int i = 0; i < root.getChildCount(); i++) {
				preOrderIterator(root.getChild(i));
			}
		}
	}
	public List<Node> getPostOrderNodelist (Tree tree){
		nodelist = new ArrayList<Node>();
		postOrderIterator(tree.getRoot());
		return nodelist;
	}
	
	public List<Node> getPreOrderNodelist (Tree tree){
		nodelist = new ArrayList<Node>();
		preOrderIterator(tree.getRoot());
		return nodelist;
	}
	
	
	
}
