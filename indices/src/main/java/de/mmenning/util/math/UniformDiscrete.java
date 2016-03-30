package de.mmenning.util.math;

public class UniformDiscrete extends Uniform {

	public UniformDiscrete(int begin, int length) {
		super(begin, length);
	}

	public int getNextDiscrete() {
		return (int) this.getNext();
	}

	@Override
	public double getNext() {
		return (int) (super.getNext() + 0.5);
	}

}
