package de.mmenning.db.index.rtree;

import java.util.HashSet;
import java.util.Set;

import de.mmenning.db.index.NDRectangleKey;
import de.mmenning.db.index.generate.NDRandomRectangleGenerator;
import de.mmenning.db.index.generate.RectangleDistributions;
import de.mmenning.db.index.generate.StatsObject;
import de.mmenning.db.index.rtree.NDRStar;

public class NDRStarTest {

	private static NDRStar tree;
	private static Set<NDRectangleKey> added = new HashSet<NDRectangleKey>();

	private static final int TESTS = 1;

	private static final int dim = 5;
	private static final int maxK = 8;
	private static final int initialSize = 25;

	public static void main(final String[] args) {
		long totalTime = 0;

		for (int j = 0; j < TESTS; j++) {

			tree = new NDRStar(maxK, maxK / 2, dim);

		final NDRandomRectangleGenerator recGen = RectangleDistributions.UNIFORM_5D;

			final long start = System.nanoTime();

			for (int i = 0; i < initialSize; i++) {
				final NDRectangleKey box = new NDRectangleKey(new StatsObject().getObjectReference(),
						recGen.getNextRectangle());

				tree.insert(box);

			}

			totalTime += (System.nanoTime() - start);

		}
		System.out.println("Average inserting " + initialSize + " "
				+ (((double) totalTime / (double) TESTS)/1000000.0));
	}

}
