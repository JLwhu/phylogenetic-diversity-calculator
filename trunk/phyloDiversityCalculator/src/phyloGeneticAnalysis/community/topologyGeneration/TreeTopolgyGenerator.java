package phyloGeneticAnalysis.community.topologyGeneration;

import java.io.PushbackReader;
import java.util.Random;

import pal.misc.Identifier;
import pal.tree.Node;
import pal.tree.NodeFactory;
import pal.tree.NodeUtils;
import pal.tree.SimpleTree;

public class TreeTopolgyGenerator  extends SimpleTree{
	private boolean uniformBranchLength = false;
	private double branchLength = 1.0;
	public TreeTopolgyGenerator(int size, double insertProb, double leftProb) {
		super();
	//	this.uniformBranchLength = uBL;
		
		constructTreeTopology(size, insertProb, leftProb);

		// node heights should be populated as well - AD
		NodeUtils.lengths2Heights(getRoot());

		createNodeList();
	}
	
	private void constructTreeTopology(int size, double insertProb, double leftProb){
	//	double branchLength = 1.0;
		if (size<=2) return;
		Random random = new Random(System.currentTimeMillis());	
		
		for (int i = 0; i < 2; i++) {
			Node newNode = NodeFactory.createNode();
			newNode.setBranchLength(branchLength);
			newNode.setIdentifier(new Identifier(String.valueOf(i + 1)));
			Node idxNode = getRoot();
			idxNode.addChild(newNode);
		}
		
		for (int i = 2; i < size; i++) {
			Node newNode = NodeFactory.createNode();
	//		newNode.setBranchLength(branchLength);
			newNode.setIdentifier(new Identifier(String.valueOf(i + 1)));

			double rand = random.nextDouble();
			Node idxNode = getRoot();
			boolean isdone = false;
			while (!idxNode.isLeaf()) {
				if (rand < insertProb) { // [0, insertProb), do insertion
					isdone = insertNode(idxNode, newNode);
					if (isdone)
						break;
				} else if (rand < leftProb + insertProb && rand >= insertProb) 
					// [insertProb,	 leftProb+insertProb), go to left branch
					idxNode = idxNode.getChild(0);
				else
					// [leftProb+insertProb, 1), go to right branch
					idxNode = idxNode.getChild(1);
				rand = random.nextDouble();
			}
			if (!isdone)
				insertNode(idxNode, newNode);
		}
	}
	
	private boolean insertNode(Node node, Node child) {
		if (!node.equals(getRoot())) {
			Node parent = node.getParent();
			Node newInterNode = NodeFactory.createNode();

			int childNum = parent.getChildCount();
			int i;
			for (i = 0; i < childNum; i++) {
				if (parent.getChild(i).equals(node))
					break;
			}
			parent.setChild(i, newInterNode);

			newInterNode.addChild(node);
			newInterNode.addChild(child);
			if (uniformBranchLength){
				newInterNode.setBranchLength(branchLength);
				child.setBranchLength(branchLength);				
			}else{
				newInterNode.setBranchLength(node.getBranchLength()/2);
				double oldBL = node.getBranchLength();
				node.setBranchLength(oldBL/2);
				double newBL = getBranchLengthSumToRoot(parent);
				newBL = 1 - newBL - oldBL/2;
				child.setBranchLength(newBL);
			}
			
		} else {
			if (node.getChildCount() < 2){
				node.addChild(child);
			}
			else
				return false;
		}
		return true;
	}
	
	private double getBranchLengthSumToRoot(Node node){
		double sum = 0;
		while(!node.equals(getRoot())){
			sum+=node.getBranchLength();
			node = node.getParent();
		}
		return sum;
	}
	
	public static void main(String[] args) {
		TreeTopolgyGenerator tree = new TreeTopolgyGenerator(10,
				0.002, 0.499);
		int i = 0;
		i++;
	}
}
