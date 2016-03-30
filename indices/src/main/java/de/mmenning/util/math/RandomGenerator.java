package de.mmenning.util.math;

import java.io.Serializable;
import java.util.Random;


public abstract class RandomGenerator implements Serializable{

	private static final long serialVersionUID = 6097400614893211667L;
	
	private final Random random;
	private final long seed;

	public RandomGenerator() {
		this(new Random().nextLong());
	}

	public RandomGenerator(final long seed) {
		this.random = new Random(seed);
		this.seed = seed;	
	}
	
	public abstract double getNext();

	public Random getRandom() {
		return this.random;
	}

	public long getSeed() {
		return this.seed;
	}

}
