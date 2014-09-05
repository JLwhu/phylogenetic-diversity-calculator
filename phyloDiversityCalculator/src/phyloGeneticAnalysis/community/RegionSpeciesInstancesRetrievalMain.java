package phyloGeneticAnalysis.community;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.sql.Date;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.SortedSet;
import java.util.TreeSet;

import org.json.JSONArray;

import pal.tree.SimpleTree;
import pal.tree.Tree;
import pal.tree.TreeParseException;
import phyloGeneticAnalysis.io.ArffIO;
import phyloGeneticAnalysis.io.csvFileIO;
import phyloGeneticAnalysis.io.newickFileIO;

import web.dao.MongoSpeciesNameDao;
import web.dao.MongoSpeciesSpotRecordDao;
import web.dao.SpeciesNameDao;
import web.dao.SpeciesSpotRecordDao;
import web.dao.impl.MongoSpeciesNameDaoImpl;
import web.dao.impl.MongoSpeciesSpotRecordDaoImpl;
import web.dao.impl.SpeciesNameDaoImpl;
import web.dao.impl.SpeciesSpotRecordDaoImpl;
import web.exception.DaoException;
import web.model.MongoSpeciesName;
import web.model.MongoSpeciesRecord;
import web.model.SpeciesSpotRecord;
import web.mongoConf.MongoConn;
import weka.core.Attribute;
import weka.core.FastVector;
import weka.core.Instance;
import weka.core.Instances;

public class RegionSpeciesInstancesRetrievalMain {
	
