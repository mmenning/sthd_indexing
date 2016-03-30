package de.mmenning.util.math;

import java.util.Random;

public class Gaussian extends RandomGenerator {

	private static final long serialVersionUID = 4891758946319738363L;
	
	private final double variance;
	private final double mean;

	public Gaussian() {
		this(1.0, 0.0);
	}

	public Gaussian(final double stdDev, final double mean) {
		this(stdDev, mean, new Random().nextLong());
	}

	public Gaussian(final double stdDev, final double mean, final long seed) {
		super(seed);
		this.variance = stdDev * stdDev;
		this.mean = mean;
	}

	public double getMean() {
		return this.mean;
	}

	@Override
	public double getNext() {
		return this.mean + this.getRandom().nextGaussian() * this.variance;
	}

	public double getVariance() {
		return this.variance;
	}
}
