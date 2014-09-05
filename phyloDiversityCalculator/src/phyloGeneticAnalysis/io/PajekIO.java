package phyloGeneticAnalysis.io;
import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
public class PajekIO {
	public void saveAsPajek(double[][] distanceMatrix, String[] labels,
			String outFileName) throws IOException {
		BufferedWriter out = new BufferedWriter(new FileWriter(outFileName));
		int nodeCount = distanceMatrix.length;
		out.write("*Vertices " + nodeCount);
		out.newLine();
		for (int i=0;i<nodeCount;i++){
			if (labels!=null)
				out.write(i+" \"" + labels[i] + "\"");	
			else
				out.write(i);
			out.newLine();
		}
		out.write("*Edges");
		out.newLine();
		for (int i=0;i<nodeCount;i++){
			for (int j=0;j<nodeCount;j++){
				if (distanceMatrix[i][j]!=0){
		            out.write(i + " " + j + " " + distanceMatrix[i][j]);
		            out.newLine();
				}					
			}
		}
		out.close();
	}
}
