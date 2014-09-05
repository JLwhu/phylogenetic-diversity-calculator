package phyloGeneticAnalysis.community.diversity;

import pal.tree.Tree;


	public class UnifracDistanceNormalized_Max extends UnifracDistance {

		public UnifracDistanceNormalized_Max(Tree phylotree) {
			super(phylotree);
			super.expectedValueAdjust = true;
			super.maxAdjustValue = true;
		}

	}
