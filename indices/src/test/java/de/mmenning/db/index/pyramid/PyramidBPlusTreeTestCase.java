package de.mmenning.db.index.pyramid;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.util.HashSet;
import java.util.LinkedList;
import java.util.Set;

import de.mmenning.db.index.pyramid.PyramidBPlusTree;
import de.mmenning.db.index.pyramid.PyramidFunctions;
import de.mmenning.db.index.pyramid.PyramidValue;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import de.mmenning.db.index.NDPointKey;
import de.mmenning.db.index.bplustree.KeyValuePair;
import de.mmenning.db.index.generate.NDPointGenerator;
import de.mmenning.db.index.generate.StatsObject;
import de.mmenning.db.storage.DefaultStorage;
import de.mmenning.db.storage.ObjectReference;
import de.mmenning.db.storage.Storable;
import de.mmenning.db.storage.StorageManager;
import de.mmenning.util.math.Uniform;
import de.mmenning.util.math.UniformOpen;

public class PyramidBPlusTreeTestCase {

	private PyramidBPlusTree<NDPointKey> tree;

	private final int initialSize = 10000;

	private NDPointGenerator pointGen;

	public PyramidBPlusTree<NDPointKey> getTree() {
		return this.tree;
	}

	private Set<NDPointKey> added;

	protected NDPointKey nextNDPointKey() {
		return new NDPointKey(new StatsObject().getObjectReference(),
				this.pointGen.getNext());
	}

	private final int dim = 5;

	@Before
	public void setUp() {

		final int blockSize = 4096;

		StorageManager s = new StorageManager() {

			@Override
			public Storable load(ObjectReference or) {
				return DefaultStorage.getInstance().load(or);
			}

			@Override
			public void delete(ObjectReference or) {
				DefaultStorage.getInstance().delete(or);
			}

			@Override
			public void store(Storable st) {
				assertTrue(st.getBytes() < blockSize);
				DefaultStorage.getInstance().store(st);
			}

			@Override
			public void cleanUp() {

			}
		};

		this.tree = new PyramidBPlusTree<NDPointKey>(blockSize, this.dim,
				DefaultStorage.getInstance(), dim * 8);

		this.pointGen = new NDPointGenerator(this.dim, new Uniform(0, 1));

		added = new HashSet<NDPointKey>();

		for (int i = 0; i < this.initialSize; i++) {
			NDPointKey k = this.nextNDPointKey();
			tree.insert(PyramidFunctions.convertToPyramidValue(k.getNDKey()), k);
			added.add(k);
		}
	}

	@After
	public void tearDown() throws Exception {

		DefaultStorage.getInstance().cleanUp();
	}

	public void testLeafNodeByteSize() {

	}

	@Test
	public void testInsert() {
		NDPointKey k = this.nextNDPointKey();

		assertTrue(this.getTree().insert(
				PyramidFunctions.convertToPyramidValue(k.getNDKey()), k));
		assertFalse(this.getTree().insert(
				PyramidFunctions.convertToPyramidValue(k.getNDKey()), k));

		assertTrue(this.getTree().insert(
				PyramidFunctions.convertToPyramidValue(k.getNDKey()),
				this.nextNDPointKey()));
	}

	@Test
	public void testContains() {
		NDPointKey k = added.iterator().next();

		assertTrue(this.getTree().contains(
				PyramidFunctions.convertToPyramidValue(k.getNDKey())));

		k = this.nextNDPointKey();
		assertFalse(this.getTree().contains(
				PyramidFunctions.convertToPyramidValue(k.getNDKey()), k));
	}

	@Test
	public void testRemove() {

		NDPointKey k = this.nextNDPointKey();
		assertFalse(this.getTree().remove(
				PyramidFunctions.convertToPyramidValue(k.getNDKey()), k));

		k = added.iterator().next();
		assertFalse(this.getTree().insert(
				PyramidFunctions.convertToPyramidValue(k.getNDKey()), k));

		for (NDPointKey key : added) {
			assertTrue(this.getTree()
					.remove(PyramidFunctions.convertToPyramidValue(key
							.getNDKey()), key));
		}

		assertTrue(this.getTree().insert(
				PyramidFunctions.convertToPyramidValue(k.getNDKey()), k));
	}

	@Test
	public void testInsertEqual() {
		NDPointKey k1 = this.nextNDPointKey();
		NDPointKey k2 = this.nextNDPointKey();

		assertTrue(this.getTree().insert(
				PyramidFunctions.convertToPyramidValue(k1.getNDKey()), k1));
		assertTrue(this.getTree().insert(
				PyramidFunctions.convertToPyramidValue(k1.getNDKey()), k2));
		assertEquals(this.initialSize + 2, this.getTree().size());
	}

	@Test
	public void testIterator() {

		int i = 0;

		for (KeyValuePair<PyramidValue, LinkedList<NDPointKey>> k : this
				.getTree()) {
			i += k.getValue().size();
		}

		assertEquals(this.initialSize, i);

	}

	@Test
	public void testRemoveEqual() {
		NDPointKey k1 = this.nextNDPointKey();
		NDPointKey k2 = this.nextNDPointKey();

		assertTrue(this.getTree().insert(
				PyramidFunctions.convertToPyramidValue(k1.getNDKey()), k1));
		assertTrue(this.getTree().insert(
				PyramidFunctions.convertToPyramidValue(k1.getNDKey()), k2));
		assertTrue(this.getTree().remove(
				PyramidFunctions.convertToPyramidValue(k1.getNDKey()), k1));
		assertEquals(this.initialSize + 1, this.getTree().size());
		assertTrue(this.getTree().remove(
				PyramidFunctions.convertToPyramidValue(k1.getNDKey()), k2));
		assertEquals(this.initialSize, this.getTree().size());
	}
}
