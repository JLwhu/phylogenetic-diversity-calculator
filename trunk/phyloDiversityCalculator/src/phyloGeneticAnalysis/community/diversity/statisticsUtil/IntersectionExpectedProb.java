package phyloGeneticAnalysis.community.diversity.statisticsUtil;

import phyloGeneticAnalysis.community.math.Combination;
import weka.core.Instance;

public class IntersectionExpectedProb {
	public double expectedProb(Instance A, Instance B, double[] funcValues) {
		int total = A.numAttributes();
		int unionSize = 0;
		int smallSetSize = 0;
		int largeSetSize = 0;
		for (int i = 0; i < total; i++) {
			if (A.value(i) != 0 || B.value(i) != 0)
				unionSize++;
			if (A.value(i) != 0)
				smallSetSize++;
			if (B.value(i) != 0)
				largeSetSize++;
		}

		if (smallSetSize > largeSetSize) {
			int temp = smallSetSize;
			smallSetSize = largeSetSize;
			largeSetSize = temp;
		}

		double sum = 0;
		int startidx = largeSetSize + smallSetSize - unionSize;
		if (startidx <= 0)
			startidx = 1;
		for (int i = startidx; i <= smallSetSize; i++) {
			double prob = probability(largeSetSize,smallSetSize, unionSize, i);
			sum = sum + funcValues[i] * prob;
			if (prob>1) {
				System.out.println(prob);
				System.out.println("largeSetSize="+largeSetSize+"smallSetSize="+smallSetSize+"unionSize="+unionSize+"i="+i);
			}
		//	System.out.println(funcValues[i]);
		//	System.out.println(sum);
		}
		return sum;

	}
	
	public double probability(int a, int b, int unionSize, int intersect) {
		
		double result = Combination.getCnmLog(a, intersect)
				+ Combination.getCnmLog(unionSize - a, b - intersect);
		result = result - Combination.getCnmLog(unionSize, b);
		result = Math.exp(result);
		return result;
	}

}
