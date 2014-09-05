package phyloGeneticAnalysis.community;

import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.util.HashSet;
import java.util.Random;
import java.util.Set;

import pal.tree.SimpleTree;
import pal.tree.TreeParseException;
import phyloGeneticAnalysis.community.diversity.CommunityDiversityContext;
import phyloGeneticAnalysis.community.diversity.NearestNeighborDistance;
import phyloGeneticAnalysis.community.diversity.NearestNeighborPairwiseDistance;
import phyloGeneticAnalysis.community.diversity.PhyloSorDistance;
import phyloGeneticAnalysis.community.diversity.SorensenIndexDistance;
import phyloGeneticAnalysis.community.diversity.UnifracDistance;
import phyloGeneticAnalysis.community.diversity.UnifracDistance.UNIFRAC_VARIANTS;
import phyloGeneticAnalysis.community.diversity.UnifracDistanceNormalized_Max;
import phyloGeneticAnalysis.community.diversity.UnifracDistanceNormalized_Random;
import phyloGeneticAnalysis.community.diversity.treeUtil.TreeUtilsMore;
import phyloGeneticAnalysis.community.topologyGeneration.TreeTopolgyGenerator;
import phyloGeneticAnalysis.io.NewickFileIO;

public class UnifracTopologyTestMain {
	public static void main(String[] args) {
		String treFileName = "Bird.Test1.MLTree.tre";// args[0];"Test_8Tax1.tre"
		String resOutFileName = "RandomTree_Variance_vs_Unifrac_treeRep50_SetRep50_Insert0.002_0.01_0.1_Set500.txt";
		// String resOutFile1Name =
		// "RandomTree_unifrac_Normalized_Max_10_1500_.txt";
		// String resOutFile2Name =
		// "RandomTree_unifrac_Normalized_Random_10_1500_.txt";
		pal.tree.SimpleTree m_palTree;

		FileWriter fout = null;
		FileWriter fout1 = null;
		FileWriter fout2 = null;

		// Tree Generation Parameters
		int randonTreeGenTimes = 50;
		int genTreeSize = 6410;
		double minInsertRate = 0.002;
		double maxInsertRate = 0.1;
		double insertRateStep = 0.01;
		boolean uniformBranchLength = false;

		double minLeftRate = 0.49999;
		double maxLeftRate = 0.49999;
		double leftRateStep = 0.00001;

		// Set Size
		int setSizeIncreaseStep = 10;
		int repeatTimes = 50;
		int MinSetSize = 500;
		int MaxSetSize = 500;// 1500;		


		try {
			
			fout = new FileWriter(resOutFileName);
			double sum = 0;
			long start = System.currentTimeMillis();

			/*
			 * newickFileIO nfio = new newickFileIO(); m_palTree = (SimpleTree)
			 * nfio.inputNewickFile(treFileName);
			 * System.out.println(m_palTree.getExternalNodeCount());
			 */

			for (double insertrate = minInsertRate; insertrate <= maxInsertRate; insertrate += insertRateStep) {
				double leftrate = (1 - insertrate) / 2;
				double balanceAvg = 0;
				// for (double leftrate = minLeftRate; leftrate <= maxLeftRate;
				// leftrate += leftRateStep) {
				for (double k = 0; k < randonTreeGenTimes; k++) {
					m_palTree = new TreeTopolgyGenerator(genTreeSize,
							insertrate, leftrate);
					SimpleTree UBLtree = (SimpleTree) m_palTree.getCopy();
					TreeUtilsMore.setBrachlengthToOne(UBLtree); 
					
					double balancevalue = TreeUtilsMore
							.getTreeBalanceValue(m_palTree);	
					balanceAvg += balancevalue;
					fout.write(balancevalue+ " ");
					System.out.print(balancevalue+ " ");
					
					double weightedbalancevalue = TreeUtilsMore
							.getTreeWeightedBalanceValue(m_palTree);
					fout.write(weightedbalancevalue+ " ");
					System.out.print(weightedbalancevalue+ " ");
					
					double weightedbalancevalueUBL = TreeUtilsMore
							.getTreeWeightedBalanceValue(UBLtree);
					fout.write(weightedbalancevalueUBL+ " ");
					System.out.print(weightedbalancevalueUBL+ " ");
					
					double variance  = TreeUtilsMore.getTreeRootToLeafPathLengthsVariance(m_palTree);
					fout.write(variance+ " ");
					System.out.print(variance+ " ");
					
					double varianceUBL  = TreeUtilsMore.getTreeRootToLeafPathLengthsVariance(UBLtree);
					fout.write(varianceUBL+ " ");
					System.out.print(varianceUBL+ " ");
					
					for (int Acount = MinSetSize; Acount <= MaxSetSize; Acount += setSizeIncreaseStep) {
						int Bcount = Acount;
						// for (int Bcount = 10; Bcount <= 100; Bcount +=
						// step) {
						double unifracAvg = 0;
						double unifracAvgUBL = 0;
						double unifracDW = 0;
						double unifracDWUBL = 0;
						double unifracD0 = 0;
						double unifracD0UBL = 0;
						double unifracDAlpha = 0;
						double unifracDAlphaUBL = 0;
						double unifracDVAW = 0;
						double unifracDVAWUBL = 0;
						double SI = 0;
						double SIUBL = 0;
						double PhyloSor = 0;
						double PhyloSorUBL = 0;
						double nn = 0;
						double nnUBL = 0;
						double nnPair = 0;
						double nnPairUBL = 0;


						for (int j = 0; j < repeatTimes; j++) {
							int[] idxArray = new int[m_palTree
									.getExternalNodeCount()];
							for (int i = 0; i < m_palTree
									.getExternalNodeCount(); i++) {
								idxArray[i] = i;
							}

							Random RandomO = new Random(
									System.currentTimeMillis());

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
							for (int i = 0; i < m_palTree
									.getExternalNodeCount(); i++) {
								idxArray[i] = i;
							}
							// for (int i = idxArray.length - Acount - 1; i
							// >=
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
					//		System.out.println("A=" + Acount + " B=" + Bcount
					//				+ " Unifrac=" + distance);							

							unifracAvg+=distance;
							
							CommunityDiversityContext cdcUBL = new CommunityDiversityContext(
									new UnifracDistance(UBLtree));
							double distanceUBL = cdcUBL.communityDiversity(A, B);
							unifracAvgUBL+=distanceUBL;
						//	fout.write(distance + " ");
							
							
			/*				UnifracDistance uddw = new UnifracDistance(m_palTree);
							uddw.setOption(UNIFRAC_VARIANTS.DW);
							cdc = new CommunityDiversityContext(
									uddw);
							distance = cdc.communityDiversity(A, B);
							unifracDW+=distance;
							
							uddw = new UnifracDistance(UBLtree);
							uddw.setOption(UNIFRAC_VARIANTS.DW);
							cdc = new CommunityDiversityContext(
									uddw);
							distance = cdc.communityDiversity(A, B);
							unifracDWUBL+=distance;

							UnifracDistance udd0 = new UnifracDistance(m_palTree);
							udd0.setOption(UNIFRAC_VARIANTS.D0);
							cdc = new CommunityDiversityContext(
									udd0);
							distance = cdc.communityDiversity(A, B);
							unifracD0+=distance;
							
							udd0 = new UnifracDistance(UBLtree);
							udd0.setOption(UNIFRAC_VARIANTS.D0);
							cdc = new CommunityDiversityContext(
									udd0);
							distance = cdc.communityDiversity(A, B);
							unifracD0UBL+=distance;
							
							UnifracDistance uddalpha = new UnifracDistance(m_palTree);
							uddalpha.setOption(UNIFRAC_VARIANTS.DALPHA);
							cdc = new CommunityDiversityContext(
									uddalpha);
							distance = cdc.communityDiversity(A, B);
							unifracDAlpha+=distance;
							
							uddalpha = new UnifracDistance(UBLtree);
							uddalpha.setOption(UNIFRAC_VARIANTS.DALPHA);
							cdc = new CommunityDiversityContext(
									uddalpha);
							distance = cdc.communityDiversity(A, B);
							unifracDAlphaUBL+=distance;
							
							UnifracDistance udDVAW = new UnifracDistance(m_palTree);
							udDVAW.setOption(UNIFRAC_VARIANTS.DVAW);
							cdc = new CommunityDiversityContext(
									udDVAW);
							distance = cdc.communityDiversity(A, B);
							unifracDVAW+=distance;
							
							udDVAW = new UnifracDistance(UBLtree);
							udDVAW.setOption(UNIFRAC_VARIANTS.DVAW);
							cdc = new CommunityDiversityContext(
									udDVAW);
							distance = cdc.communityDiversity(A, B);
							unifracDVAWUBL+=distance;
							
							cdc = new CommunityDiversityContext(new SorensenIndexDistance(m_palTree));
							distance = cdc.communityDiversity(A, B);
						//	System.out.println("Sorensen Index="+distance);							
							SI+=distance;
							
							cdc = new CommunityDiversityContext(new SorensenIndexDistance(UBLtree));
							distance = cdc.communityDiversity(A, B);
						//	System.out.println("Sorensen Index="+distance);							
							SIUBL+=distance;	
							
							cdc = new CommunityDiversityContext(new PhyloSorDistance(m_palTree));
							distance = cdc.communityDiversity(A, B);
						//	System.out.println("PhyloSor Distance="+distance);
							PhyloSor+=distance;
							
							cdc = new CommunityDiversityContext(new PhyloSorDistance(UBLtree));
							distance = cdc.communityDiversity(A, B);
						//	System.out.println("PhyloSor Distance="+distance);
							PhyloSorUBL+=distance;
							
							cdc = new CommunityDiversityContext(new NearestNeighborDistance(m_palTree));
							distance = cdc.communityDiversity(A, B);
					//		System.out.println("NN Distance="+distance);
							nn+=distance;
							
							cdc = new CommunityDiversityContext(new NearestNeighborDistance(UBLtree));
							distance = cdc.communityDiversity(A, B);
							nnUBL+=distance;
							
							cdc = new CommunityDiversityContext(new NearestNeighborPairwiseDistance(m_palTree));
							distance = cdc.communityDiversity(A, B);
					//		System.out.println("NN Pairwise Distance="+distance);
							nnPair += distance;
							
							cdc = new CommunityDiversityContext(new NearestNeighborPairwiseDistance(UBLtree));
							distance = cdc.communityDiversity(A, B);
							nnPairUBL += distance;
*/
							/*
							 * cdc = new CommunityDiversityContext( new
							 * UnifracDistanceNormalized_Max(m_palTree));
							 * distance = cdc.communityDiversity(A, B); System
							 * .out.println("  Normalized Unifrac(Max)=" +
							 * distance); fout1.write(distance + " ");
							 * 
							 * cdc = new CommunityDiversityContext( new
							 * UnifracDistanceNormalized_Random(m_palTree));
							 * distance = cdc.communityDiversity(A, B); System
							 * .out.println("  Normalized Unifrac(Random)=" +
							 * distance); fout2.write(distance + " ");
							 */
						}
						unifracAvg/=repeatTimes;
						fout.write(unifracAvg+ " ");
						System.out.print(unifracAvg+ " ");
						
						unifracAvgUBL/=repeatTimes;
						fout.write(unifracAvgUBL+ " ");
						System.out.print(unifracAvgUBL+ " ");
						
			/*			unifracDW/=repeatTimes;
						fout.write(unifracDW+ " ");
						System.out.print(unifracDW+ " ");
						
						unifracDWUBL/=repeatTimes;
						fout.write(unifracDWUBL+ " ");
						System.out.print(unifracDWUBL+ " ");
						
						unifracD0/=repeatTimes;
						fout.write(unifracD0+ " ");
						System.out.print(unifracD0+ " ");
						
						unifracD0UBL/=repeatTimes;
						fout.write(unifracD0UBL+ " ");
						System.out.print(unifracD0UBL+ " ");
						
						unifracDAlpha/=repeatTimes;
						fout.write(unifracDAlpha+ " ");
						System.out.print(unifracDAlpha+ " ");
						
						unifracDAlphaUBL/=repeatTimes;
						fout.write(unifracDAlphaUBL+ " ");
						System.out.print(unifracDAlphaUBL+ " ");
						
						unifracDVAW/=repeatTimes;
						fout.write(unifracDVAW+ " ");
						System.out.print(unifracDVAW+ " ");
						
						unifracDVAWUBL/=repeatTimes;
						fout.write(unifracDVAWUBL+ " ");
						System.out.print(unifracDVAWUBL+ " ");

						SI/=repeatTimes;
						fout.write(SI+ " ");
						System.out.print(SI+ " ");
						
						SIUBL/=repeatTimes;
						fout.write(SIUBL+ " ");
						System.out.print(SIUBL+ " ");

						PhyloSor/=repeatTimes;
						fout.write(PhyloSor+ " ");
						System.out.print(PhyloSor+ " ");
						
						PhyloSorUBL/=repeatTimes;
						fout.write(PhyloSorUBL+ " ");
						System.out.print(PhyloSorUBL+ " ");
						
						nn/=repeatTimes;
						fout.write(nn+ " ");
						System.out.print(nn+ " ");
						
						nnUBL/=repeatTimes;
						fout.write(nnUBL+ " ");
						System.out.print(nnUBL+ " ");
						
						nnPair/=repeatTimes;
						fout.write(nnPair+ " ");
						System.out.print(nnPair+ " ");
						
						nnPairUBL/=repeatTimes;
						fout.write(nnPairUBL+ " ");
						System.out.print(nnPairUBL+ " ");*/

				//		fout1.write("\r\n");
				//		fout2.write("\r\n");
						// }

					}
					fout.write("\r\n");
					System.out.print("\r\n");
				}
				balanceAvg/=randonTreeGenTimes;
				fout.write("\r\n");
				System.out.print("\r\n");
			}
			// }
		} catch (Exception e) {

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
		}

	}
}
