package de.mmenning.db.index.generate;

import java.util.Arrays;

import de.mmenning.db.index.NDPoint;
import de.mmenning.util.math.RandomGenerator;

public class NDPointGenerator {

	private final RandomGenerator[] random;

	public NDPointGenerator(final int dim, final RandomGenerator random) {
		this.random = new RandomGenerator[dim];
		Arrays.fill(this.random, random);
	}

	public NDPointGenerator(final RandomGenerator[] random) {
		this.random = random;
	}

	public NDPoint getNext() {
		final double[] values = new double[this.random.length];
		for (int i = 0; i < this.random.length; i++) {
			values[i] = this.random[i].getNext();
		}
		return new NDPoint(values);
	}

	public RandomGenerator getRandomGenerator(int dim) {
		return this.random[dim];
	}
}
