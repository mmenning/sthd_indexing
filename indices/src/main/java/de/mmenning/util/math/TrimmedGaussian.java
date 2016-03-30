package de.mmenning.util.math;

import java.util.Random;

public class TrimmedGaussian extends Gaussian {

	private static final long serialVersionUID = -3948784137821675480L;

	private final double left;
	private final double right;

	public TrimmedGaussian() {
		this(1.0, 0.0, -1.0, 1.0);
	}

	public TrimmedGaussian(final double stdDev, final double mean,
			final double left, final double right) {
		super(stdDev, mean, new Random().nextLong());
		this.left = left;
		this.right = right;
	}

	public TrimmedGaussian(final double stdDev, final double mean,
			final double left, final double right, final long seed) {
		super(stdDev, mean, seed);
		this.left = left;
		this.right = right;
	}

	public double getLeft() {
		return this.left;
	}

	@Override
	public double getNext() {
		double next = super.getNext();
		while (next < this.left || next > this.right) {
			next = super.getNext();
		}
		return next;
	}

	public double getRight() {
		return this.right;
	}

}