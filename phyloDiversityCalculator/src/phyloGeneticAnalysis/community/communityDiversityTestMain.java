package phyloGeneticAnalysis.community;

import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import pal.tree.Node;
import pal.tree.RootedTreeUtils;
import pal.tree.SimpleTree;
import pal.tree.Tree;
import pal.tree.TreeParseException;
import phyloGeneticAnalysis.Exception.nameFormatException;
import phyloGeneticAnalysis.Util.RootedTreeSubtreeUtils;
import phyloGeneticAnalysis.Util.TreeUtilsAdded;
import phyloGeneticAnalysis.Util.speciesNameFormatUtils;
import phyloGeneticAnalysis.community.discovery.CommunityDiscoveryContext;
import phyloGeneticAnalysis.community.discovery.KMeansClustering;
import phyloGeneticAnalysis.community.diversity.CommunityDiversityContext;
import phyloGeneticAnalysis.community.diversity.NearestNeighborDistance;
import phyloGeneticAnalysis.community.diversity.NearestNeighborPairwiseDistance;
import phyloGeneticAnalysis.community.diversity.PhyloSorDistance;
import phyloGeneticAnalysis.community.diversity.SpeciesBetaDiversityDistanceExpectedFracA;
import phyloGeneticAnalysis.community.diversity.SpeciesBetaDiversityDistanceExpectedFrac;
import phyloGeneticAnalysis.community.diversity.SorensenIndexDistance;
import phyloGeneticAnalysis.community.diversity.UnifracDistance;
import phyloGeneticAnalysis.community.diversity.UnifracDistanceNormalized_Max;
import phyloGeneticAnalysis.community.diversity.UnifracDistanceNormalized_Random;
import phyloGeneticAnalysis.community.diversity.treeUtil.TreeUtilsMore;
import phyloGeneticAnalysis.io.ArffIO;
import phyloGeneticAnalysis.io.EnvFileIO;
import phyloGeneticAnalysis.io.csvFileIO;
import phyloGeneticAnalysis.io.newickFileIO;
import web.service.impl.PalTreeServiceImpl;
import weka.core.Instance;
import weka.core.Instances;

