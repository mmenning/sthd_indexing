package de.mmenning.util.math;

public class UniformDiscreteOpen extends UniformOpen {

	public UniformDiscreteOpen(Uniform uni, double infinityProbablity) {
		super(uni, infinityProbablity);
	}

	public int getNextDiscrete() {
		return (int) super.getNext();
	}

	@Override
	public double getNext() {
		return (int) super.getNext();
	}
}
