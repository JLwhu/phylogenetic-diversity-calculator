package phyloGeneticAnalysis.community;

import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Random;
import java.util.Set;

import pal.tree.SimpleTree;
import pal.tree.Tree;
import pal.tree.TreeParseException;
import phyloGeneticAnalysis.Util.RootedTreeSubtreeUtils;
import phyloGeneticAnalysis.Util.speciesNameFormatUtils;
import phyloGeneticAnalysis.community.discovery.CommunityDiscoveryContext;
import phyloGeneticAnalysis.community.discovery.KMeansClustering;
import phyloGeneticAnalysis.community.diversity.CommunityDiversityContext;
import phyloGeneticAnalysis.community.diversity.UnifracDistance;
import phyloGeneticAnalysis.community.diversity.UnifracDistanceNormalized_Max;
import phyloGeneticAnalysis.community.diversity.UnifracDistanceNormalized_Random;
import phyloGeneticAnalysis.community.diversity.treeUtil.TreeUtilsMore;
import phyloGeneticAnalysis.io.csvFileIO;
import phyloGeneticAnalysis.io.newickFileIO;
import web.service.impl.PalTreeServiceImpl;
import weka.core.Instance;

public class UnifracTestMain {
	public static void main(String[] args) {
		// TODO Auto-generated method stub
		String treFileName = "Bird.Test1.MLTree.tre";// args[0];"Test_8Tax1.tre"
														// "Bird.Test1.MLTree.tre""test.tre"
		String resOutFileName = "unifrac_SetSize10_1000_Rep500_1119.txt";
		String resOutBalanceFileName = "balance_SetSize10_1000_Rep500_1119.txt";
	//	String resOutFile1Name = "unifrac_Normalized_Max_10_1000_Rep500.txt";
	//	String resOutFile2Name = "unifrac_Normalized_Random_10_1000_Rep500.txt";
		pal.tree.SimpleTree m_palTree;

		FileWriter fout = null;
		FileWriter fout1 = null;
		FileWriter fout2 = null;
		FileWriter fbalanceOut = null;

		int step = 10;
		int repeatTimes = 500;
		int MinSetSize = 10;
		int MaxSetSize = 1000;

		try {
			newickFileIO nfio = new newickFileIO();
			m_palTree = (SimpleTree) nfio.inputNewickFile(treFileName);
			
	//		TreeUtilsMore.setBrachlengthToOne(m_palTree); 
			
			fout = new FileWriter(resOutFileName);
	//		fout1 = new FileWriter(resOutFile1Name);
	//		fout2 = new FileWriter(resOutFile2Name);
			fbalanceOut = new FileWriter(resOutBalanceFileName);
			
			for (int Acount = MinSetSize; Acount <= MaxSetSize; Acount += step) {
				int Bcount = Acount;
				// for (int Bcount = 10; Bcount <= 100; Bcount += step) {
				double avgUnifrac = 0;
				double avgUnifracMax = 0;
				double avgUnifracRand = 0;
				double avgBalance = 0;
				double avgWeightedBalance = 0;
				for (int j = 0; j < repeatTimes; j++) {
					int[] idxArray = new int[m_palTree.getExternalNodeCount()];
					for (int i = 0; i < m_palTree.getExternalNodeCount(); i++) {
						idxArray[i] = i;
					}

					Random RandomO = new Random(System.currentTimeMillis());

					Set<String> A = new HashSet<String>();
					for (int i = idxArray.length - 1; i >= idxArray.length
							- Acount; i--) {
						int index = RandomO.nextInt(i + 1);
						A.add(m_palTree.findNode(idxArray[index] + 1)
								.getIdentifier().getName());
						int temp = idxArray[index];
						idxArray[index] = idxArray[i];
						idxArray[i] = temp;
					}
					Set<String> B = new HashSet<String>();
					for (int i = 0; i < m_palTree.getExternalNodeCount(); i++) {
						idxArray[i] = i;
					}
					// for (int i = idxArray.length - Acount - 1; i >=
					// idxArray.length
					// - Acount - Bcount; i--) {

					for (int i = idxArray.length - 1; i >= idxArray.length
							- Bcount; i--) {
						int index = RandomO.nextInt(i + 1);
						B.add(m_palTree.findNode(idxArray[index] + 1)
								.getIdentifier().getName());
						int temp = idxArray[index];
						idxArray[index] = idxArray[i];
						idxArray[i] = temp;
					}

					CommunityDiversityContext cdc = new CommunityDiversityContext(
							new UnifracDistance(m_palTree));
					double distance = cdc.communityDiversity(A, B);
					System.out.println("A="+Acount+" B="+Bcount+" Unifrac=" + distance);
			//		fout.write(distance + " ");		
					avgUnifrac += distance;

					cdc = new CommunityDiversityContext(
							new UnifracDistanceNormalized_Max(m_palTree));
					distance = cdc.communityDiversity(A, B);
					System.out.println("  Normalized Unifrac(Max)=" + distance);
			//		fout.write(distance + " ");
					avgUnifracMax += distance;
			//		fout1.write(distance + " ");
					
					cdc = new CommunityDiversityContext(
							new UnifracDistanceNormalized_Random(m_palTree));
					distance = cdc.communityDiversity(A, B);
					System.out.println("  Normalized Unifrac(Random)=" + distance);
			//		fout.write(distance + " ");
					avgUnifracRand += distance;
			//		fout2.write(distance + " ");
					
					UnifracTestMain utm = new UnifracTestMain();
					
					SimpleTree subtree = (SimpleTree) utm.getSubTree(m_palTree,A,B);
					double balance = TreeUtilsMore.getTreeBalanceValue(subtree);
					avgBalance += balance;
					System.out.println("Blance="+balance);
				//	fbalanceOut.write(balance + " ");
					balance = TreeUtilsMore.getTreeWeightedBalanceValue(subtree);
				//	fbalanceOut.write(balance + " ");
					avgWeightedBalance += balance;
					System.out.println("Weighted Blance="+balance);
					
				}
				fout.write(avgUnifrac/repeatTimes+" "+avgUnifracMax/repeatTimes+" "+avgUnifracRand/repeatTimes+" "+"\r\n");
		//		fout1.write("\r\n");
		//		fout2.write("\r\n");
				fbalanceOut.write(avgBalance/repeatTimes+" "+avgWeightedBalance/repeatTimes+"\r\n");
				// }
			}

		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {

			e.printStackTrace();
		} catch (TreeParseException e) { // TODO Auto-generated catch block
			e.printStackTrace();
		} finally {
			if (fout != null) {
				try {
					fout.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
			if (fout1 != null) {
				try {
					fout1.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
			if (fout2 != null) {
				try {
					fout2.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
			
			if (fbalanceOut != null) {
				try {
					fbalanceOut.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}

	}
	
	
	public Tree getSubTree(SimpleTree m_palTree, Set<String> A, Set<String> B){
		Tree ret_subtree;
		
		Set<String> union = new HashSet<String>();
		union.addAll(A);
		union.addAll(B);

		RootedTreeSubtreeUtils rtsu = new RootedTreeSubtreeUtils();
		ret_subtree = rtsu.getSpecifiedSubTree(m_palTree, union);
		return ret_subtree;
	}
}
