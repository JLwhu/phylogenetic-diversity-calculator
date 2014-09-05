/*
 * @author JingLiu
 * @version 1.0
 * @date Jan 7, 2013
 */


package phyloGeneticAnalysis.io;

import java.util.List;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;

import au.com.bytecode.opencsv.CSVReader;
import au.com.bytecode.opencsv.CSVWriter;


// TODO: Auto-generated Javadoc
/**
 * The Class csvFileIO.
 */


public class csvFileIO {
	

	/**
	 * Read csvfile specifice row.
	 *
	 * @param inFilename the in filename
	 * @param rowNum the row num
	 * @param colStart the column start
	 * @param colEnd the column end
	 * @return the list
	 * @throws IOException Signals that an I/O exception has occurred.
	 */
	public List<String> readCSVrow (String inFilename, int rowNum, int colStart, int colEnd) throws IOException{		
		List<String> rowList=new ArrayList<String>();
		String csvFilename = inFilename;
		CSVReader csvReader = new CSVReader(new FileReader(csvFilename));
		String[] row = null;
		int i = 0;
		
		while((row = csvReader.readNext()) != null && i<rowNum) {
		    i++;
		}
		
		if (row!=null){
			for (i = colStart;i<=colEnd;i++){
				rowList.add(row[i]);
			}
			
		}
		csvReader.close();
		return rowList;
		
	}

	
	/**
	 * Read csvfile specified column.
	 *
	 * @param inFilename the filename of the input filename, type String
	 * @param colNum the column number, type int
	 * @param skipFirstRow the skip first row
	 * @return the list of the specified column
	 * @throws IOException Signals that an I/O exception has occurred.
	 */

	public List<String> readCSVcolumn(String inFilename, int colNum, boolean skipFirstRow) throws IOException{		
		List<String> colList=new ArrayList<String>();
		CSVReader csvReader = new CSVReader(new FileReader(inFilename));
		String[] row = null;
		if (skipFirstRow) row = csvReader.readNext();
		while((row = csvReader.readNext()) != null) {
			colList.add(row[colNum]);
		}
		csvReader.close();
		return colList;
		
	}
	
	
	/**
	 * Read csv column.
	 *
	 * @param inFilename the in filename
	 * @param colNum the col num
	 * @return the list
	 * @throws IOException Signals that an I/O exception has occurred.
	 */
	public List<String> readCSVcolumn(String inFilename, int colNum) throws IOException{		
		List<String> colList;
		colList = readCSVcolumn(inFilename, colNum, true);		
		return colList;
	}
	
	
	/*
	public boolean writeCSV(String outFilename,List outList) throws IOException{		
		CSVWriter writer = new CSVWriter(new FileWriter(outFilename));
		
		List<String[]> data = new ArrayList<String[]>();
		data.add(new String[] {"India", "New Delhi"});
		data.add(new String[] {"United States", "Washington D.C"});
		data.add(new String[] {"Germany", "Berlin"});

		 
		writer.writeAll(data);
		 
		writer.close();
		
	}*/
	

}
