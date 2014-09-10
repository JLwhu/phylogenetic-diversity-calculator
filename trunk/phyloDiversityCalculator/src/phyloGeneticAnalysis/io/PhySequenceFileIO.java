package phyloGeneticAnalysis.io;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

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
		
		line = reader.readLine();
		returnMap.put("totalBits", totalBitsStr);
		int i=0;
		while (line != null && line != "") {
			i++;
			idx = 0;
			startIdx = idx;
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
		writer.write("\t");
		writer.write((String)speciesToSeqMap.get("totalBits"));
		writer.write("\r\n");
		Iterator it = speciesToSeqMap.entrySet().iterator();
		while (it.hasNext()) {
			String speciesName = (String) it.next();
			if (!speciesName.equals("totalBits")) {
				writer.write(speciesName);
				writer.write("\t");
				String sequence = (String) speciesToSeqMap.get(speciesName);
				writer.write(sequence);
				writer.write("\r\n");
			}
		}
		writer.flush();
		writer.close();
	}
	public void readExtractAndSaveToPhyFile(String filename,String outfilename, List speciesNameList)throws IOException {
		FileReader file = null;
		file = new FileReader(filename);
		BufferedReader reader = new BufferedReader(file);
		BufferedWriter writer = new BufferedWriter(new FileWriter(outfilename));

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
		
		int count = speciesNameList.size();
		writer.write(String.valueOf(count));
		writer.write("\t");
		writer.write(totalBitsStr);
		writer.write("\r\n");
		
		line = reader.readLine();

		int i=0;
		while (line != null && line != "") {
			i++;
			idx = 0;
			startIdx = idx;
			String speName = "";
			startIdx = skipBlanks(idx, line);
			endIdx = getNextWordEndidx(startIdx, line);
			speName = line.substring(startIdx, endIdx);
			if (speciesNameList.contains(speName)) {
				String genebits = "";
				idx = endIdx;
				startIdx = skipBlanks(idx, line);
				endIdx = getNextWordEndidx(startIdx, line);
				genebits = line.substring(startIdx, endIdx);
				writer.write(speName);
				writer.write("\t");
				writer.write(genebits);
				writer.write("\r\n");
			}			
			line = reader.readLine();
		}
		
		if (file != null) {
			file.close();
		}
	}
}
