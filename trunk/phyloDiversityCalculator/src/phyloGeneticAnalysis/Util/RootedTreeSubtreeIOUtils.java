package phyloGeneticAnalysis.Util;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.List;

import pal.tree.SimpleTree;
import pal.tree.Tree;
import pal.tree.TreeParseException;
import phyloGeneticAnalysis.io.NewickFileIO;
import phyloGeneticAnalysis.io.PalTreeUtils;

public class RootedTreeSubtreeIOUtils extends RootedTreeSubtreeUtils{

	private NewickFileIO nio = new NewickFileIO();

	public void inputNewickFileExtractSubtreeAndOuputNewickFile(
			String inFileName, String outFileName, List speciesNameList)
			throws Exception {
		Tree ret_tree = null;
		BufferedReader br = null;
		PrintWriter pw = null;
		
		try {
			br = new BufferedReader(new FileReader(inFileName));
			String newick = "";
			String line;			

		/*	File destinationDir;
			String desDir;
			if (outFileName.lastIndexOf("/")>=0){
				desDir = outFileName.substring(0,
					outFileName.lastIndexOf("/"));
				destinationDir = new File(desDir);
				if (!destinationDir.exists())
					destinationDir.mkdir();
				}
			else if ((outFileName.lastIndexOf("\\")>=0)){
				desDir = outFileName.substring(0,
						outFileName.lastIndexOf("\\"));
				destinationDir = new File(desDir);
				if (!destinationDir.exists())
					destinationDir.mkdir();
			}*/

			
			pw = new PrintWriter(outFileName);

			while ((line = br.readLine()) != null) {
				newick = line;
				if (line.length() > 0) {
					ret_tree = PalTreeUtils.newickToPal(newick);
					SimpleTree subtree = (SimpleTree) getSpecifiedSubTree(ret_tree,speciesNameList);
					String newickout = PalTreeUtils.palToNewick((SimpleTree) subtree);
					pw.printf(newickout);
					pw.printf("\r\n");
				}
			}
			
		} finally {
			if (br != null)
				br.close();
			if (pw != null)
				pw.close();
		}	

	}

}
