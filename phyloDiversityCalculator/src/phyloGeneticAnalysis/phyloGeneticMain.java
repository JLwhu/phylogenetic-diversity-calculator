
package phyloGeneticAnalysis;

import java.util.ArrayList;
import java.util.List;
import java.io.FileNotFoundException;
import java.io.IOException;

import pal.tree.RootedTreeUtils;
import pal.tree.SimpleTree;
import pal.tree.Tree;
import pal.tree.TreeParseException;
import phyloGeneticAnalysis.Exception.nameFormatException;
import phyloGeneticAnalysis.Util.RootedTreeSubtreeUtils;
import phyloGeneticAnalysis.Util.TreeUtilsAdded;
import phyloGeneticAnalysis.Util.speciesNameFormatUtils;
import phyloGeneticAnalysis.io.csvFileIO;
import phyloGeneticAnalysis.io.newickFileIO;
import web.service.impl.PalTreeServiceImpl;



public class phyloGeneticMain {

	/**
	 * The main method.
	 * 
	 * @param args
	 *            the arguments
	 */
	public static void main(String[] args) {
		// TODO Auto-generated method stub
		String treFileName = "Test_8Tax.tre";// args[0];
		String restreFileName = "Test_4Tax.tre";

		String resOutTreFileName = "out.tre";
		pal.tree.SimpleTree m_palTree;
		pal.tree.SimpleTree res_subtree;
		String csvFile = "AllSpeciesList_ToUse2.csv";
		String subTreeFile = "ebird_4tax_list.csv";
		List<String> scientificNameList, commonNameList, subtreeNameList;
		List<String> subSciNl = null;
		List<String> subComNl = null;
		subSciNl=new ArrayList<String>();
		subComNl=new ArrayList<String>();

		Tree ret_subtree;

		try {

			newickFileIO nfio = new newickFileIO();
			m_palTree = (SimpleTree) nfio.inputNewickFile(treFileName);
			res_subtree = (SimpleTree) nfio.inputNewickFile(restreFileName);

			csvFileIO csvf = new csvFileIO();

			int i, count;
		/*	scientificNameList = csvf.readCSVcolumn(csvFile, 0, false);
			commonNameList = csvf.readCSVcolumn(csvFile, 1, false);
			Hashtable nametable = new Hashtable();
			i = 0;
			count = scientificNameList.size();
			while (count > 0 && i < count) {
				nametable.put(scientificNameList.get(i), commonNameList.get(i));
				i++;
			}*/

			subtreeNameList = csvf.readCSVcolumn(subTreeFile, 1, true);

			speciesNameFormatUtils.nameListFormat(subtreeNameList, subComNl, subSciNl);
			RootedTreeSubtreeUtils rtsu = new RootedTreeSubtreeUtils();
			ret_subtree = rtsu.getSpecifiedSubTree(m_palTree, subSciNl);
			System.out.print(TreeUtilsAdded.computeDiversity(m_palTree)+"\r\n");
			System.out.print(TreeUtilsAdded.computeDiversity(ret_subtree)+"\r\n");
			if (RootedTreeUtils.equal(ret_subtree.getRoot(),res_subtree.getRoot()))
				System.out.print("Same Result!"+"\r\n");
			else
				System.out.print("Not Same Result, check!"+"\r\n");
			nfio.outputNewickFile(ret_subtree, resOutTreFileName);
			
			PalTreeServiceImpl paltreedisplay = new PalTreeServiceImpl();
			paltreedisplay.setTree(ret_subtree);
			paltreedisplay.graphicsGeneration();


		//	System.out.print(scientificNameList.size());

		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {

			e.printStackTrace();
		} catch (TreeParseException e) { // TODO Auto-generated catch block
			e.printStackTrace();
		} catch (nameFormatException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

}
