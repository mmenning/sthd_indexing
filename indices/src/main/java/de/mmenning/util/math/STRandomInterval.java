package de.mmenning.util.math;

import de.mmenning.db.index.NowGen;
import de.mmenning.db.index.STFunctions;

public class STRandomInterval extends STConstantRandomInterval {

	private static final long serialVersionUID = 6557901271542195081L;

	private final NowGen now;

	public STRandomInterval(final RandomGenerator middle,
			final RandomGenerator expand, final double currentLikelihood,
			final RandomGenerator likelihoodGen, NowGen now) {
		super(middle, expand, currentLikelihood, likelihoodGen,
				STFunctions.CURRENT);
		this.now = now;
	}

	public STRandomInterval(final RandomGenerator middle,
			final RandomGenerator expand, final double currentLikelihood,
			NowGen now) {
		super(middle, expand, currentLikelihood, STFunctions.CURRENT);
		this.now = now;
	}

	@Override
	public double[] getNext() {

		double middle;
		double expand;

		if (this.getLikelihoodGen().getNext() < this.getConstantLikelihood()) {

			double[] interval = new double[2];

			interval[1] = this.getConstant();

			middle = this.getMiddle().getNext();
			expand = this.getExpand().getNext();

			while ((interval[0] = middle - expand) > now.getNow()) {
				middle = this.getMiddle().getNext();
				expand = this.getExpand().getNext();
			}
			return interval;

		} else {
			middle = this.getMiddle().getNext();
			expand = this.getExpand().getNext();

			double end = middle + expand;
			double begin = middle - expand;

			if (end < begin) {
				return new double[] { end, begin };
			} else {
				return new double[] { begin, end };
			}
		}

	}
}
