package de.mmenning.db.index.pyramid;

import java.util.Arrays;

import de.mmenning.db.index.NDPoint;

public class PyramidFunctions {

	public final static int HHIGH = 1;
	public final static int HLOW = 0;

	public static boolean checkPoint(double[] point) {
		for (double d : point) {
			if (Double.isNaN(d)) {
				return false;
			}
			if (d < 0 || d > 1) {
				return false;
			}
		}
		return true;
	}

	public static double[] convertToQCaret(double[] q) {
		double[] qCaret = new double[q.length];
		for (int i = 0; i < q.length; i++) {
			qCaret[i] = q[i] - 0.5;
		}
		return qCaret;
	}

	public static double getHeight(double[] point, int pyramid) {
		return Math.abs(0.5 - point[pyramid % point.length]);
	}

	public static double[] getHQueryInterval(int pyramid, final double[] qmin,
			double[] qmax) {

		final int d = qmin.length;

		double hlow;
		double hhigh;

		boolean intersectsall = true;
		for (int j = 0; j < qmin.length && intersectsall; j++) {
			intersectsall = qmin[j] <= 0.5 && 0.5 <= qmax[j];
		}

		if (pyramid < d) {

			if (intersectsall) {

				hlow = 0.0;
				hhigh = 0.5 - qmin[pyramid];

			} else {

				hhigh = 0.5 - qmin[pyramid];

				double min = Double.MAX_VALUE;
				double max = Double.MIN_VALUE;
				for (int j = 0; j < qmin.length; j++) {
					min = Math.min(min, qmax[j]);
					max = Math.max(max, qmin[j]);
				}

				if (min <= 0.5) {
					hlow = 0.5 - min;
				} else {
					hlow = max - 0.5;

				}
			}

		} else {

			pyramid = pyramid % d;

			if (intersectsall) {

				hlow = 0;
				hhigh = qmax[pyramid] - 0.5;

			} else {

				double max = Double.MIN_VALUE;
				double min = Double.MAX_VALUE;

				for (int j = 0; j < qmin.length; j++) {
					max = Math.max(max, qmin[j]);
					min = Math.min(min, qmax[j]);
				}
				if (max >= 0.5) {
					hlow = max - 0.5;
				} else {
					hlow = 0.5 - min;
				}

				hhigh = qmax[pyramid] - 0.5;
			}
		}

		return new double[] { hlow, hhigh };
	}

	static int getJmax(double[] point) {
		boolean allInfinite = true;

		for (int j = 0; j < point.length; j++) {

			if (!Double.isNaN(point[j])) {
				boolean allK = true;

				allInfinite = false;

				for (int k = 0; k < point.length && allK; k++) {

					if (!Double.isNaN((point[k])) && j != k) {

						if (Math.abs(0.5 - point[j]) < Math.abs(0.5 - point[k])) {
							allK = false;
						}
					}
				}
				if (allK) {
					return j;
				}
			}
		}
		if (allInfinite) {
			return 0;
		}
		return -1;
	}

	public static int getPyramid(double[] point) {
		int jmax = getJmax(point);
		if (jmax == -1) {
			throw new IllegalStateException("No pyramid found");
		}

		if (point[jmax] < 0.5) {
			return jmax;
		} else {
			return jmax + point.length;
		}
	}

	public static double getPyramidValue(double[] point) {
		if (!checkPoint(point)) {
			throw new IllegalArgumentException(
					"all entries must lay between 0 and 1: "
							+ Arrays.toString(point));
		}
		int pyramid = getPyramid(point);
		double height = getHeight(point, pyramid);
		return pyramid + height;
	}

	static boolean intersectsHigherPyramid(final int pyramid,
			double[] qmincaret, double[] qmaxcaret) {
		for (int j = 0; j < qmaxcaret.length; j++) {
			if (j != pyramid - qmaxcaret.length
					&& qmaxcaret[pyramid - qmaxcaret.length] < min(
							qmincaret[j], qmaxcaret[j])) {
				return false;
			}
		}
		return true;
	}

	static boolean intersectsLowerPyramid(final int pyramid,
			double[] qmincaret, double[] qmaxcaret) {
		for (int j = 0; j < qmincaret.length; j++) {
			if (j != pyramid
					&& qmincaret[pyramid] > -min(qmincaret[j], qmaxcaret[j])) {
				return false;
			}
		}
		return true;
	}

	public static boolean intersectsPyramid(final int pyramid,
			double[] qmincaret, double[] qmaxcaret) {
		if (pyramid >= qmincaret.length) {
			return intersectsHigherPyramid(pyramid, qmincaret, qmaxcaret);
		} else {
			return intersectsLowerPyramid(pyramid, qmincaret, qmaxcaret);
		}
	}

	static double max(double r_min, double r_max) {
		return Math.max(Math.abs(r_min), Math.abs(r_max));
	}

	static double min(double r_min, double r_max) {
		if (r_min <= 0 && 0 <= r_max) {
			return 0;
		} else {
			return Math.min(Math.abs(r_min), Math.abs(r_max));
		}
	}

	public static PyramidValue convertToPyramidValue(NDPoint point) {
		return convertToPyramidValue(point.toArray());
	}

	public static PyramidValue convertToPyramidValue(double[] point) {
		if (!checkPoint(point)) {
			throw new IllegalArgumentException(
					"all entries must lay between 0 and 1: "
							+ Arrays.toString(point));
		}
		int pyramid = PyramidFunctions.getPyramid(point);
		double height = PyramidFunctions.getHeight(point, pyramid);
		return new PyramidValue(pyramid, height);
	}

}
