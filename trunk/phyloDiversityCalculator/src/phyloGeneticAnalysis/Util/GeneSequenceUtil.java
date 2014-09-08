package phyloGeneticAnalysis.Util;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

import pal.tree.Tree;

public class GeneSequenceUtil {
	public HashMap getSpecifiedSpeciesNameToSequenceMap(HashMap sequenceMap, List nameList) {
		HashMap subSeqMap = new HashMap();
		Iterator it = nameList.iterator();
		while (it.hasNext()) {
			String speciesName = (String) it.next();
			if (sequenceMap.containsKey(speciesName)) {
				String sequence = (String) sequenceMap.get(speciesName);
				subSeqMap.put(speciesName, sequence);
			}
		}
		return subSeqMap;
	}
	
	
	public List getSpecifiedSpeiceNameToGeneIDMap(List speciesToSequenceIDList, List nameList) {
		ArrayList speList = (ArrayList) speciesToSequenceIDList.get(0);
		ArrayList geneNameList = (ArrayList) speciesToSequenceIDList.get(1);
		HashMap speToGeneIDMap = (HashMap) speciesToSequenceIDList.get(2);
		
		ArrayList subSpeList = new ArrayList();
		HashMap subSpeToGeneIDMap = new HashMap();
		Iterator it = nameList.iterator();
		while (it.hasNext()) {
			String speciesName = (String) it.next();
			if (speList.contains(speciesName)) {
				subSpeList.add(speciesName);
				HashMap geneNameToIDMap = (HashMap) speToGeneIDMap.get(speciesName);
				subSpeToGeneIDMap.put(speciesName, geneNameToIDMap);
			}
		}		
		
		ArrayList resultList = new ArrayList();
		resultList.add(subSpeList);
		resultList.add(geneNameList);
		resultList.add(subSpeToGeneIDMap);
		return resultList;
	}
}
