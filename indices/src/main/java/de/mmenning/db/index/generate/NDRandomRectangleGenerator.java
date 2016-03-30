package de.mmenning.db.index.generate;

import java.io.Serializable;
import java.util.Arrays;

import de.mmenning.db.index.NDPoint;
import de.mmenning.db.index.NDRectangle;
import de.mmenning.util.math.RandomInterval;

public class NDRandomRectangleGenerator extends NDRectangleGenerator implements
		Serializable {

	private final RandomInterval[] intervals;

	private String description;

	public NDRandomRectangleGenerator(final RandomInterval... intervals) {
		this.intervals = Arrays.copyOf(intervals, intervals.length);
	}

	@Override
	public String getDescription() {
		return this.description;
	}

	@Override
	public int getDim() {
		return this.intervals.length;
	}

	@Override
	public synchronized NDRectangle getNextRectangle() {
		final double[] begin = new double[this.getDim()];
		final double[] end = new double[this.getDim()];

		for (int i = 0; i < this.getDim(); i++) {
			final double[] interval = this.intervals[i].getNext();
			begin[i] = interval[0];
			end[i] = interval[1];
		}

		return new NDRectangle(new NDPoint(begin), new NDPoint(end));
	}

	public RandomInterval getRandomInterval(final int dim) {
		return this.intervals[dim];
	}

	public RandomInterval[] getRandomIntervals() {
		return Arrays.copyOf(this.intervals, this.intervals.length);
	}

	@Override
	public void setDescription(final String description) {
		this.description = description;
	}

	@Override
	public void init() {
		/*
		 * Random Rectangles do not need to be initialized. Already done by
		 * constructing.
		 */

	}

}
