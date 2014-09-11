/*
 * @author JingLiu
 * @version 1.0
 * @date Jan 8, 2013
 */
package phyloGeneticAnalysis.Util;

import java.util.ArrayList;
import java.util.List;

import phyloGeneticAnalysis.Exception.nameFormatException;
/**
 * The Class speciesNameFormatUtils.
 */
public class speciesNameFormatUtils {
	public static void nameListFormat(List<String> subtreeNameList, List<String> subComNl,List<String> subSciNl) throws nameFormatException{
		int i,count;

		i = 0;
		count = subtreeNameList.size();
		while (count > 0 && i < count) {
			String temp = (String) subtreeNameList.get(i);
			int loc = temp.indexOf("-");
			int locblk = temp.substring(0, loc - 1).indexOf(" ");
			if (locblk > 0)
				subComNl.add(temp.substring(0, locblk).toLowerCase() + "_"
						+ temp.substring(locblk + 1, loc - 1).toLowerCase());
			else
				subComNl.add(temp.substring(0, loc - 1).toLowerCase());
			locblk = 0;
			locblk = temp.substring(loc + 2).indexOf(" ");
			if (locblk > 0)
				subSciNl.add(temp.substring(loc + 2, loc + 2 + locblk).toLowerCase() + "_"
						+ temp.substring(loc + 2 + locblk + 1).toLowerCase());
			else
				subSciNl.add(temp.substring(loc + 2).toLowerCase());
			loc = 0;
			locblk = 0;
			i++;
		}

	}
	public static ArrayList nameListFormat(List<String> nameList) throws nameFormatException{
		ArrayList retList = new ArrayList();
		for (int i =0;i< nameList.size();i++){
			String name = (String) nameList.get(i);
			name = name.replaceAll(" ", "_");
			retList.add(name.toLowerCase());
		}
		return retList;
	}
}
