package de.mmenning.util.math;

import de.mmenning.db.index.STFunctions;

public class UniformOpen extends RandomGenerator {

	private static final long serialVersionUID = -4492742708190776968L;
	private double currentProbablity;
	private Uniform uni;

	public UniformOpen(Uniform uni, double currentProbablity) {
		this.uni = uni;
		this.currentProbablity = currentProbablity;
	}

	@Override
	public double getNext() {
		return Math.random() < this.currentProbablity ? STFunctions.CURRENT
				: uni.getNext();
	}

	public Uniform getUniform(){
		return this.uni;
	}
	
	public double getInfinityProbability(){
		return this.currentProbablity;
	}
	
}
