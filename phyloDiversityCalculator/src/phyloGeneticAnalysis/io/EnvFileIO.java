package phyloGeneticAnalysis.io;


import java.io.BufferedReader;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.SortedMap;
import java.util.TreeMap;

import pal.tree.Tree;
import weka.core.Instance;

public class EnvFileIO {
	public HashMap readEnvToMap(String filename)throws IOException {
		HashMap returnMap = new HashMap();
		List speEnvList = readEnv(filename);
		
		List envList = new ArrayList();
		for(int i=0;i<speEnvList.size();i++){
			String temp = (String) speEnvList.get(i);
	//		String spename = temp.substring(0, temp.indexOf("@"));
			int idx = temp.indexOf("#");			
			String env;
			if (idx<0){
				env =  temp.substring(temp.indexOf("@")+1);
			}else{
				env =  temp.substring(temp.indexOf("@")+1,temp.indexOf("#"));
			}
			if (!envList.contains(env))
				envList.add(env);
		}
		Collections.sort(envList); 
		
		Iterator it = envList.iterator();
		HashMap envNameToSpeciesSetMap = new HashMap();
		HashMap envNameToSpeciesAbundanceMap = new HashMap();
		while(it.hasNext()){
			String envName = (String) it.next();
			Set<String> speciesSet = new HashSet<String>();
			envNameToSpeciesSetMap.put(envName, speciesSet);
			HashMap speciesAbundanceMap = new HashMap();
			envNameToSpeciesAbundanceMap.put(envName, speciesAbundanceMap);
		}		
		
		for(int i=0;i<speEnvList.size();i++){
			String temp = (String) speEnvList.get(i);
			String spename = temp.substring(0, temp.indexOf("@"));
			int idx = temp.indexOf("#");			
			String envName;
			if (idx<0){
				envName =  temp.substring(temp.indexOf("@")+1);
			}else{
				envName =  temp.substring(temp.indexOf("@")+1,temp.indexOf("#"));
				String abundance = temp.substring(temp.indexOf("#")+1);
				HashMap speciesAbundanceMap = (HashMap) envNameToSpeciesAbundanceMap.get(envName);
				speciesAbundanceMap.put(spename, abundance);
			}
			Set<String> speciesSet = (Set<String>) envNameToSpeciesSetMap.get(envName);
			speciesSet.add(spename);			
		}
		
		returnMap.put("EnvList", envList);
		returnMap.put("EnvToSpeciesMap", envNameToSpeciesSetMap);
		returnMap.put("EnvNameToSpeciesAbundanceMap", envNameToSpeciesAbundanceMap);

		return returnMap;
	}
	
	
	
	
	public List readEnv(String filename) throws IOException {
		List speEnvList = new ArrayList();
		FileReader file = null;

		file = new FileReader(filename);
		BufferedReader reader = new BufferedReader(file);

		String line = "";
		line = reader.readLine();
		Set seperator = new HashSet();
		seperator.add("\t");
		seperator.add(" ");
		seperator.add("\r");
		seperator.add("\r\n");
		while (line != null && line != "") { // && i<10
			int idx = 0;
			while (idx<line.length()&&seperator.contains(line.substring(idx, idx+1)))
				idx++;
			String speName ="";
			while (idx<line.length()&&!seperator.contains(line.substring(idx, idx+1))){
				speName+= line.substring(idx, idx+1);
				idx++;
			}
			while (idx<line.length()&&seperator.contains(line.substring(idx, idx+1)))
				idx++;	
			String envName ="";
			while (idx<line.length()&&!seperator.contains(line.substring(idx, idx+1))){
				envName+= line.substring(idx, idx+1);
				idx++;
			}
			while (idx<line.length()&&seperator.contains(line.substring(idx, idx+1)))
				idx++;	
			String abundance = "";
			while (idx<line.length()&&!seperator.contains(line.substring(idx, idx+1))){
				abundance+= line.substring(idx, idx+1);
				idx++;
			}
			line = reader.readLine();
			if (abundance.isEmpty())
				speEnvList.add(speName+"@"+envName);
			else
				speEnvList.add(speName+"@"+envName+"#"+abundance);
		}
		
		if (file != null) {
			file.close();
		}
		return speEnvList;
	}
	
	public void saveEnv(String outfilename, Tree tree, Instance instanceA, Instance instanceB) throws IOException {
		FileWriter fout = null;
		fout = new FileWriter(outfilename);
		for (int i=0;i<instanceA.numAttributes();i++){
			if (instanceA.value(i) > 0) {
				fout.write(tree.getExternalNode(i).getIdentifier().getName());
				fout.write("        ");
				fout.write("ENVA\r\n");
			}
		}
		for (int i=0;i<instanceB.numAttributes();i++){
			if (instanceB.value(i) > 0) {
				fout.write(tree.getExternalNode(i).getIdentifier().getName());
				fout.write("\t");
				fout.write("ENVB\r\n");
			}
		}
		if (fout != null) fout.close();
	}
}
