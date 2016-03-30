package de.mmenning.db.index.rsttree;

public class BasicConstants implements STConstants {

	private static STConstants instance = new BasicConstants();

	public static STConstants getInstance() {
		return instance;
	}

	@Override
	public double getNowValue() {
		return 1000.0;
	}

	@Override
	public double getPValue() {
		return 10000.0;
	}

	@Override
	public double getAlphaValue() {
		return 0.75;
	}

}
