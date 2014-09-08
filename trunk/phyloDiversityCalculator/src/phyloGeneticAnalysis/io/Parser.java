package phyloGeneticAnalysis.io;

import java.util.HashSet;
import java.util.Set;

public class Parser {
	public boolean isBlank(String character) {
		Set seperator = new HashSet();
		seperator.add("\t");
		seperator.add(" ");
		seperator.add("\r");
		seperator.add("\r\n");
		if (seperator.contains(character))
			return true;
		else
			return false;

	}

	public int skipBlanks(int idx, String line) {

		while (idx < line.length() && isBlank(line.substring(idx, idx + 1)))
			idx++;

		return idx;
	}

	public int getNextWordEndidx(int idx, String line) {
		String word = "";

		int index = skipBlanks(idx, line);

		while (idx < line.length() && !isBlank(line.substring(idx, idx + 1))) {
			word += line.substring(idx, idx + 1);
			idx++;
		}

		return idx;
	}

}
