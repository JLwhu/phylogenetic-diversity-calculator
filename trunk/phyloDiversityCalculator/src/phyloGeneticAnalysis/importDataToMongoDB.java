package phyloGeneticAnalysis;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.sql.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import web.dao.MongoSpeciesNameDao;
import web.dao.MongoSpeciesSpotRecordDao;
import web.dao.impl.MongoSpeciesNameDaoImpl;
import web.dao.impl.MongoSpeciesSpotRecordDaoImpl;
import web.exception.DaoException;
import web.model.MongoSpeciesRecord;
import web.mongoConf.MongoConn;

public class importDataToMongoDB {
	public static void main(String[] args) {
	//	importDataToMongoDB idtd = new importDataToMongoDB();
		HashMap newnames = importSpeciesname();
	/*	Iterator iter = newnames.entrySet().iterator(); 
		while (iter.hasNext()) { 
		    Map.Entry entry = (Map.Entry) iter.next(); 
		    Object key = entry.getKey(); 
		    Object val = entry.getValue();
		    System.out.print((String)key+val+"\r\n");
		} */
		importData(newnames);

	}
	
	public static HashMap importSpeciesname() {
		String fileName = "C:\\Users\\jingliu5\\UFLwork\\UFL web\\Jing_BirdWebsite\\dataset\\newspecies.txt";
	//	String outfileName = "C:\\Users\\jingliu5\\UFLwork\\UFL web\\Jing_BirdWebsite\\dataset\\NewCommonNamesNofound.txt";
		FileReader file = null;
		FileWriter fout = null;
		PrintWriter pw = null;
		HashMap speciesname = new HashMap();

		try {
			file = new FileReader(fileName);
			BufferedReader reader = new BufferedReader(file);
	//		fout = new FileWriter(outfileName);
			
			String line = "";
			line = reader.readLine();
			MongoSpeciesNameDao mongoSpeciesNameDao = new MongoSpeciesNameDaoImpl();
			HashMap unfoundSpecies = new HashMap();
			HashMap sciName = (HashMap) mongoSpeciesNameDao.getAllSpeciesScientificNameMap();
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
		} catch (DaoException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} finally {
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
	
	public static void importData(HashMap newnames) {
		String fileName = "C:\\Users\\jingliu5\\UFLwork\\UFL web\\Jing_BirdWebsite\\dataset\\AllFilesForJing.ED.txt";
		String outfileName = "C:\\Users\\jingliu5\\UFLwork\\UFL web\\Jing_BirdWebsite\\dataset\\unfoundSpecies.txt";
		FileReader file = null;
		FileWriter fout = null;
		PrintWriter pw = null;

		try {
			file = new FileReader(fileName);
			BufferedReader reader = new BufferedReader(file);
			fout = new FileWriter(outfileName);

			String line = "";
			int i = 0;
			int j=0;
			JSONArray resultJSONArray = new JSONArray();
			line = reader.readLine();
			MongoSpeciesSpotRecordDao mongoSpeciesSpotRecordDao = new MongoSpeciesSpotRecordDaoImpl();
			MongoConn conn= new MongoConn();
			MongoSpeciesNameDao mongoSpeciesNameDao = new MongoSpeciesNameDaoImpl();
	//		HashMap map = (HashMap) mongoSpeciesNameDao.getAllSpeciesNames();
			HashMap unfoundSpecies = new HashMap();
			HashMap sciName = (HashMap) mongoSpeciesNameDao.getAllSpeciesScientificNameMap();
			String lastname="";
			String currname="";
			conn.openDbConnection();
			
			while (line != null && line != "") { // && i<10
				String line1 = line;
				System.out.println(line);
				int idx = 0;
				String recordName = line.substring(0, line.indexOf("\t"));
				idx = line.indexOf("\t") + 1;
				line = line.substring(idx);
				String speciesname = line.substring(0, line.indexOf("\t"));
				int idx1 = speciesname.indexOf(' ');
				speciesname = speciesname.substring(0,idx1)+"_"+speciesname.substring(idx1+1);
				speciesname.replace(' ', '_');
				if (newnames.containsKey(speciesname)){
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
				String spotdate = year+"-"+month+"-"+date;
				idx = line.indexOf("\t") + 1;
				line = line.substring(idx);
				String abundance = line.substring(0, line.indexOf("\t"));


				MongoSpeciesRecord msr = new MongoSpeciesRecord();
				double[] loc = new double[2];
				loc[1] = Double.valueOf(lat);
				loc[0] = Double.valueOf(lng);
				msr.setRecordName(recordName);
				msr.setLoc(loc);
				msr.setYear(Integer.valueOf(year));
				msr.setMonth(Integer.valueOf(month));
				msr.setSpotDate(Date.valueOf(spotdate));
				if (abundance==null||abundance.equals(""))
					;//msr.setAbundance(0);
				else
					msr.setAbundance(Integer.valueOf(abundance));
/*				if (!lastname.equals(currname)){*/
				if (sciName.containsValue(speciesname)) {
					Integer speciesid = null;
					Iterator iter = sciName.entrySet().iterator();
					while (iter.hasNext()) {
						Map.Entry entry = (Map.Entry) iter.next();
						if (entry.getValue().equals(speciesname)) {
							speciesid = (Integer) entry.getKey();
							break;
						}
					}

					msr.setSpecisid(speciesid);
					if (Double.valueOf(lng) <= 180
							&& Double.valueOf(lng) >= -180
							&& Double.valueOf(lat) <= 90
							&& Double.valueOf(lat) >= -90) {
						mongoSpeciesSpotRecordDao.saveSpeciesSpotRecord(msr);
						j++;
					}
				}
				/*
				 * } else { // fout.write(line1); //
				 * fout.write(speciesname+"\r\n"); if
				 * (!unfoundSpecies.containsKey(speciesname))
				 * unfoundSpecies.put(speciesname, speciesname); } lastname =
				 * currname; }
				 */
				i++;
				// if (i>100) break;
				line = reader.readLine();
			}
			System.out.println("all="+i);
			System.out.println("database="+j);
			conn.closeDbConnection();

	//	} catch (DaoException e) {
			// TODO Auto-generated catch block
	//		e.printStackTrace();
		} catch (FileNotFoundException e) {
			throw new RuntimeException("File not found");
		} catch (IOException e) {
			throw new RuntimeException("IO Error occured");
		} catch (DaoException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		} finally {
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
	}
}
