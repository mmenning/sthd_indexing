package de.mmenning.db.index.rsttree;

import de.mmenning.db.index.NowGen;

import java.io.Serializable;
import java.util.Observable;
import java.util.Observer;

public class TimeHorizonCalc implements Observer, Serializable {

	private final RSTTree tree;
	private final OperationTracker it;
	private final NowGen now;

	private final int refreshRate;

	private int inserts;

	private double deltaT;

	private final double alphaW;

	public TimeHorizonCalc(RSTTree toObserve, OperationTracker it, NowGen now) {
		this(toObserve, it, now, 60, 0.5);
	}

	public TimeHorizonCalc(RSTTree toObserve, OperationTracker it, NowGen now,
			int refreshRate, double alphaW) {
		it.addObserver(this);

		this.tree = toObserve;
		this.it = it;
		this.now = now;

		this.deltaT = 0;
		this.startTime = now.getNow();

		this.inserts = 0;
		this.refreshRate = refreshRate;

		this.alphaW = alphaW;
	}

	private double startTime;

	private double ui;

	@Override
	public void update(final Observable o, Object arg) {

		if (this.inserts < refreshRate) {
			this.inserts++;
		} else {
			this.inserts = 0;
			this.deltaT = this.now.getNow() - this.startTime;
			this.startTime = this.now.getNow();
			this.ui = deltaT / this.tree.getMaxCapacity() * this.tree.size();
		}
	}

	public double getTimeHorizon() {
		return this.ui + this.alphaW * this.ui;
	}
}
