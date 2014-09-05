package phyloGeneticAnalysis.community;

import java.io.FileWriter;
import java.io.IOException;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Set;
import java.util.Vector;

import pal.tree.SimpleTree;
import pal.tree.TreeParseException;
import phyloGeneticAnalysis.community.discovery.CommunityDiscoveryContext;
import phyloGeneticAnalysis.community.discovery.KMeansClustering;
import phyloGeneticAnalysis.community.diversity.UnifracDistance;
import phyloGeneticAnalysis.io.ArffIO;
import phyloGeneticAnalysis.io.NewickFileIO;
import phyloGeneticAnalysis.statistics.InstancesStatistics;
import weka.core.Instances;

public class testCommunityDiscoveryData {
	public static void main(String[] args) {
		String filename = "C:\\Users\\jingliu5\\UFLwork\\eclispe\\jee workspace\\phyloGenetic\\regionsfre0828_01.arff";
		String treFileName = "Bird.Test1.MLTree.tre";// args[0];"Test_8Tax1.tre"  "Bird.Test1.MLTree.tre"
		String outFileName = "Statistics.txt";
	//	String distanceMatrix = "distances1.txt";
		int k = 100;
		int iteration =500;
		FileWriter fout = null;
		try {
			NewickFileIO nfio = new NewickFileIO();
			SimpleTree m_palTree;
			m_palTree = (SimpleTree) nfio.inputNewickFile(treFileName);
			ArffIO arffio = new ArffIO();
			Instances regions = arffio.readArff(filename);
			
			InstancesStatistics is = new InstancesStatistics();
			HashMap distinctMap = is.distinctMemberNumber(regions);
			
			fout = new FileWriter(outFileName);
			fout.write("distanct instances number = "+distinctMap.size()+"\r\n");
			Set keys= distinctMap.keySet();
			Iterator it = keys.iterator();
			while (it.hasNext()){				
				fout.write(distinctMap.get(it.next())+" ");
			}
			fout.write("\r\n");
			
			
			String[] options;
			Vector result;
			result = new Vector();
			result.add("-N");
			result.add("" + k);
			result.add("-I");
			result.add("" + iteration);
			result.add("-t");
			result.add(filename);
		//	result.add("-A");
		//	result.add("phyloGeneticAnalysis.community.diversity.UnifracDistance");
			result.add("-O");
			options = (String[]) result.toArray(new String[result.size()]);
			
			CommunityDiscoveryContext comdc = new CommunityDiscoveryContext(new KMeansClustering(options), new UnifracDistance(m_palTree));

			String resulttext = comdc.commnityDiscovery(m_palTree, regions);
			

			fout.write(resulttext);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (TreeParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}finally {
			if (fout != null) {
				try {
					fout.close();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		}
	}
}
