package de.mmenning.db.index.generate;

import de.mmenning.util.math.RandomInterval;
import de.mmenning.util.math.STConstantRandomInterval;
import de.mmenning.util.math.STRandomInterval;
import de.mmenning.util.math.Uniform;

public class RectangleDistributions {

	public static final NDRandomRectangleGenerator UNIFORM_5D;
	public static final NDRandomRectangleGenerator STC_UNIFORM_5D;

	static {
		RandomInterval[] r;

		r = new RandomInterval[5];
		for (int i = 0; i < r.length; i++) {
			r[i] = new RandomInterval(new Uniform(0.005, 0.990), new Uniform(0, 0.005));
		}
		UNIFORM_5D = new NDRandomRectangleGenerator(r);
		UNIFORM_5D
				.setDescription("Uniform Distribution from 0 to 1000, with uniformly distributed length from 0 to 10");

		r = new RandomInterval[5];
		for (int i = 0; i < 3; i++) {
			r[i] = new RandomInterval(new Uniform(0.005, 0.990), new Uniform(0, 0.005));
		}
		
		r = new RandomInterval[5];
		for (int i = 0; i < 3; i++) {
			r[i] = new RandomInterval(new Uniform(0.005, 0.990), new Uniform(0, 0.005));
		}
		r[3] = new STConstantRandomInterval(new Uniform(0.005, 0.990), new Uniform(0,
				0.005), 0.1, 10000);
		r[4] = new STConstantRandomInterval(new Uniform(0.005, 0.990), new Uniform(0,
				0.5), 0.1, 10000);
		STC_UNIFORM_5D = new NDRandomRectangleGenerator(r);
		STC_UNIFORM_5D
				.setDescription("Uniform Distribution from 0 to 1000, with uniformly distributed length from 0 to 10. Open Interval likelihood is 0.1, instead of Infinity, 10000 is used.");

	}

}
