package phyloGeneticAnalysis;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.sql.Date;
import java.util.Random;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import web.model.SpeciesSpotRecord;

public class dataToJason {
	public static void main(String[] args) {
		String fileName = "C:\\mongodb-win32-x86_64-2008plus-2.4.3\\data\\db\\record.csv";
		String outfileName = "C:\\mongodb-win32-x86_64-2008plus-2.4.3\\data\\db\\record1.json";
		FileReader file = null;
		FileWriter fout = null;
		PrintWriter pw = null;

		try {
			file = new FileReader(fileName);
			BufferedReader reader = new BufferedReader(file);
			fout = new FileWriter(outfileName);

			String line = "";
			int i = 0;
			try {
				JSONArray resultJSONArray = new JSONArray();
				line = reader.readLine();
				while (line  != null && line != "") { // && i<10
					System.out.print(line + "\r\n");
					int idx = 0;
					String reocordName = line.substring(1,
							line.indexOf(",") - 1);
					idx = line.indexOf(",") + 1;
					line = line.substring(idx);
					String speciesid = line.substring(0, line.indexOf(","));
					idx = line.indexOf(",") + 1;
					line = line.substring(idx);
					String lng = line.substring(0, line.indexOf(","));
					idx = line.indexOf(",") + 1;
					line = line.substring(idx);
					String lat = line.substring(0, line.indexOf(","));
					idx = line.indexOf(",") + 1;
					line = line.substring(idx);
					String spotdate = line.substring(1,line.length()-1);

					JSONObject obj = new JSONObject();
					obj.put("recordName", reocordName);
					obj.put("speciesid", Integer.valueOf(speciesid));
					obj.put("spotdate", Date.valueOf(spotdate));
					JSONObject obj1 = new JSONObject();
					obj1.put("type", "Point");
					JSONArray cood =  new JSONArray();
					cood.put(Double.valueOf(lng));
					cood.put(Double.valueOf(lat));
			//		obj1.put("coordinates", cood);
					
			//		obj.put("loc", obj1);
					obj.put("loc", cood);

					if (Double.valueOf(lng) <= 180
							&& Double.valueOf(lng) >= -180
							&& Double.valueOf(lat) <= 90
							&& Double.valueOf(lat) >= -90)
						fout.write(obj.toString() + "\r\n");

			//		i++;
			//		if (i>100) break;
					line = reader.readLine();
				}

			} catch (JSONException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

		} catch (FileNotFoundException e) {
			throw new RuntimeException("File not found");
		} catch (IOException e) {
			throw new RuntimeException("IO Error occured");
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
