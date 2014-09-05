package phyloGeneticAnalysis.community;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Enumeration;
import java.util.Vector;

import org.json.JSONArray;

import pal.tree.SimpleTree;
import pal.tree.Tree;
import pal.tree.TreeParseException;
import phyloGeneticAnalysis.community.discovery.CommunityDiscoveryContext;
import phyloGeneticAnalysis.community.discovery.KMeansClustering;
import phyloGeneticAnalysis.community.discovery.KMeansClustering.KMEANS_VARIANTS;
import phyloGeneticAnalysis.community.diversity.CommunityDiversityContext;
import phyloGeneticAnalysis.community.diversity.UnifracDistance;
import phyloGeneticAnalysis.community.diversity.UnifracDistanceNormalized_Max;
import phyloGeneticAnalysis.io.ArffIO;
import phyloGeneticAnalysis.io.newickFileIO;
import web.exception.DaoException;
import weka.core.Instance;
import weka.core.Instances;

public class CommunityDiscoveryMain {
	public static void main(String[] args) {
		String filename = "regionsfre0828_01.arff";//regionsfre1014_0.5.txt";
	//	String savefilename = "C:\\Users\\jingliu5\\UFLwork\\eclispe\\jee workspace\\phyloGenetic\\regionsfre0828_01.arff";
		String treFileName = "Bird.Test1.MLTree.tre";// args[0];"Test_8Tax1.tre"  "Bird.Test1.MLTree.tre"
		String outFileName = "DU_KMOPT1_K10_I500_01_squre1_unifracNormalizedMax1121.txt";
	//	String distanceMatrix = "distances1.txt";
		int k = 10;
		int iteration =500;
		int kmeansVairant = KMEANS_VARIANTS.SKME;   //Option 1: SKME; Option 2: SKMENVC; Option 3: SKMEVCG.
		FileWriter fout = null;
		try {
			newickFileIO nfio = new newickFileIO();
			SimpleTree m_palTree;
			m_palTree = (SimpleTree) nfio.inputNewickFile(treFileName);
			ArffIO arffio = new ArffIO();
			Instances regions = arffio.readArff(filename);
			
			/*		fout = new FileWriter(distanceMatrix);
			CommunityDiversityContext cdc = new CommunityDiversityContext(new UnifracDistance(m_palTree));
			double[][] distanceM = new double[regions.numInstances()][regions.numInstances()];
			for (int i=0;i<regions.numInstances();i++){
				for (int j=0;j<regions.numInstances();j++){
					distanceM[i][j] = 0;
				}
			}
			for (int i=0;i<regions.numInstances();i++){
				for (int j=i+1;j<regions.numInstances();j++){
					distanceM[i][j] = cdc.communityDiversity(regions.instance(i), regions.instance(j));
					System.out.println("region "+i+" to "+j+" "+distanceM[i][j]);
				}
			}
			
			for (int i=0;i<regions.numInstances();i++){
				for (int j=0;j<regions.numInstances();j++){
					fout.write(distanceM[i][j]+" ");
				}
				fout.write("\r\n");
			}
			
		    
			for (int i=0;i<regions.numInstances();i++){
				Instance instance = regions.instance(i);
				for (int j=0;j<instance.numValues();j++){
					if (instance.value(j)>0)
						instance.setValue(j, 1);
				}
			}
			arffio.saveArff(regions, savefilename);*/
			
			
		//	regions.setClassIndex(regions.numAttributes() - 1);
			
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
			
			KMeansClustering kc = new KMeansClustering(options);
			kc.setKmeansVariant(kmeansVairant);
	//		CommunityDiscoveryContext comdc = new CommunityDiscoveryContext(kc, new UnifracDistance(m_palTree));
			
			CommunityDiscoveryContext comdc = new CommunityDiscoveryContext(kc, new UnifracDistance(m_palTree));
			fout = new FileWriter(outFileName);
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
