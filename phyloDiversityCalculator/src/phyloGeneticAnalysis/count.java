package phyloGeneticAnalysis;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;

public class count {

	public static void main(String[] args) {
		String fileName = "C:\\Users\\jingliu5\\UFLwork\\UFL web\\Jing_BirdWebsite\\elevation\\MissingElevations.28May.Unique.txt";
	//	String outfileName = "C:\\Users\\jingliu5\\UFLwork\\UFL web\\Jing_BirdWebsite\\elevation.txt";
		FileReader file = null;
	//	FileReader fout = null;
		FileWriter fleft = null;

		try {
			file = new FileReader(fileName);
			BufferedReader reader = new BufferedReader(file);
	//		fout = new FileReader(outfileName);
	//		BufferedReader reader2 = new BufferedReader(fout);
	//		fleft = new FileWriter("C:\\Users\\jingliu5\\UFLwork\\UFL web\\Jing_BirdWebsite\\ALL_LOCSleft.txt");

			String line = "";
			String line2 = "";
			int i = 0;
				
			while ( (line = reader.readLine()) != null ) { //(line = reader.readLine()) != null && && (line2 = reader2.readLine()) != null
				line2  = reader.readLine();
				String lat = line.substring(0, line.indexOf("\t"));
				if (lat.indexOf(".")==0) lat = "0"+lat;
				if (lat.indexOf("-")==0 && lat.indexOf(".")==1) lat = "-0"+lat.substring(lat.indexOf("."));
				String lng = line.substring(line.indexOf("\t") + 1);
				if (lng.indexOf(".")==0) lng = "0"+lng;
				if (lng.indexOf("-")==0 && lng.indexOf(".")==1) lng = "-0"+lng.substring(lng.indexOf("."));

		
				System.out.print(line + "\r\n");
				System.out.print(line2 + "\r\n");

				i++;
			}
		//	while ((line = reader.readLine()) != null) {
		//		fleft.write(line + "\r\n");
		//	}

		//	System.out.print(line + "\r\n");
		//	System.out.print(line2 + "\r\n");
			System.out.print("count="+i + "\r\n");

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
			
		/*	if (fout != null) {
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
			}*/
		}
	}
}
