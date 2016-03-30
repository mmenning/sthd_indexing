package de.mmenning.db.index.rtree;

import static org.junit.Assert.assertTrue;

import org.junit.Before;

import de.mmenning.db.index.NDRectangleKey;

public class NDRStarTestCase extends NDRTreeTestCase {

	private NDRStar tree;

	@Override
	public NDRTree getTree() {
		return this.tree;
	}

	@Override
	@Before
	public void setUp() throws Exception {
		this.tree = new NDRStar(maxK, 0.5, dim);

		for (int i = 0; i < initialSize; i++) {

			final NDRectangleKey box = this.nextNDRectangleKey();
			assertTrue(this.tree.insert(box));

			assertTrue(this.added.add(box));

		}
	}
}