package phyloGeneticAnalysis;

import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLConnection;
import java.util.Random;

public class elevationMain {
	public static void main(String[] args) {
		String fileName = "C:\\Users\\jingliu5\\UFLwork\\UFL web\\Jing_BirdWebsite\\elevation\\MissingElevations.28May.Unique.left.txt";
		String outfileName = "C:\\Users\\jingliu5\\UFLwork\\UFL web\\Jing_BirdWebsite\\elevation\\MissingElevations.28May.UniqueElevation1.txt";
		String outleft = "C:\\Users\\jingliu5\\UFLwork\\UFL web\\Jing_BirdWebsite\\elevation\\MissingElevations.28May.Unique.left1.txt";
		FileReader file = null;
		FileWriter fout = null;
		FileWriter fleft = null;

		try {
			file = new FileReader(fileName);
			BufferedReader reader = new BufferedReader(file);
			fout = new FileWriter(outfileName);
			fleft= new FileWriter(outleft);

			String line = "";
			int i = 0;
			String url = "http://gisdata.usgs.gov/xmlwebservices2/elevation_service.asmx/getElevation?";
			//String  url = "http://www.earthtools.org/height/";
	//		String  url = "http://maps.googleapis.com/maps/api/elevation/xml?locations=";
			while ((line = reader.readLine()) != null&& (line = reader.readLine()) != "") { // && i<10
				System.out.print(line + "\r\n");
				String lat = line.substring(0, line.indexOf("\t"));
				if (lat.indexOf(".")==0) lat = "0"+lat;
				if (lat.indexOf("-")==0 && lat.indexOf(".")==1) lat = "-0"+lat.substring(lat.indexOf("."));
				String lng = line.substring(line.indexOf("\t") + 1);
				if (lng.indexOf(".")==0) lng = "0"+lng;
				if (lng.indexOf("-")==0 && lng.indexOf(".")==1) lng = "-0"+lng.substring(lng.indexOf("."));

				String rawData = "id=10";
				String type = "application/x-www-form-urlencoded";
				String parameter = "X_Value="
						+ lat
						+ "&Y_Value="
						+ lng
						+ "&Elevation_Only=TRUE&Elevation_Units=FEET&Source_Layer=*NED";
			//	String parameter = lat+"/"+lng;
		//		String parameter = lat+","+lng+"&sensor=true";
				URL u = new URL(url + parameter);
				HttpURLConnection conn = (HttpURLConnection) u.openConnection();
				conn.setDoOutput(true);
				conn.setRequestMethod("POST");
				conn.setRequestProperty("Content-Type", type);
				// OutputStream os = conn.getOutputStream();
				// System.out.print(os);



				elevationMain c = new elevationMain();
				String out = c.getHTML(url + parameter);
		//		String elevation = out.substring(out.indexOf("<double>") + 8,
		//				out.indexOf("</double>"));
				String retstatus = out.substring(out.indexOf("<status>")+8,out.indexOf("</status>"));
				 Random randomGenerator = new Random();
				
				while (out==null || out=="" ){
					int randomInt = randomGenerator.nextInt(100);

					try {
						Thread.sleep(10000+randomInt);
					} catch (InterruptedException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
					out = c.getHTML(url + parameter);
				}
				
				String elevation;
				if (retstatus.equals("OK")) {

					// String elevation = out.substring(out.indexOf("<meters>")
					// + 8,
					// out.indexOf("</meters>"));
					elevation = out.substring(
							out.indexOf("<elevation>") + 11,
							out.indexOf("</elevation>"));
					System.out.print(elevation + "\r\n");
					fout.write(line+"\r\n"+elevation + "\r\n");
					i++;
				}else if (retstatus.equals("INVALID_REQUEST")){
					elevation = "-9999";
					System.out.print(elevation + "\r\n");
					fout.write(elevation + "\r\n");
					i++;
				}else {
					elevation = retstatus;
					System.out.print(elevation + "\r\n");
					fleft.write(line + "\r\n");
					i++;
				}
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
			
			if (fleft != null) {
				try {
					fleft.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}

	}

	public String getHTML(String urlToRead) {
		URL url;
		HttpURLConnection conn;
		BufferedReader rd;
		String line;
		String result = "";
		try {
			url = new URL(urlToRead);
			conn = (HttpURLConnection) url.openConnection();
			conn.setRequestMethod("GET");
			rd = new BufferedReader(
					new InputStreamReader(conn.getInputStream()));
			while ((line = rd.readLine()) != null) {
				result += line;
			}
			rd.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return result;
	}

}