	public static void main(String[] args) {
		String recordFilename = "C:\\Users\\jingliu5\\UFLwork\\UFL web\\Jing_BirdWebsite\\dataset\\AllFilesForJing.ED.txt";
		String outfilename = "regionsfre1014_0.5.txt";
		String outlatlngfilename = "latlngall1014_0.5.txt";
		String treFileName = "Bird.Test1.MLTree.tre";// args[0];"Test_8Tax1.tre"
		
		String fileName = "C:\\Users\\jingliu5\\UFLwork\\UFL web\\Jing_BirdWebsite\\dataset\\newspecies.txt";
										// "Bird.Test1.MLTree.tre"
		SimpleTree m_palTree;
		FileWriter fout = null;
		
		newickFileIO nfio = new newickFileIO();
		try {
			m_palTree = (SimpleTree) nfio.inputNewickFile(treFileName);
			RegionSpeciesInstancesRetrievalMain cd = new RegionSpeciesInstancesRetrievalMain();
	//		List latList = null;
	//		List lngList = null;
	//		Instances regions = cd.getRegions(m_palTree, outlatlngfilename);
			HashMap newnames = importSpeciesname();
			Instances regions = cd.getRegionsFromTxt(m_palTree, recordFilename, newnames, outlatlngfilename);
			ArffIO arffio = new ArffIO();
			arffio.saveArff(regions,outfilename);	
			
		} catch (IOException e) {
			e.printStackTrace();
		} catch (TreeParseException e) {
			e.printStackTrace();
		} catch (DaoException e) {
			e.printStackTrace();
		} catch (InterruptedException e) {
			e.printStackTrace();
		}finally {
			if (fout != null) {
				try {
					fout.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}

	}
	

	
	private Instances getRegions(Tree tree, String outlatlngfilename) throws DaoException, IOException, InterruptedException {
		double step = 0.1;
		double minlat = -90;
		double maxlat = 89;
		double minlng = -180;
		double maxlng = 179;
		int regionCount = 64800;


		String relationName = "species";
		// Create vectors to hold information temporarily.
		FastVector attributes = new FastVector();
		for (int i = 0; i < tree.getExternalNodeCount(); i++) {
			attributes.addElement(new Attribute(tree.getExternalNode(i)
					.getIdentifier().getName(), attributes.size()));
		}
		Instances regions = new Instances(relationName, attributes, regionCount);
		HashMap nameMap = new HashMap();
		for (int j = 0; j < tree.getExternalNodeCount(); j++) {
			nameMap.put(tree.getExternalNode(j).getIdentifier().getName(), j);
		}		
		
		MongoSpeciesNameDao mongoSpeciesNameDao = new MongoSpeciesNameDaoImpl();
		HashMap idNameMap = (HashMap) mongoSpeciesNameDao.getAllSpeciesNames();
		
		MongoSpeciesSpotRecordDao mongoSpeciesSpotRecordDao = new MongoSpeciesSpotRecordDaoImpl();
		MongoConn conn= new MongoConn();
		conn.openDbConnection();
		List latList = new ArrayList();
		List lngList = new ArrayList();
		int count = 0;
		for (double lat = minlat; lat < maxlat; lat += step) {
			for (double lng = minlng; lng < maxlng; lng += step) {
				List<MongoSpeciesRecord> msr = mongoSpeciesSpotRecordDao
						.getSpeciesSpotRecordByLocationRectangle(lat, lng, lat
								+ step, lng + step,conn);//

				List<Integer> subTreeSpeciesIdList = new ArrayList<Integer>();
				HashMap idfreMap = new HashMap();
				if (msr != null && msr.size() > 1){
					for (int j = 0; j < msr.size(); j++) {
						Integer temp = msr.get(j).getSpecisid();
						if (subTreeSpeciesIdList.indexOf((Object) temp) < 0) {
							subTreeSpeciesIdList.add(temp);
							idfreMap.put(temp, 1);
						} else {
							idfreMap.put(temp, (Integer) idfreMap.get(temp) + 1);
						}
					}

					List subTreeNamelist = new ArrayList<String>();
					List frelist = new ArrayList<String>();
					for (int j = 0; j < subTreeSpeciesIdList.size(); j++) {
						String name;
						name = ((MongoSpeciesName) idNameMap
								.get(subTreeSpeciesIdList.get(j)))
								.getScientificName();
						subTreeNamelist.add(name);
						frelist.add((idfreMap.get(subTreeSpeciesIdList.get(j))));
					}

					double[] attValue = new double[tree.getExternalNodeCount()];
					for (int j = 0; j < tree.getExternalNodeCount(); j++) {
						attValue[j] = 0;
					}

					for (int j = 0; j < subTreeNamelist.size(); j++) {
						if (nameMap.get(subTreeNamelist.get(j)) != null)
							attValue[(int) nameMap.get(subTreeNamelist.get(j))] = ((Integer) frelist.get(j)).doubleValue();
					}
					Instance instance = new Instance(1, attValue);
					regions.add(instance);
					latList.add(lat);
					lngList.add(lng);
					System.out.print(lat+" "+lng+"\r\n");
				}
				count++;
				if (count % 10000 == 1)  Thread.sleep(4000);
			}

		}
		conn.closeDbConnection();
		
		FileWriter fout = null;
		fout = new FileWriter(outlatlngfilename);
		for (int i = 0; i < latList.size(); i++) {
			fout.write(latList.get(i) + " " + lngList.get(i) + "\r\n");
		}

		if (fout != null) {
			fout.close();
		}
		return regions;

	}
	
	private Instances getRegionsFromTxt(Tree tree, String recordFilename, HashMap newnames, String outlatlngfilename) throws DaoException, IOException, InterruptedException {
		double step = 0.5;
		double minlat = -90;
		double maxlat = 89;
		double minlng = -180;
		double maxlng = 179;
		int regionCount = 64800;
		FileReader file = null;
		FileWriter fout = null;


		String relationName = "species";
		// Create vectors to hold information temporarily.
		FastVector attributes = new FastVector();
		for (int i = 0; i < tree.getExternalNodeCount(); i++) {
			attributes.addElement(new Attribute(tree.getExternalNode(i)
					.getIdentifier().getName(), attributes.size()));
		}
		Instances regions = new Instances(relationName, attributes, regionCount);
		HashMap nameMap = new HashMap();
		for (int j = 0; j < tree.getExternalNodeCount(); j++) {
			nameMap.put(tree. getExternalNode(j).getIdentifier().getName(), j);
		}		

		file = new FileReader(recordFilename);
		BufferedReader reader = new BufferedReader(file);
		fout = new FileWriter(outlatlngfilename);

		String line = "";

		line = reader.readLine();

		String lastname = "";
		String currname = "";
		
		List latList = new ArrayList();
		List lngList = new ArrayList();
		
		HashMap regionInstance = new HashMap();
		
		while (line != null && line != "") { // && i<10
			String line1 = line;
			System.out.println(line);
			int idx = 0;
			String recordName = line.substring(0, line.indexOf("\t"));
			idx = line.indexOf("\t") + 1;
			line = line.substring(idx);
			String speciesname = line.substring(0, line.indexOf("\t"));
			int idx1 = speciesname.indexOf(' ');
			speciesname = speciesname.substring(0, idx1) + "_"
					+ speciesname.substring(idx1 + 1);
			speciesname.replace(' ', '_');
			if (newnames.containsKey(speciesname)) {
				speciesname = (String) newnames.get(speciesname);
			}
			currname = speciesname;

			idx = line.indexOf("\t") + 1;
			line = line.substring(idx);
			String lat = line.substring(0, line.indexOf("\t"));
			idx = line.indexOf("\t") + 1;
			line = line.substring(idx);
			String lng = line.substring(0, line.indexOf("\t"));
			idx = line.indexOf("\t") + 1;
			line = line.substring(idx);
			String year = line.substring(0, line.indexOf("\t"));
			idx = line.indexOf("\t") + 1;
			line = line.substring(idx);
			String month = line.substring(0, line.indexOf("\t"));
			idx = line.indexOf("\t") + 1;
			line = line.substring(idx);
			String date = line.substring(0, line.indexOf("\t"));
			String spotdate = year + "-" + month + "-" + date;
			idx = line.indexOf("\t") + 1;
			line = line.substring(idx);
			String abundance = line.substring(0, line.indexOf("\t"));

			SpeciesSpotRecord sr = new SpeciesSpotRecord();
			double[] loc = new double[2];
			loc[1] = Double.valueOf(lat);
			loc[0] = Double.valueOf(lng);
			sr.setRecordName(recordName);
			sr.setLat(loc[1]);
			sr.setLng(loc[0]);
			sr.setSpotDate(Date.valueOf(spotdate));
			if (abundance == null || abundance.equals(""))
				;// msr.setAbundance(0);
			else
				sr.setAbundance(Integer.valueOf(abundance));
			/* if (!lastname.equals(currname)){ */
			
			String key = "";
			if (loc[1]<Math.floor(loc[1])+step)
				key = String.valueOf(Math.floor(loc[1]));
			else
				key = String.valueOf(Math.floor(loc[1])+step);
			key = key +"_";
			if (loc[0]<Math.floor(loc[0])+step)
				key = key + String.valueOf(Math.floor(loc[0]));
			else
				key = key + String.valueOf(Math.floor(loc[0])+step);
			
		//	String key = String.valueOf(Math.floor(loc[1]))+"_"+String.valueOf(Math.floor(loc[0]));
			if (regionInstance.get(key)==null){
				double[] attValue = new double[tree.getExternalNodeCount()];
				for (int j = 0; j < tree.getExternalNodeCount(); j++) {
					attValue[j] = 0;
				}
				if (nameMap.get(currname) != null) {
					attValue[(int) nameMap.get(currname)] = 1;
					Instance instance = new Instance(1, attValue);
					regionInstance.put(key, instance);
				}			
			}else{
				Instance instance = (Instance) regionInstance.get(key);
				if (nameMap.get(currname) != null) {
					int attidx = (int) nameMap.get(currname);
					double newvalue = instance.value(attidx) + 1;
					instance.setValue(attidx, newvalue);
				}
			}			

			
			line = reader.readLine();
		}
		
		//order by lat lng
		Iterator it = regionInstance.keySet().iterator();
		SortedSet<String> ss = new TreeSet<String>();
		while(it.hasNext()){
			String key = (String) it.next();
			ss.add(key);
		}
		it = ss.iterator();
		while(it.hasNext()){
			String key = (String) it.next();
			String lat = key.substring(0, key.indexOf('_'));
			String lng = key.substring(key.indexOf('_')+1);
			latList.add(lat);
			lngList.add(lng);
			Instance instance = (Instance) regionInstance.get(key);
			regions.add(instance);
		}
		
		fout = new FileWriter(outlatlngfilename);
		for (int i = 0; i < latList.size(); i++) {
			fout.write(latList.get(i) + " " + lngList.get(i) + "\r\n");
		}

		
		if (file != null) {
			file.close();
		}
		if (fout != null) {
			fout.close();
		}
		return regions;

	}
	
	public static HashMap importSpeciesname() {
		String fileName = "C:\\Users\\jingliu5\\UFLwork\\UFL web\\Jing_BirdWebsite\\dataset\\newspecies.txt";
		// String outfileName =
		// "C:\\Users\\jingliu5\\UFLwork\\UFL web\\Jing_BirdWebsite\\dataset\\NewCommonNamesNofound.txt";
		FileReader file = null;
		FileWriter fout = null;
		PrintWriter pw = null;
		HashMap speciesname = new HashMap();

		try {
			file = new FileReader(fileName);
			BufferedReader reader = new BufferedReader(file);
			// fout = new FileWriter(outfileName);

			String line = "";
			line = reader.readLine();
			SpeciesNameDao speciesNameDao = new SpeciesNameDaoImpl();
			while (line != null && line != "") { // && i<10
				String line1 = line;
				System.out.println(line);
				int idx = 0;
				String old = line.substring(0, line.indexOf("\t"));
				idx = line.indexOf("\t") + 1;
				line = line.substring(idx);
				String newname = line;
				speciesname.put(old, newname);
				line = reader.readLine();
			}

		} catch (FileNotFoundException e) {
			throw new RuntimeException("File not found");
		} catch (IOException e) {
			throw new RuntimeException("IO Error occured");
		}finally {
			if (file != null) {
				try {
					file.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}

			if (fout != null) {
				try {
					fout.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
			if (pw != null)
				pw.close();

		}
		return speciesname;
	}


}
