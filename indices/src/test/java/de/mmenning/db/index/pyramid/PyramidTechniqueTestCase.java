package de.mmenning.db.index.pyramid;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

import de.mmenning.db.index.pyramid.PyramidTechnique;
import de.mmenning.db.index.pyramid.PyramidValue;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import de.mmenning.db.index.NDPoint;
import de.mmenning.db.index.NDPointKey;
import de.mmenning.db.index.NDRectangle;
import de.mmenning.db.index.PointQuery;
import de.mmenning.db.index.generate.NDPointGenerator;
import de.mmenning.db.index.generate.NDRandomRectangleGenerator;
import de.mmenning.db.index.generate.RectangleDistributions;
import de.mmenning.db.index.generate.StatsObject;
import de.mmenning.db.storage.DefaultStorage;
import de.mmenning.util.math.Uniform;

public class PyramidTechniqueTestCase {

	protected PyramidTechnique tree;

	protected NDPointGenerator pointGen;

	protected final Set<NDPointKey> added = new HashSet<NDPointKey>();

	protected int dim;
	protected int initialSize;

	final int REGION_TESTS = 1000;

	@Before
	public void setUp() throws Exception {
		this.dim = 5;
		this.initialSize = 10000;
		this.tree = new PyramidTechnique(this.dim, 4096);
		this.pointGen = new NDPointGenerator(this.dim, new Uniform(0, 1));

		NDPointKey insert;

		for (int i = 0; i < this.initialSize; i++) {
			insert = this.nextNDPointKey();
			assertTrue(this.added.add(insert));
			assertTrue(this.tree.insert(insert));
		}
	}

	@After
	public void tearDown() throws Exception {

		DefaultStorage.getInstance().cleanUp();
	}

	@Test
	public void testContains() {
		for (final NDPointKey k : this.added) {
			assertTrue(this.getTree().contains(k));
		}
		assertFalse(this.getTree().contains(this.nextNDPointKey()));
	}

	@Test
	public void testContainsDeleted() {
		NDPointKey deleted = this.added.iterator().next();
		assertTrue(this.getTree().delete(deleted));
		assertFalse(this.getTree().contains(deleted));
	}

	@Test
	public void testDelete() {
		assertFalse(this.getTree().delete(this.nextNDPointKey()));

		for (NDPointKey k : this.added) {
			assertTrue(this.getTree().delete(k));
		}
	}

	@Test
	public void testSize() {
		assertEquals(this.getTree().size(), added.size());
		assertTrue(this.getTree().insert(this.nextNDPointKey()));
		assertEquals(this.getTree().size(), added.size() + 1);

		assertTrue(this.getTree().delete(this.added.iterator().next()));
		assertEquals(this.getTree().size(), added.size());
	}

	protected PyramidTechnique getTree() {
		return this.tree;
	}

	protected NDPointKey nextNDPointKey() {
		return new NDPointKey(new StatsObject().getObjectReference(),
				this.pointGen.getNext());
	}

	@Test
	public void testMultipleRegions() {

		final NDRandomRectangleGenerator queries = RectangleDistributions.UNIFORM_5D;

		for (int i = 0; i < REGION_TESTS; i++) {

			final HashSet<NDPointKey> s = new HashSet<>();

			NDRectangle testRegion = queries.getNextRectangle();

			int contained = 0;

			for (NDPointKey k : added) {
				if (testRegion.contains(k.getNDKey())) {
					contained++;
				}
			}

			PointQuery q = new PointQuery() {
				@Override
				public boolean query(NDPointKey v) {
					assertTrue(s.add(v));
					return true;
				}
			};

			this.getTree().regionQuery(testRegion, q);

			assertEquals(contained, s.size());

		}
	}

	@Test
	public void testRegion() {
		final HashSet<NDPointKey> s = new HashSet<>();

		double[] d1 = new double[this.dim];
		double[] d2 = new double[this.dim];

		for (int i = 0; i < d1.length; i++) {
			d1[i] = 0.5;
			d2[i] = 1.0;
		}

		final NDRectangle testRegion = new NDRectangle(new NDPoint(d1),
				new NDPoint(d2));
		int contained = 0;

		for (NDPointKey k : added) {
			if (testRegion.contains(k.getNDKey())) {
				contained++;
			}
		}

		PointQuery q = new PointQuery() {
			@Override
			public boolean query(NDPointKey v) {
				assertTrue(s.add(v));
				return true;
			}
		};

		this.getTree().regionQuery(testRegion, q);

		assertEquals(contained, s.size());
	}

	@Test
	public void testConversion() {

		NDPointKey k = this.nextNDPointKey();

		PyramidValue v = this.getTree().convert(k.getNDKey());

		PyramidValue v2 = this.getTree().convert(k.getNDKey());

		assertEquals(v, v2);
	}

	@Test
	public void testUpdate() {

		NDPointKey newOne = this.nextNDPointKey();

		Iterator<NDPointKey> iter = added.iterator();

		NDPointKey oldOne = iter.next();

		assertTrue(this.getTree().update(oldOne, newOne));

		assertTrue(this.getTree().contains(newOne));

		assertFalse(this.getTree().contains(oldOne));

		assertFalse(this.getTree().update(oldOne, added.iterator().next()));

		while (iter.hasNext()) {
			assertTrue(this.getTree()
					.update(iter.next(), this.nextNDPointKey()));
		}

	}
}
