package phyloGeneticAnalysis.community.diversity.instanceUtil;

import pal.tree.Tree;
import weka.core.Instance;

public class InstanceSetUtils {
	public static int getNonZeroValuesNum(Instance first){
		int size =0;
		int total = first.numAttributes();
		for (int i = 0; i < total; i++) {
			if (first.value(i) != 0)
				size++;
		}
		return size;
	}
	
	public static int getIntersectionSize(Instance first, Instance second){
		int size =0;
		int total = first.numAttributes();
		for (int i = 0; i < total; i++) {
			if (first.value(i) != 0 && second.value(i)!=0)
				size++;
		}
		return size;
	}
	public static int getUnionSize(Instance first, Instance second){
		int size =0;
		int total = first.numAttributes();
		for (int i = 0; i < total; i++) {
			if (first.value(i) != 0 || second.value(i)!=0)
				size++;
		}
		return size;
	}
}
