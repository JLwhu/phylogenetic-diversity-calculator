package phyloDiversityCalculator;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Set;

import pal.tree.SimpleTree;
import phyloGeneticAnalysis.community.diversity.CommunityDiversityContext;
import phyloGeneticAnalysis.community.diversity.NearestNeighborDistance;
import phyloGeneticAnalysis.community.diversity.NearestNeighborPairwiseDistance;
import phyloGeneticAnalysis.community.diversity.PhyloSorDistance;
import phyloGeneticAnalysis.community.diversity.SorensenIndexDistance;
import phyloGeneticAnalysis.community.diversity.UnifracDistance;
import phyloGeneticAnalysis.community.diversity.UnifracDistance.UNIFRAC_VARIANTS;
import phyloGeneticAnalysis.io.EnvFileIO;
import phyloGeneticAnalysis.io.MatrixTxtFileIO;
import phyloGeneticAnalysis.io.NewickFileIO;
import processListen.ProcessSubject;

public class phyloDiversityCalculatorProcessSubject extends ProcessSubject {
	private String locFilePath;
	private String phyloFilePath;
	private String outfilename;
	private int distanceStrategy;
	private double alpha;
	

	public class DISTANCE_STRATEGY {
	public static final int UNIFRAC_DU = 1;
	public static final int UNIFRAC_DW = 2;
	public static final int UNIFRAC_D0 = 3;
	public static final int UNIFRAC_DALPHA = 4;
	public static final int UNIFRAC_DVAW = 5;	
	public static final int PhyloSor = 6;
	public static final int SorensenIndex = 7;
	public static final int NearestNeighbor = 8;
	public static final int NearestNeighborPairwise = 9;
	}
	
	phyloDiversityCalculatorProcessSubject(String phyloFilePath,
			String locFilePath, String outfilename, int distanceStrategy) {

		this.locFilePath = locFilePath;
		this.phyloFilePath = phyloFilePath;
		this.outfilename = outfilename;
		this.distanceStrategy = distanceStrategy;
	}
	
	phyloDiversityCalculatorProcessSubject(String phyloFilePath,
			String locFilePath, String outfilename, int distanceStrategy,
			double alpha) {
		
		this.locFilePath = locFilePath;
		this.phyloFilePath = phyloFilePath;
		this.outfilename = outfilename;
		this.distanceStrategy = distanceStrategy;
		this.alpha = alpha;
	}
	
