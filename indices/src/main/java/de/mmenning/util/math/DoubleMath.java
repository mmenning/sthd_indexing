package de.mmenning.util.math;

public class DoubleMath {

	public static double min(double[] r) {
		double min = r[0];

		for (int i = 1; i < r.length; i++) {
			if (r[i] < min) {
				min = r[i];
			}
		}
		return min;
	}

	public static double max(double[] r) {
		double max = r[0];

		for (int i = 1; i < r.length; i++) {
			if (r[i] > max) {
				max = r[i];
			}
		}
		return max;
	}

	private static final double LOG2 = Math.log(2);

	public static double log2(double x) {
		return Math.log(x) / LOG2;

	}

}
