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

public class googleElevation {
	public static void main(String[] args) {
		String fileName = "C:\\Users\\jingliu5\\UFLwork\\UFL web\\Jing_BirdWebsite\\elevation\\MissingElevations.28May.Unique.left10.txt";
		String outfileName = "C:\\Users\\jingliu5\\UFLwork\\UFL web\\Jing_BirdWebsite\\elevation\\MissingElevations.28May.UniqueElevation2.txt";
		String outleft = "C:\\Users\\jingliu5\\UFLwork\\UFL web\\Jing_BirdWebsite\\elevation\\MissingElevations.28May.Unique.left2.txt";
		FileReader file = null;
		FileWriter fout = null;
		FileWriter fleft = null;

		try {
			file = new FileReader(fileName);
			BufferedReader reader = new BufferedReader(file);
			fout = new FileWriter(outfileName);
			fleft = new FileWriter(outleft);

			String line = "";
			
			int i = 0;
			int j = 0;
			int unsuccesful = 0;
			String url = "http://maps.googleapis.com/maps/api/elevation/xml?locations=";
			while ((line = reader.readLine()) != null) { // && i<10
				while (line=="") {line = reader.readLine();}
				if (line==null) break;
				String allLine = "";
				String parameter="";
				String lat = line.substring(0, line.indexOf("\t"));
				if (lat.indexOf(".") == 0)
					lat = "0" + lat;
				if (lat.indexOf("-") == 0 && lat.indexOf(".") == 1)
					lat = "-0" + lat.substring(lat.indexOf("."));
				String lng = line.substring(line.indexOf("\t") + 1);
				if (lng.indexOf(".") == 0)
					lng = "0" + lng;
				if (lng.indexOf("-") == 0 && lng.indexOf(".") == 1)
					lng = "-0" + lng.substring(lng.indexOf("."));
				System.out.print(line + "\r\n");
				allLine = allLine + "\r\n" + line;
				parameter=lat + "," + lng;
				for (j = 1; j < 10; j++) {
					if ((line = reader.readLine()) == null){
						break;
					}
					lat = line.substring(0, line.indexOf("\t"));
					if (lat.indexOf(".") == 0)
						lat = "0" + lat;
					if (lat.indexOf("-") == 0 && lat.indexOf(".") == 1)
						lat = "-0" + lat.substring(lat.indexOf("."));
					 lng = line.substring(line.indexOf("\t") + 1);
					if (lng.indexOf(".") == 0)
						lng = "0" + lng;
					if (lng.indexOf("-") == 0 && lng.indexOf(".") == 1)
						lng = "-0" + lng.substring(lng.indexOf("."));
					System.out.print(line + "\r\n");
					allLine = allLine + "\r\n" + line;
					parameter = parameter +"|"+lat + "," + lng;
				}
				parameter = parameter + "&sensor=true";
				URL u = new URL(url + parameter);

				String rawData = "id=10";
				String type = "application/x-www-form-urlencoded";

				HttpURLConnection conn = (HttpURLConnection) u.openConnection();
				conn.setDoOutput(true);
				conn.setRequestMethod("POST");
				conn.setRequestProperty("Content-Type", type);

				googleElevation c = new googleElevation();
				String out = c.getHTML(url + parameter);

				String retstatus = out.substring(out.indexOf("<status>") + 8,
						out.indexOf("</status>"));
				Random randomGenerator = new Random();

				while (out == null || out == "") {
					int randomInt = randomGenerator.nextInt(100);

					try {
						Thread.sleep(10000 + randomInt);
					} catch (InterruptedException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
					out = c.getHTML(url + parameter);
				}

				String elevation;
				if (retstatus.equals("OK")) {
					System.out.print(out + "\r\n");
					fout.write(out + "\r\n");
					i++;
				} else {
					elevation = retstatus;
					System.out.print(elevation + "\r\n");
					fleft.write(allLine);
					i++;
					unsuccesful++;
				}
				if (unsuccesful>10){
					while ((line = reader.readLine()) != null) { 
						fleft.write(line+ "\r\n" );
					}
					
					//break;					
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
