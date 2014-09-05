package phyloGeneticAnalysis;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;

public class processText {
	public static void main(String[] args) {
		String fileName = "C:\\Users\\jingliu5\\UFLwork\\UFL web\\Jing_BirdWebsite\\elevation\\MissingElevations.28May.Unique.left1.txt";
		String outfileName = "C:\\Users\\jingliu5\\UFLwork\\UFL web\\Jing_BirdWebsite\\elevation\\MissingElevations.28May.Unique.Elevation1.1.txt";
		FileReader file = null;
		FileWriter fout = null;

		try {
			file = new FileReader(fileName);
			BufferedReader reader = new BufferedReader(file);
			fout = new FileWriter(outfileName);


			String line = "";
			String line2 = "";
			int i = 0;
				
			while ((line = reader.readLine()) != null) {
				String lat;
				String lng;
				String elevation;
				int pos=0;
				for (int j = 0; j < 10; j++) {
					if (line.indexOf("<lat>") > 0) {
						lat = line.substring(line.indexOf("<lat>") + 5,
								line.indexOf("</lat>"));
						fout.write(lat + "\t");
					}
					if (line.indexOf("<lng>") > 0) {
						lng = line.substring(line.indexOf("<lng>") + 5,
								line.indexOf("</lng>"));
						fout.write(lng + "\r\n");
					}
					if (line.indexOf("<elevation>") > 0) {
						elevation = line.substring(
								line.indexOf("<elevation>") + 11,
								line.indexOf("</elevation>"));
						fout.write(elevation + "\r\n");
					}
					pos = line.indexOf("</elevation>");
					line = line.substring(pos+12);
					i++;

				}
				System.out.print(line + "\r\n");


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
			
			if (fout != null) {
				try {
					fout.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
	}
}
