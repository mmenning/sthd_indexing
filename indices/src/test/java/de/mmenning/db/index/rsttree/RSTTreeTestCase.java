package de.mmenning.db.index.rsttree;

import static org.junit.Assert.assertTrue;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import de.mmenning.db.index.NDRectangleKey;
import de.mmenning.db.index.rtree.NDRTree;
import de.mmenning.db.index.rtree.NDRTreeTestCase;
import de.mmenning.db.storage.DefaultStorage;

public class RSTTreeTestCase extends NDRTreeTestCase {

	private RSTTree tree;

	@Override
	public NDRTree getTree() {
		return this.tree;
	}

	@Override
	@Before
	public void setUp() throws Exception {

		this.tree = new RSTTree(maxK, 0.5, dim-2);

		for (int i = 0; i < initialSize; i++) {

			final NDRectangleKey box = this.nextNDRectangleKey();
			assertTrue(this.tree.insert(box));

			assertTrue(this.added.add(box));

		}
	}

	@Override
	@After
	public void tearDown() throws Exception {

		DefaultStorage.getInstance().cleanUp();
	}

	@Override
	@Test
	public void testLeafPathLength(){
	}

	@Override
	@Test
	public void testFindLeaf() {}


}