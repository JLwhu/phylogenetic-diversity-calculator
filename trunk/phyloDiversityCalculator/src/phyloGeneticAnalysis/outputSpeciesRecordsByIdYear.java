package phyloGeneticAnalysis;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import web.dao.MongoSpeciesNameDao;
import web.dao.MongoSpeciesSpotRecordDao;
import web.dao.impl.MongoSpeciesNameDaoImpl;
import web.dao.impl.MongoSpeciesSpotRecordDaoImpl;
import web.exception.DaoException;
import web.model.MongoSpeciesName;
import web.model.MongoSpeciesRecord;
import web.mongoConf.MongoConn;

public class outputSpeciesRecordsByIdYear {
	public static void main(String[] args) {
		String outfileName = "C:\\Users\\jingliu5\\UFLwork\\UFL web\\Jing_BirdWebsite\\dataset\\speciesIdYear.txt";
		FileReader file = null;
		FileWriter fout = null;
		PrintWriter pw = null;

		try {
			fout = new FileWriter(outfileName);
			MongoSpeciesNameDao mongoSpeciesNameDao = new MongoSpeciesNameDaoImpl();
			HashMap unfoundSpecies = new HashMap();
			HashMap sciName = (HashMap) mongoSpeciesNameDao
					.getAllSpeciesNames();

			MongoSpeciesSpotRecordDao mongoSpeciesSpotRecordDao = new MongoSpeciesSpotRecordDaoImpl();
			List years = mongoSpeciesSpotRecordDao.getSpeciesSpotYearList();
			fout.write("species ");
			System.out.print("species ");
			for(int i=0;i<years.size();i++){
				fout.write(years.get(i)+" ");
				System.out.print(years.get(i)+" ");
			}
			fout.write("\r\n");
			System.out.print("\r\n");
			
			Iterator iter = sciName.entrySet().iterator();
			while (iter.hasNext()) {
				Map.Entry entry = (Map.Entry) iter.next();
				Integer speciesid = (Integer) entry.getKey();
				MongoSpeciesName msn = (MongoSpeciesName) entry.getValue();
				fout.write(speciesid+" "+msn.getScientificName()+" "+msn.getCommonName()+" ");
				System.out.print(speciesid+" "+msn.getScientificName()+" "+msn.getCommonName()+" ");
				for (int i = 0; i < years.size(); i++) {
					List<MongoSpeciesRecord> msr = mongoSpeciesSpotRecordDao
							.getSpeciesSpotRecordBySpeciesidYear(
									String.valueOf(speciesid),
									String.valueOf(years.get(i)));
					fout.write(msr.size() + " ");
					System.out.print(msr.size() + " ");
				}
				fout.write("\r\n");
				System.out.print("\r\n");

			}
			
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
	}

}
