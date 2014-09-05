package phyloGeneticAnalysis.io;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;

import weka.core.Instances;

public class ArffIO {
	public Instances readArff(String filename) throws IOException{
		BufferedReader reader = new BufferedReader(new FileReader(filename));
		Instances data = new Instances(reader);
		reader.close();
		// setting class attribute
		data.setClassIndex(data.numAttributes() - 1);
		return data;
	}
	public void saveArff(Instances dataSet, String filename)
			throws IOException {
		BufferedWriter writer = new BufferedWriter(new FileWriter(filename));
		writer.write(dataSet.toString());
		writer.flush();
		writer.close();
	}
}
