package de.mmenning.util.math;

public class STConstantRandomInterval extends RandomInterval {

	private static final long serialVersionUID = 2638746265025423131L;
	private final double constant;
	private final double constantLikelihood;
	private final RandomGenerator likelihoodGen;

	public double getConstant() {
		return constant;
	}

	public double getConstantLikelihood() {
		return constantLikelihood;
	}

	public RandomGenerator getLikelihoodGen() {
		return likelihoodGen;
	}

	public STConstantRandomInterval(final RandomGenerator middle,
			final RandomGenerator expand, final double constantLikelihood,
			final RandomGenerator likelihoodGen, final double constant) {
		super(middle, expand);
		this.constantLikelihood = constantLikelihood;
		this.constant = constant;
		this.likelihoodGen = likelihoodGen;
	}

	public STConstantRandomInterval(final RandomGenerator middle,
			final RandomGenerator expand, final double constantLikelihood,
			final double constant) {
		this(middle, expand, constantLikelihood, new Uniform(0, 1), constant);
	}

	@Override
	public double[] getNext() {
		double[] interval = super.getNext();

		if (likelihoodGen.getNext() < constantLikelihood) {
			interval[1] = this.constant;
		}

		return interval;
	}
}
