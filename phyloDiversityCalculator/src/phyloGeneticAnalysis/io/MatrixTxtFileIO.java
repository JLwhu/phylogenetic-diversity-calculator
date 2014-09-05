package phyloGeneticAnalysis.io;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;

public class MatrixTxtFileIO {
	public void saveMatrixFile(String filename, ArrayList headers, double[][] matrix)
			throws IOException {
		BufferedWriter writer = new BufferedWriter(new FileWriter(filename));
		int count = matrix.length;
		for (int i = 0; i < count; i++) {
			writer.write((String)headers.get(i));
			writer.write("\t");
			for (int j = 0; j < count; j++) {
				writer.write(String.valueOf(matrix[i][j]));
				writer.write("\t");
			}
			writer.write("\r\n");
		}
		writer.flush();
		writer.close();
	}
}