    @Override
    protected Object doInBackground() throws Exception {
		int progressvalue = 0;
		
		// Input Phylogenetic Tree
        setCurrentMessage("Input Phylogenetic Tree ...");
		SimpleTree m_palTree;
		NewickFileIO nfio = new NewickFileIO();
		
		
	//	ArrayList abc = (ArrayList) nfio.inputMultipleNewickFile(phyloFilePath);
		
		m_palTree = (SimpleTree) nfio
				.inputNewickFile(phyloFilePath);

		
		progressvalue += 2;
		setCurrentPercentage(progressvalue);
		
		// Input Enviroment File
        setCurrentMessage("Input Enviroment File ...");
		EnvFileIO envio = new EnvFileIO();
		HashMap resultMap = envio.readEnvToMap(locFilePath);
		ArrayList envList = (ArrayList) resultMap
				.get("EnvList");
		HashMap envNameToSpeciesSetMap = (HashMap) resultMap
				.get("EnvToSpeciesMap");
		HashMap envNameToSpeciesAbundanceMap = (HashMap) resultMap
				.get("EnvNameToSpeciesAbundanceMap");
		
		progressvalue += 2;
		setCurrentPercentage(progressvalue);
		
    	int envCount = envList.size();
		double[][] distanceMatrix = new double[envCount][envCount];

		
		// Calculate the phylogenetic distance matrix
        setCurrentMessage("Calculate the phylogenetic distance matrix ...");
        
		CommunityDiversityContext cdc;
		
		if (distanceStrategy == DISTANCE_STRATEGY.UNIFRAC_DU) {
			UnifracDistance distanceStrategy = new UnifracDistance(
					m_palTree);
			cdc = new CommunityDiversityContext(
					distanceStrategy);
		} else if (distanceStrategy == DISTANCE_STRATEGY.UNIFRAC_DW) {
			UnifracDistance distanceStrategy = new UnifracDistance(
					m_palTree);
			distanceStrategy.setOption(UNIFRAC_VARIANTS.DW);
			cdc = new CommunityDiversityContext(
					distanceStrategy);
		} else if (distanceStrategy == DISTANCE_STRATEGY.UNIFRAC_D0) {
			UnifracDistance distanceStrategy = new UnifracDistance(
					m_palTree);
			distanceStrategy.setOption(UNIFRAC_VARIANTS.D0);
			cdc = new CommunityDiversityContext(
					distanceStrategy);
		} else if (distanceStrategy == DISTANCE_STRATEGY.UNIFRAC_DVAW) {
			UnifracDistance distanceStrategy = new UnifracDistance(
					m_palTree);
			distanceStrategy
					.setOption(UNIFRAC_VARIANTS.DVAW);
			cdc = new CommunityDiversityContext(
					distanceStrategy);
		} else if (distanceStrategy == DISTANCE_STRATEGY.UNIFRAC_DALPHA) {
			UnifracDistance distanceStrategy = new UnifracDistance(
					m_palTree);
			distanceStrategy
					.setOption(UNIFRAC_VARIANTS.DALPHA);
			distanceStrategy.setAlpha(alpha);
			cdc = new CommunityDiversityContext(
					distanceStrategy);
		} else if (distanceStrategy == DISTANCE_STRATEGY.PhyloSor) {
			PhyloSorDistance distanceStrategy = new PhyloSorDistance(
					m_palTree);
			cdc = new CommunityDiversityContext(
					distanceStrategy);
		} else if (distanceStrategy == DISTANCE_STRATEGY.SorensenIndex) {
			SorensenIndexDistance distanceStrategy = new SorensenIndexDistance(
					m_palTree);
			cdc = new CommunityDiversityContext(
					distanceStrategy);
		} else if (distanceStrategy == DISTANCE_STRATEGY.NearestNeighbor) {
			NearestNeighborDistance distanceStrategy = new NearestNeighborDistance(
					m_palTree);
			cdc = new CommunityDiversityContext(
					distanceStrategy);
		} else {
			NearestNeighborPairwiseDistance distanceStrategy = new NearestNeighborPairwiseDistance(
					m_palTree);
			cdc = new CommunityDiversityContext(
					distanceStrategy);
		}
		
		
		
		for (int i = 0; i < envCount; i++) {
			for (int j = i + 1; j < envCount; j++) {
				progressvalue += 190/(envCount*(envCount-1));
		        setCurrentPercentage(progressvalue);
				Set<String> A = (Set<String>) envNameToSpeciesSetMap
						.get(envList.get(i));
				Set<String> B = (Set<String>) envNameToSpeciesSetMap
						.get(envList.get(j));

				HashMap speciesToAbundanceMap = (HashMap) envNameToSpeciesAbundanceMap
						.get(envList.get(j));
				
				double distance = cdc.communityDiversity(A,
						B, speciesToAbundanceMap);
				distanceMatrix[i][j] = distance;

				
			}
		}
		
		

		// output distance matrix
        setCurrentMessage("Output distance matrix ...");
		MatrixTxtFileIO matrixfile = new MatrixTxtFileIO();
		matrixfile.saveMatrixFile(outfilename, envList,
				distanceMatrix);
		
		progressvalue += 1;
		setCurrentPercentage(progressvalue);


        setCurrentPercentage(100);
        setCurrentMessage("End Saving Matrix to File. \r\nDone!");
        return distanceMatrix;
    }
}
