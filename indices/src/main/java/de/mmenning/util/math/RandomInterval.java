package de.mmenning.util.math;

import java.io.Serializable;

public class RandomInterval implements Serializable {

	private static final long serialVersionUID = -4971801332940304365L;
	private final RandomGenerator middle;
	private final RandomGenerator expand;

	public RandomInterval(final RandomGenerator middle,
			final RandomGenerator expand) {
		this.middle = middle;
		this.expand = expand;
	}

	public RandomGenerator getMiddle() {
		return this.middle;
	}

	public RandomGenerator getExpand() {
		return this.expand;
	}

	public double[] getNext() {

		double middle = this.middle.getNext();
		double expand = this.expand.getNext();

		double end = middle + expand;
		double begin = middle - expand;

		if (end < begin) {
			return new double[] { end, begin };
		} else {
			return new double[] { begin, end };
		}

	}

}
