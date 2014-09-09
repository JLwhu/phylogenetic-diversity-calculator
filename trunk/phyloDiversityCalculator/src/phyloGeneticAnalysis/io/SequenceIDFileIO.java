package phyloGeneticAnalysis.io;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import pal.tree.Tree;
import weka.core.Instance;

public class SequenceIDFileIO extends Parser{
	public List readSequenceIDFile(String filename) throws IOException {
		List result = new ArrayList();
		List speList = new ArrayList();
		List geneNameList = new ArrayList();
		HashMap speToGeneIDMap = new HashMap();
		FileReader file = null;

		file = new FileReader(filename);
		BufferedReader reader = new BufferedReader(file);

		String line = "";
		line = reader.readLine();
		int idx = 0;
		while (idx< line.length()){
			int startIdx = idx;
			int endIdx = idx;
			String geneName ="";
			startIdx = skipBlanks(idx, line);
			endIdx = getNextWordEndidx(startIdx, line);
			geneName = line.substring(startIdx,endIdx);
			geneNameList.add(geneName);
			idx = endIdx+1;
		}
		
		line = reader.readLine();
		while (line != null && line != "") { // && i<10
			idx = 0;
			int startIdx = idx;
			int endIdx = idx;
			String speName ="";
			startIdx = skipBlanks(idx, line);
			endIdx = getNextWordEndidx(startIdx, line);
			idx = endIdx;

			speName = line.substring(startIdx,endIdx);	
			speList.add(speName);
			
			HashMap geneNameToIDMap = new HashMap();
			speToGeneIDMap.put(speName, geneNameToIDMap);
			for (int i=0;i<geneNameList.size();i++){
				if (!isBlank(line.substring(idx,idx+1))){
					startIdx = idx;
					endIdx = getNextWordEndidx(idx, line);
					idx = endIdx;
					String geneID = line.substring(startIdx,endIdx);
					geneNameToIDMap.put(geneNameList.get(i), geneID);
				}
			}
			
		}
		
		if (file != null) {
			file.close();
		}
		
		result.add(speList);   //"specieNameList"
		result.add(geneNameList); ///"geneNameList"
		result.add(speToGeneIDMap);//"speciesNameToGeneIDMap"
		return result;
	}
	
	public void saveSequenceIDFile(String outfilename,List resultList) throws IOException {
		FileWriter fout = null;
		fout = new FileWriter(outfilename);
		
		ArrayList speList = (ArrayList) resultList.get(0);
		ArrayList geneNameList = (ArrayList) resultList.get(1);
		HashMap speToGeneIDMap = (HashMap) resultList.get(2);
		
		fout.write("TAXON");
		for (int i = 0; i < geneNameList.size(); i++) {
			fout.write("\t");
			fout.write((String)geneNameList.get(i));
		}
		fout.write("\r\n");
		
		for (int i = 0; i < speList.size(); i++) {
			fout.write((String)speList.get(i));
			for (int j = 0; j < geneNameList.size(); j++) {
				fout.write("\t");
				String geneName = (String) geneNameList.get(j);
				if (speToGeneIDMap.containsKey(geneName))
					fout.write((String)speToGeneIDMap.get(geneName));
			}
			fout.write("\r\n");
		}
		
		if (fout != null)
			fout.close();
	}
}
