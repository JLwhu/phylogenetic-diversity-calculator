/*
 * @author JingLiu
 * @version 1.0
 * @date Jan 8, 2013
 */
package phyloGeneticAnalysis.io;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.List;

import pal.tree.SimpleTree;
import pal.tree.Tree;
import pal.tree.TreeParseException;

// TODO: Auto-generated Javadoc
/**
 * The Class newickFileIO.
 */
public class NewickFileIO {
	
	/**
	 * Input newick file.
	 *
	 * @param inFileName the in file name
	 * @return the tree
	 * @throws IOException Signals that an I/O exception has occurred.
	 * @throws TreeParseException the tree parse exception
	 */
	public Tree inputNewickFile(String inFileName) throws IOException, TreeParseException {
		Tree ret_tree = null;
		BufferedReader br = null;
		
		try {
			br = new BufferedReader(new FileReader(inFileName));
			String newick = "";
			String line;

			while ((line = br.readLine()) != null) {
				newick += line;
			}
			ret_tree = PalTreeUtils.newickToPal(newick);
		} finally {
			if (br != null)
				br.close();
		}
		return ret_tree;
		
	}
	
	/**
	 * Output newick file.
	 *
	 * @param tree the tree
	 * @param outFileName the out file name
	 * @throws IOException Signals that an I/O exception has occurred.
	 * @throws TreeParseException the tree parse exception
	 */
	public void outputNewickFile(Tree tree, String outFileName) throws IOException, TreeParseException {
		String newick = PalTreeUtils.palToNewick((SimpleTree) tree);
		PrintWriter pw = null;
		try {
			File destinationDir;
			destinationDir = new File(outFileName.substring(0,
					outFileName.lastIndexOf("\\")));
			if (!destinationDir.exists())
				destinationDir.mkdir();
			pw = new PrintWriter(outFileName);
			pw.printf(newick);
		} finally {
			if (pw != null)
				pw.close();
		}
		
	}
	
	/**
	 * Input multiple newick file.
	 *
	 * @param inFileName the in file name
	 * @return the tree
	 * @throws IOException Signals that an I/O exception has occurred.
	 * @throws TreeParseException the tree parse exception
	 */
	public List inputMultipleNewickFile(String inFileName) throws IOException, TreeParseException {
		Tree ret_tree = null;
		BufferedReader br = null;
		
		List treeList = new ArrayList();
		
		try {
			br = new BufferedReader(new FileReader(inFileName));
			String newick = "";
			String line;

			while ((line = br.readLine()) != null) {
				newick = line;
				if (line.length() > 0) {
					ret_tree = PalTreeUtils.newickToPal(newick);
					treeList.add(ret_tree);
				}
			}
			
		} finally {
			if (br != null)
				br.close();
		}
		return treeList;
		
	}
	
	/**
	 * Output Multiple tree to one newick file.
	 *
	 * @param tree the tree
	 * @param outFileName the out file name
	 * @throws IOException Signals that an I/O exception has occurred.
	 * @throws TreeParseException the tree parse exception
	 */
	public void outputMultipleNewickFile(List treelist, String outFileName) throws IOException, TreeParseException {

		PrintWriter pw = null;
		try {
			File destinationDir;
			destinationDir = new File(outFileName.substring(0,
					outFileName.lastIndexOf("\\")));
			if (!destinationDir.exists())
				destinationDir.mkdir();
			pw = new PrintWriter(outFileName);
			
			for (int i = 0;i<treelist.size();i++){
				SimpleTree tree = (SimpleTree) treelist.get(i);
				String newick = PalTreeUtils.palToNewick((SimpleTree) tree);
				pw.printf(newick);
				pw.printf("\r\n");
			}
				
		} finally {
			if (pw != null)
				pw.close();
		}
		
	}
	

}
