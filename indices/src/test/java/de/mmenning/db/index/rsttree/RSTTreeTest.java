package de.mmenning.db.index.rsttree;

import java.util.HashSet;
import java.util.Set;

import de.mmenning.db.index.NDRectangleKey;
import de.mmenning.db.index.generate.NDRectangleGenerator;
import de.mmenning.db.index.generate.RectangleDistributions;
import de.mmenning.db.index.generate.StatsObject;
import de.mmenning.db.index.rsttree.RSTTree;

public class RSTTreeTest {

	private static RSTTree tree;
	private static Set<NDRectangleKey> added = new HashSet<NDRectangleKey>();

	private static final int TESTS = 100;

	private static final int dim = 3;
	private static final int maxK = 8;
	private static final int initialSize = 30000;

	public static void main(final String[] args) {
		long totalTime = 0;

		for (int j = 0; j < TESTS; j++) {

			tree = new RSTTree(maxK, maxK/2, dim);

			final NDRectangleGenerator recGen = RectangleDistributions.STC_UNIFORM_5D;
			final long start = System.nanoTime();

			for (int i = 0; i < initialSize; i++) {
				final NDRectangleKey box = new NDRectangleKey(
						new StatsObject().getObjectReference(), recGen.getNextRectangle());

				if (!tree.insert(box)) {
					System.err.println("Cannot insert " + box);
				}
			}

			totalTime += (System.nanoTime() - start);

		}
		System.out.println("Average inserting " + initialSize + " "
				+ (totalTime / 1000000.0 / TESTS));

	}

}
