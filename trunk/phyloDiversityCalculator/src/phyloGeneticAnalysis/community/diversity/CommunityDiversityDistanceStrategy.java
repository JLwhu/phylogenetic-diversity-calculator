package phyloGeneticAnalysis.community.diversity;

import java.util.Enumeration;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

import pal.tree.Node;
import pal.tree.Tree;
import weka.core.DistanceFunction;
import weka.core.Instance;
import weka.core.Instances;
import weka.core.TechnicalInformation;
import weka.core.TechnicalInformationHandler;
import weka.core.neighboursearch.PerformanceStats;

public abstract class CommunityDiversityDistanceStrategy implements DistanceFunction, Cloneable,
TechnicalInformationHandler{
	private Tree phylotree;
	public Tree getPhylogeneticTree(){
		return phylotree;
	}
	private void setPhylogeneticTree(Tree phylotree){
		this.phylotree = phylotree;
	}

	CommunityDiversityDistanceStrategy(Tree phylotree){
		setPhylogeneticTree(phylotree);
	}
	abstract public double distance(Set<String> A, Set<String> B) throws Exception;
	
	abstract public double distance(Instance first, Instance second);
	
	abstract public double distance(Set<String> A, Set<String> B, HashMap abundanceMap) throws Exception;

	protected Set<Node> getNodeSetFromNameSet(Tree tree, Set<String> nameSet){		
		Set<Node> nodeSet = new HashSet<Node>();
		
		Iterator it = nameSet.iterator();
		while (it.hasNext()){
			String nameString = (String) it.next();
			int extCount = tree.getExternalNodeCount();
			for (int i=0;i<extCount;i++){
				if (tree.getExternalNode(i).getIdentifier().getName().equals(nameString)) {
					nodeSet.add(tree.getExternalNode(i));
					break;
				}
			}
		}
		
		return nodeSet;
	}
	
	protected Set<Node> getNodeSetFromInstance(Tree tree, Instance instance){		
		Set<Node> nodeSet = new HashSet<Node>();
		for (int i=0;i<instance.numAttributes();i++){
			if (instance.value(i)>0)
				nodeSet.add(tree.getExternalNode(i));
		}		
		return nodeSet;
	}
	
	protected Set<String> getNodeNameSetFromInstance(Tree tree, Instance instance){		
		Set<String> nodeNameSet = new HashSet<String>();
		for (int i=0;i<instance.numAttributes();i++){
			if (instance.value(i)>0)
				nodeNameSet.add(tree.getExternalNode(i).getIdentifier().getName());
		}		
		return nodeNameSet;
	}
	
	protected Instance getInstanceFromNodeNameSet(Tree tree, Set<String> nodeNameSet){
		Set<Node> nodeSet = getNodeSetFromNameSet(tree, nodeNameSet);
		Instance instance = getInstanceFromNodeSet(tree, nodeSet);
		return instance;
	}
	
	protected Instance getInstanceFromNodeNameSet(Tree tree, Set<String> nodeNameSet, HashMap valueMap){
		Set<Node> nodeSet = getNodeSetFromNameSet(tree, nodeNameSet);
		Instance instance = getInstanceFromNodeSet(tree, nodeSet, valueMap);
		return instance;
	}
	
	protected Instance getInstanceFromNodeSet(Tree tree, Set<Node> nodeSet){
		/*double weight =1;
		int speciesCount = tree.getExternalNodeCount();
		double[] attValue = new double[speciesCount];
		for (int j = 0; j < speciesCount; j++) {
			if (nodeSet.contains(tree.getExternalNode(j)))
				attValue[j] = 1;
		}
		Instance instance = new Instance(weight, attValue);*/
		Instance instance = getInstanceFromNodeSet(tree, nodeSet, null);
		return instance;
	}
	
	protected Instance getInstanceFromNodeSet(Tree tree, Set<Node> nodeSet, HashMap valueMap){
		double weight =1;
		int speciesCount = tree.getExternalNodeCount();
		double[] attValue = new double[speciesCount];
		for (int j = 0; j < speciesCount; j++) {
			if (nodeSet.contains(tree.getExternalNode(j)))
				if (valueMap!=null && valueMap.size()>0){
					String speciesName = tree.getExternalNode(j).getIdentifier().getName();
					if (valueMap.containsKey(speciesName))
						attValue[j] = Double.valueOf((String)valueMap.get(speciesName));
					else
						attValue[j] = 1;
				}else
					attValue[j] = 1;
		}
		Instance instance = new Instance(weight, attValue);
		return instance;
	}
	
	@Override
	public Enumeration listOptions() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void setOptions(String[] options) throws Exception {
		// TODO Auto-generated method stub
		
	}

	@Override
	public String[] getOptions() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public TechnicalInformation getTechnicalInformation() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void setInstances(Instances insts) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public Instances getInstances() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void setAttributeIndices(String value) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public String getAttributeIndices() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void setInvertSelection(boolean value) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public boolean getInvertSelection() {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public double distance(Instance first, Instance second,
			PerformanceStats stats) throws Exception {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public double distance(Instance first, Instance second, double cutOffValue) {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public double distance(Instance first, Instance second, double cutOffValue,
			PerformanceStats stats) {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public void postProcessDistances(double[] distances) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void update(Instance ins) {
		// TODO Auto-generated method stub
		
	}
}
