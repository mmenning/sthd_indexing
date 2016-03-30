package de.mmenning.util.math;

import java.util.Random;

public class Uniform extends RandomGenerator {

	private static final long serialVersionUID = 2680199927482569390L;

	private final double begin;

	private final double length;

	public Uniform() {
		this(-1.0, 2.0);
	}

	public Uniform(final double begin, final double length) {
		this(begin, length, new Random().nextLong());
	}

	public Uniform(final double begin, final double length, final long seed) {
		super(seed);
		if (length < 0.0) {
			throw new IllegalArgumentException("length (" + length
					+ ") is lesser than zero");
		}
		this.begin = begin;
		this.length = length;
	}

	public double getBegin() {
		return this.begin;
	}

	public double getLength() {
		return this.length;
	}

	@Override
	public double getNext() {
		return this.getRandom().nextDouble() * this.length + this.begin;
	}

}
