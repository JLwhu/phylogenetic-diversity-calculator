package phyloGeneticAnalysis.community.math;
/**
 * ���㲢��������
 * @author  
 * @date    2010-9-13
 * @file    org.demo.algorithm.Combination.java
 */
public class Combination {
	/**
	 * ���������
	 * ������������Ƶ��������£�
	 * ��Ϊֱ�Ӽ��� n! ���׷�������������ʿɸ�Ϊ���� ln(n)
	 * C(n,m) = n!/(m!(n-m)!) 
	 * C(n,m) = n(n-1)..(n-m+1)/m!
	 * ln(C(n,m)) = ln(n(n-1)..(n-m+1)/m!)
	 * ln(C(n,m)) = ln(n(n-1)..(n-m+1)) - ln(m!)
	 * ln(C(n,m)) = (ln(n) + ln(n-1) + .. + ln(n-m+1))
	 *             -(ln(m) + ln(m-1) + .. + ln(1))
	 * ���Ͽ�֪ C(n,m) = e^ln(C(n,m)),
	 * �����ɹ�ʽ�Ҳ��֪��m ԽС������ԽС
	 * �� C(n,m) = C(n,n-m)
	 * �� �� m>n/2.0ʱ,�ɸ�Ϊ���� C(n,n-m)
	 * @param n
	 * @param m
	 * @return
	 */
	public static long getCnm(int n,int m){
		if (n < 0 || m < 0){
			throw new IllegalArgumentException("n,m must be > 0");
		}
		if (n == 0 || m == 0){
			return 1;
		}
		if (m > n){
			return 0;
		}
		if (m > n/2.0){
			m = n-m;
		}
		double result = 0.0;
		for (int i=n; i>=(n-m+1); i--){
			result += Math.log(i);
		}
		for (int i=m; i>=1; i--){
			result -= Math.log(i);
		}
		result = Math.exp(result);
		return Math.round(result);
	}
	
	public static double getCnmLog(int n,int m){
		if (n < 0 || m < 0){
			throw new IllegalArgumentException("n,m must be > 0");
		}
		if (n == 0 || m == 0){
			return 0.0;
		}
		if (m > n){
			throw new IllegalArgumentException("m must be > n");
		}
		if (m > n/2.0){
			m = n-m;
		}
		double result = 0.0;
		for (int i=n; i>=(n-m+1); i--){
			result += Math.log(i);
		}
		for (int i=m; i>=1; i--){
			result -= Math.log(i);
		}
		return result;
	}
}
