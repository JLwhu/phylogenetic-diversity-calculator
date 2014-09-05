package phyloGeneticAnalysis.statistics;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import weka.core.Instance;
import weka.core.Instances;

public class InstancesStatistics {
	public HashMap distinctMemberNumber(Instances instances){
		HashMap distinctInstanceMap = new HashMap();
		for (int i=0;i<instances.numInstances();i++){
			Set<Instance> instanceSet = distinctInstanceMap.keySet();
			Iterator it = instanceSet.iterator();
			boolean exist = false;
			while (it.hasNext()){
				Instance curinstance = (Instance) it.next();
				if (equals(curinstance, instances.instance(i))){
					int value = ((Integer) distinctInstanceMap.get(curinstance)).intValue();
					distinctInstanceMap.put(curinstance, value+1);
					exist = true;
					break;
				}
			}			
			if  (!exist) {
				distinctInstanceMap.put(instances.instance(i),1);
			}
		}
		
		return distinctInstanceMap;
	}
	private boolean equals(Instance A, Instance B){
		if (A.numAttributes()!=B.numAttributes())
			return false;
		for (int i = 0; i < A.numAttributes(); i++) {
			if (A.value(i)!=B.value(i))
				return false;				
		}
		return true;
	}
}
