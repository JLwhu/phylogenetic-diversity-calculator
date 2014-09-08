package phyloGeneticAnalysis.io;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.HashMap;
import java.util.Iterator;

public class PhySequenceFileIO extends Parser{
	public HashMap readPhyToMap(String filename)throws IOException {
		HashMap returnMap = new HashMap();

		FileReader file = null;
		file = new FileReader(filename);
		BufferedReader reader = new BufferedReader(file);

		String line = "";
		line = reader.readLine();
		
		int totalTaxa;
		String totalTaxaStr = "";
		int totalBits;
		String totalBitsStr = "";
		int idx = 0;
		
		int startIdx = idx;
		int endIdx = idx;
		startIdx = skipBlanks(idx, line);
		endIdx = getNextWordEndidx(startIdx, line);
		totalTaxaStr = line.substring(startIdx,endIdx);
		idx = endIdx;
		startIdx = skipBlanks(idx, line);
		endIdx = getNextWordEndidx(startIdx, line);
		totalBitsStr = line.substring(startIdx,endIdx);
		
		while (line != null && line != "") {
			idx = 0;
			String speName = "";
			startIdx = skipBlanks(idx, line);
			endIdx = getNextWordEndidx(startIdx, line);
			speName = line.substring(startIdx,endIdx);
			String genebits = "";
			idx = endIdx;
			startIdx = skipBlanks(idx, line);
			endIdx = getNextWordEndidx(startIdx, line);
			genebits = line.substring(startIdx,endIdx);
			returnMap.put(speName, genebits);
			
			line = reader.readLine();
		}
		
		if (file != null) {
			file.close();
		}
		return returnMap;
		
	}
	
	public void saveMapToPhy(String outfilename, HashMap speciesToSeqMap)throws IOException {
		BufferedWriter writer = new BufferedWriter(new FileWriter(outfilename));
		int count = speciesToSeqMap.size();
		writer.write(String.valueOf(count));
		Iterator it = speciesToSeqMap.entrySet().iterator();
		while(it.hasNext()){
			String speciesName = (String) it.next();
			writer.write(speciesName);
			writer.write("\t");
			String sequence = (String) speciesToSeqMap.get(speciesName);
			writer.write(sequence);
			writer.write("\r\n");
		}
		writer.flush();
		writer.close();
	}
}
