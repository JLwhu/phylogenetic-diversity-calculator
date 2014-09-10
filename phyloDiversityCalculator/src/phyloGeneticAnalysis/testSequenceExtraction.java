package phyloGeneticAnalysis;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import pal.tree.SimpleTree;
import phyloGeneticAnalysis.Util.GeneSequenceUtil;
import phyloGeneticAnalysis.Util.RootedTreeSubtreeIOUtils;
import phyloGeneticAnalysis.Util.RootedTreeSubtreeUtils;
import phyloGeneticAnalysis.io.CsvFileIO;
import phyloGeneticAnalysis.io.NewickFileIO;
import phyloGeneticAnalysis.io.PhySequenceFileIO;
import phyloGeneticAnalysis.io.SequenceIDFileIO;

public class testSequenceExtraction {

	public static void main(String[] args) {
		NewickFileIO nfio = new NewickFileIO();

		String treFileName = "/Users/Tao_Jiang/Documents/Jing/UFLwork/data/Forjing/BigBird.All.NewNames.7000Taxa.tre";
		String resOutTreFileName = "/Users/Tao_Jiang/Documents/Jing/UFLwork/data/Forjing/BigBird.All.NewNames.7000Taxa_out.tre";
		String subTreeNamelistFile =  "/Users/Tao_Jiang/Documents/Jing/UFLwork/data/testSubspecieNamelist.csv";
		String inSeqIDFileName = "/Users/Tao_Jiang/Documents/Jing/UFLwork/data/Forjing/TotalAccessionTable.txt";
		String resOutSeqIDFileName = "/Users/Tao_Jiang/Documents/Jing/UFLwork/data/Forjing/TotalAccessionTable_out.txt";
		String inPhyFileName = "/Users/Tao_Jiang/Documents/Jing/UFLwork/data/Forjing/AllBird.NewNames.7000Taxa.phy";
		String resinPhyFileName = "/Users/Tao_Jiang/Documents/Jing/UFLwork/data/Forjing/AllBird.NewNames.7000Taxa_out.phy";
		
		SimpleTree m_palTree, ret_subtree, subtree;
		
		
		CsvFileIO csvf = new CsvFileIO();
		List subtreeNameList;
		try {
			subtreeNameList = csvf.readCSVcolumn(subTreeNamelistFile, 0, true);

		RootedTreeSubtreeIOUtils rtsu = new RootedTreeSubtreeIOUtils();
		
	//	rtsu.inputNewickFileExtractSubtreeAndOuputNewickFile(treFileName, resOutTreFileName, subtreeNameList);		


		SequenceIDFileIO seqIDio = new SequenceIDFileIO();

		
		List speToSequenceIDList = (List) seqIDio.readSequenceIDFile(inSeqIDFileName);
		GeneSequenceUtil gsutil = new GeneSequenceUtil();
	/*	List subSequenceIDlistList = gsutil.getSpecifiedSpeiceNameToGeneIDMap(speToSequenceIDList, subtreeNameList);
		seqIDio.saveSequenceIDFile(resOutSeqIDFileName, subSequenceIDlistList);*/
		
		PhySequenceFileIO physeqio = new PhySequenceFileIO();
		
		physeqio.readExtractAndSaveToPhyFile(inPhyFileName,resinPhyFileName,subtreeNameList);
		
//		HashMap speciesToSequenceMap = physeqio.readPhyToMap(inPhyFileName);
	//	HashMap subSpeciesToSequenceMap = gsutil.getSpecifiedSpeciesNameToSequenceMap(speciesToSequenceMap, subtreeNameList);

//		physeqio.saveMapToPhy(resinPhyFileName, subSpeciesToSequenceMap);
		
		} catch (Exception e) {

			e.printStackTrace();
		}
	}

}
