/*
	Copied this code from CIPRes, changed package name only.
	Added "import pal.tree.*;" to get TreeParseException class.
*/


package phyloGeneticAnalysis.io;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.PrintWriter;
import pal.tree.*;

public class UTree
{
	private pal.tree.SimpleTree m_palTree;

	public UTree(final pal.tree.SimpleTree palTree)
	{
		m_palTree = palTree;
	}

	public UTree(final String newick) throws TreeParseException
	{
		m_palTree = PalTreeUtils.newickToPal(newick);
	}

	public UTree(File file) throws TreeParseException, IOException, FileNotFoundException
	{
		BufferedReader br = null;
		try
		{
			br = new BufferedReader(new FileReader(file));
			String newick = ""; 
			String line;
			while ((line = br.readLine()) != null)
			{
				newick +=line;
			}
			m_palTree = PalTreeUtils.newickToPal(newick);
		}
		finally
		{
			if (br != null) br.close();
		}
	}

	public String asNewick()
	{
		return PalTreeUtils.palToNewick(m_palTree);
	}

	public static final int MAP_NONE = 0;

	public static final int MAP_NAMES_TO_INDICES = 1;

	public static final int MAP_INDICES_TO_NAMES = 2;

	public String asNewick(final int mapping, final String[] taxaMgr)
	{
		return PalTreeUtils.palToNewick(m_palTree, PalTreeUtils.hasBrLens(m_palTree), mapping, taxaMgr, true);
	}

	/**
	 * getStrLeafsetTaxa
	 * @return String[] - array of taxa names comprising the tree
	 */
	public String[] getStrLeafsetTaxa()
	{
		return PalTreeUtils.getStrLeafsetTaxa(m_palTree);
	}

	public static void newickToFile(File file, String newick) throws FileNotFoundException
	{
		PrintWriter pw = null;
		try
		{
			pw = new PrintWriter(file);
			pw.printf(newick);
		}
		finally
		{
			if (pw != null) pw.close();
		}
	}

	/**
		See static reroot.
	*/
	public void reroot(String[] outgroups)
	{
		m_palTree = (pal.tree.SimpleTree)PalTreeUtils.reroot(m_palTree,outgroups);
	}

	/**
		Uses Pal library's TreeManipulator to reroot the tree.

		If outgroups is null or empty string, just copies the input file to output
		file (it uses pal to do this so that branch lengths will be written using
		pal's formatting regardless of whether the tree is actually rerooted).
		
		Throws IllegalArgument exception if none of the taxa specified are part
		of the tree.

		Ignores outgroup taxa that aren't in the tree.

		PAL documentation says that MRCA of specified outgroup taxa "influences"
		the rooting.  Not sure why they don't say that MRCA *is* the outgroup. 
	*/
	public static void reroot(File infile, File outfile, String outgroups[]) throws
		FileNotFoundException, IOException, TreeParseException
	{
		UTree utree = new UTree(infile);
		if ((outgroups != null) && (outgroups.length > 0))
		{
			utree.reroot(outgroups);
		}
		String newick = utree.asNewick();
		UTree.newickToFile(outfile, newick);
	}

	public static void main(String args[])
	{
		String infile, outfile, outgroups;
		infile = System.getProperty("infile");
		outfile = System.getProperty("outfile");
		outgroups = System.getProperty("outgroups");
		if (infile == null || outfile == null || outgroups == null)
		{
			System.exit(-1);
		}
		String outgroupArray[] = outgroups.split(",");

		for (int i = 0; i < outgroupArray.length; i++)
		{
			outgroupArray[i] = outgroupArray[i].trim();
		}
		try
		{
			UTree.reroot(new File(infile), new File(outfile), outgroupArray);
		}
		catch(Throwable t)
		{
			//t.printStackTrace();
			System.err.println(t.getMessage());
			System.exit(-1);
		}
	}


}