public class communityDiversityTestMain {
	/**
	 * The main method.
	 * 
	 * @param args
	 *            the arguments
	 */
	public static void main(String[] args) {
		// TODO Auto-generated method stub
		String treFileName =  "test.tre";//"Bird.Test1.MLTree.tre";// args[0];"Test_8Tax1.tre"   
		String resOutTreFileName = "out.tre";
		pal.tree.SimpleTree m_palTree;
		pal.tree.SimpleTree res_subtree;
		String subTreeFile = "ebird_4tax_list.csv";
		String envfile = "BirdenvA.txt";
		List<String> scientificNameList, commonNameList, subtreeNameList;
		List<String> subSciNl = null;
		List<String> subComNl = null;
		subSciNl=new ArrayList<String>();
		subComNl=new ArrayList<String>();
		
		
		String filename = "C:\\Users\\jingliu5\\UFLwork\\eclispe\\jee workspace\\phyloGenetic\\regionsfre0828_01.arff";
		String outEnvName = "testenv.txt";

		try {

			ArffIO arffio = new ArffIO();
			Instances regions = arffio.readArff(filename);

			newickFileIO nfio = new newickFileIO();
			m_palTree = (SimpleTree) nfio.inputNewickFile(treFileName);


			Set<String> A = new HashSet<String>();
			A.add(m_palTree.findNode(1).getIdentifier().getName());
			A.add(m_palTree.findNode(2).getIdentifier().getName());
			A.add(m_palTree.findNode(3).getIdentifier().getName());
			A.add(m_palTree.findNode(4).getIdentifier().getName());
			Set<String> B = new HashSet<String>();
			B.add(m_palTree.findNode(3).getIdentifier().getName());
			B.add(m_palTree.findNode(4).getIdentifier().getName());
			B.add(m_palTree.findNode(5).getIdentifier().getName());
			B.add(m_palTree.findNode(6).getIdentifier().getName());
			
			int speciesCount = m_palTree.getExternalNodeCount();		
			
			double[] attValue = new double[speciesCount];
			for (int j=0;j<speciesCount;j++){
				attValue[j] = 0;
			}
			attValue[0]=1;
			attValue[1]=1;
			attValue[2]=1;
			attValue[3]=1;
			attValue[4]=1;
			attValue[5]=1;
			attValue[6]=1;
			attValue[7]=1;
			Instance instanceA = new Instance(1, attValue);
			double[] attValueB = new double[speciesCount];
			for (int j=0;j<speciesCount;j++){
				attValueB[j] = 0;
			}
			attValueB[4]=1;
			attValueB[5]=1;
			attValueB[6]=1;
			attValueB[7]=1;
			Instance instanceB = new Instance(1, attValueB);
			
			
			EnvFileIO envio = new EnvFileIO();
			List speEnvList = envio.readEnv(envfile);
			Set<String> C = new HashSet<String>();
			Set<String> D = new HashSet<String>();
			String envfirst=null;
			for(int i=0;i<speEnvList.size();i++){
				String temp = (String) speEnvList.get(i);
				String spename = temp.substring(0, temp.indexOf("@"));
				String env =  temp.substring(temp.indexOf("@")+1);
				if (i==0) envfirst = env;
				if (env.equals(envfirst))
					C.add(spename);
				else
					D.add(spename);
			}

			
	//		envio.saveEnv(outEnvName, m_palTree, regions.instance(1), regions.instance(2));
		//	B.add(4);
			

			CommunityDiversityContext cdc = new CommunityDiversityContext(new SorensenIndexDistance(m_palTree));
			double distance = cdc.communityDiversity(A, B);
			System.out.println("Sorensen Index="+distance);
			
			cdc = new CommunityDiversityContext(new SpeciesBetaDiversityDistanceExpectedFracA(m_palTree));
			distance = cdc.communityDiversity(A, B);
			System.out.println("Species Diversity Distance Expected Fraction A="+distance);

			cdc = new CommunityDiversityContext(new SpeciesBetaDiversityDistanceExpectedFrac(m_palTree));
			distance = cdc.communityDiversity(A, B);
			System.out.println("Species Diversity Distance Expected Fraction B="+distance);
			
			cdc = new CommunityDiversityContext(new UnifracDistance(m_palTree));
			distance = cdc.communityDiversity(instanceA, instanceB);
			System.out.println("Unifrac Distance="+distance);
			
			cdc = new CommunityDiversityContext(new UnifracDistanceNormalized_Max(m_palTree));
			distance = cdc.communityDiversity(instanceA, instanceB);
			System.out.println("Unifrac Normalized Distance(Max)="+distance);
			
			cdc = new CommunityDiversityContext(new UnifracDistanceNormalized_Random(m_palTree));
			distance = cdc.communityDiversity(instanceA, instanceB);
			System.out.println("Unifrac Normalized Distance(Random)="+distance);
	//		distance = cdc.communityDiversity(regions.instance(1), regions.instance(2));
	//		System.out.println("Unifrac Distance="+distance);
	//		distance = cdc.communityDiversity(instanceA, instanceB);
	//		System.out.println("Unifrac Distance="+distance);
			
			
		/*	for (int i=0;i<1;i++){
				Instance instance1 = regions.instance(i);
				for (int j=1;j<2;j++){
					Instance instance2 = regions.instance(j);
					distance = cdc.communityDiversity(instance1, instance2);
					System.out.println("Unifrac Distance="+distance);
				}
			}*/
			
			cdc = new CommunityDiversityContext(new PhyloSorDistance(m_palTree));
			distance = cdc.communityDiversity(A, B);
			System.out.println("PhyloSor Distance="+distance);
			
			cdc = new CommunityDiversityContext(new NearestNeighborDistance(m_palTree));
			distance = cdc.communityDiversity(A, B);
			System.out.println("NN Distance="+distance);
			
			cdc = new CommunityDiversityContext(new NearestNeighborPairwiseDistance(m_palTree));
			distance = cdc.communityDiversity(A, B);
			System.out.println("NN Pairwise Distance="+distance);
			

			
	/*		int[][] regionSpecies = new int[2][m_palTree.getExternalNodeCount()];
			regionSpecies[0][0] =1;
			regionSpecies[0][1] =1;
			regionSpecies[0][2] =1;
			regionSpecies[0][3] =1;
			regionSpecies[1][4] =1;
			regionSpecies[1][5] =1;
			regionSpecies[1][6] =1;
			regionSpecies[1][7] =1;
			
			CommunityDiscoveryContext comdc = new CommunityDiscoveryContext(new KMeansClustering(), new UnifracDistance(m_palTree));
			comdc.commnityDiscovery(m_palTree, regionSpecies);
*/

		//	System.out.print(scientificNameList.size());

		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {

			e.printStackTrace();
		} catch (TreeParseException e) { // TODO Auto-generated catch block
			e.printStackTrace();
		}

	}
}
