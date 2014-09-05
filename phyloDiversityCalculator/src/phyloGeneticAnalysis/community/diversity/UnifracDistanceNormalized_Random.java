package phyloGeneticAnalysis.community.diversity;

import pal.tree.Tree;

	public class UnifracDistanceNormalized_Random extends UnifracDistance {

		public UnifracDistanceNormalized_Random(Tree phylotree) {
			super(phylotree);
			super.expectedValueAdjust = true;
			super.maxAdjustValue = false;
		}

	}