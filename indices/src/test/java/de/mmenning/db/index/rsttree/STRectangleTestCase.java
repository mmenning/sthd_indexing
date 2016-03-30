package de.mmenning.db.index.rsttree;

import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import de.mmenning.db.index.rsttree.STConstants;
import de.mmenning.db.index.rsttree.STRectangle;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import de.mmenning.db.index.NDPoint;
import de.mmenning.db.index.NDRectangle;

public class STRectangleTestCase {

	static STConstants basic = new STConstants() {

		@Override
		public double getNowValue() {
			return 1000.0;
		}

		@Override
		public double getPValue() {
			return 1000.0;
		}

		@Override
		public double getAlphaValue() {
			return 0.75;
		}

	};

	@Before
	public void setUp() throws Exception {
	}

	@After
	public void tearDown() throws Exception {
	}

	@Test
	public void testContainsClosed() {
		final double[] aBegin = new double[] { 0, 0, 0, 0 };
		final double[] aEnd = new double[] { 2, 2, 2, 2 };

		final double[] bBegin = new double[] { 0.5, 0.5, 0.5, 0.5 };
		final double[] bEnd = new double[] { 1.5, 1.5, 1.5, 1.5 };

		STRectangle a = new STRectangle(new NDRectangle(new NDPoint(aBegin),
				new NDPoint(aEnd)), 0.0, basic);
		STRectangle b = new STRectangle(new NDRectangle(new NDPoint(bBegin),
				new NDPoint(bEnd)), 0.0, basic);

		assertTrue(a.contains(b));
		assertFalse(b.contains(a));
	}

	@Test
	public void testOpenContainsClosed() {
		final double[] aBegin = new double[] { 0, 0, 0, 0 };
		final double[] aEnd = new double[] { 2, 2, STRectangle.UC,
				STRectangle.NOW };

		final double[] bBegin = new double[] { 0.5, 0.5, 0.5, 0.5 };
		final double[] bEnd = new double[] { 1.5, 1.5, 1.5, 1.5 };

		STRectangle a = new STRectangle(new NDRectangle(new NDPoint(aBegin),
				new NDPoint(aEnd)), 0.0, basic);
		STRectangle b = new STRectangle(new NDRectangle(new NDPoint(bBegin),
				new NDPoint(bEnd)), 0.0, basic);

		assertTrue(a.contains(b));
		assertFalse(b.contains(a));
	}

	@Test
	public void testOpenContainsOpen() {
		final double[] aBegin = new double[] { 0, 0, 0, 0 };
		final double[] aEnd = new double[] { 2, 2, STRectangle.UC,
				STRectangle.NOW };

		final double[] bBegin = new double[] { 0.5, 0.5, 0.5, 0.5 };
		final double[] bEnd = new double[] { 1.5, 1.5, STRectangle.UC,
				STRectangle.NOW };

		STRectangle a = new STRectangle(new NDRectangle(new NDPoint(aBegin),
				new NDPoint(aEnd)), 0.0, basic);
		STRectangle b = new STRectangle(new NDRectangle(new NDPoint(bBegin),
				new NDPoint(bEnd)), 0.0, basic);

		assertTrue(a.contains(b));
		assertFalse(b.contains(a));
	}

	@Test
	public void testClosedContainsOpen() {
		final double[] aBegin = new double[] { 0, 0, 0, 0 };
		final double[] aEnd = new double[] { 2, 2, 2, 2 };

		final double[] bBegin = new double[] { 0.5, 0.5, 0.5, 0.5 };
		final double[] bEnd = new double[] { 1.5, 1.5, STRectangle.UC,
				STRectangle.NOW };

		STRectangle a = new STRectangle(new NDRectangle(new NDPoint(aBegin),
				new NDPoint(aEnd)), 0.0, basic);
		STRectangle b = new STRectangle(new NDRectangle(new NDPoint(bBegin),
				new NDPoint(bEnd)), 0.0, basic);

		assertFalse(a.contains(b));
		assertFalse(b.contains(a));
	}

	@Test
	public void testNotContainsTT() {
		final double[] aBegin = new double[] { 0, 0, 0, 0 };
		final double[] aEnd = new double[] { 2, 2, 2, 2 };

		final double[] bBegin = new double[] { 0.5, 0.5, 0.5, 0.5 };
		final double[] bEnd = new double[] { 1.5, 1.5, STRectangle.UC,
				1.5 };

		STRectangle a = new STRectangle(new NDRectangle(new NDPoint(aBegin),
				new NDPoint(aEnd)), 0.0, basic);
		STRectangle b = new STRectangle(new NDRectangle(new NDPoint(bBegin),
				new NDPoint(bEnd)), 0.0, basic);

		assertFalse(a.contains(b));
		assertFalse(b.contains(a));
	}


	@Test
	public void testNotContainsVT() {
		final double[] aBegin = new double[] { 0, 0, 0, 0 };
		final double[] aEnd = new double[] { 2, 2, 2, 2 };

		final double[] bBegin = new double[] { 0.5, 0.5, 0.5, 0.5 };
		final double[] bEnd = new double[] { 1.5, 1.5, 1.5,
				3.5 };

		STRectangle a = new STRectangle(new NDRectangle(new NDPoint(aBegin),
				new NDPoint(aEnd)), 0.0, basic);
		STRectangle b = new STRectangle(new NDRectangle(new NDPoint(bBegin),
				new NDPoint(bEnd)), 0.0, basic);

		assertFalse(a.contains(b));
		assertFalse(b.contains(a));
	}


	@Test
	public void testNotContains() {
		final double[] aBegin = new double[] { 0, 0, 0, 0 };
		final double[] aEnd = new double[] { 2, 2, 2, 2 };

		final double[] bBegin = new double[] { 2.0, 2.0, 2.0, 2.0 };
		final double[] bEnd = new double[] { 4.0, 4.0, 4.0, 4.0 };

		STRectangle a = new STRectangle(new NDRectangle(new NDPoint(aBegin),
				new NDPoint(aEnd)), 0.0, basic);
		STRectangle b = new STRectangle(new NDRectangle(new NDPoint(bBegin),
				new NDPoint(bEnd)), 0.0, basic);

		assertFalse(a.contains(b));
		assertFalse(b.contains(a));
	}


	@Test
	public void testIntersectsClosed() {
		final double[] aBegin = new double[] { 0, 0, 0, 0 };
		final double[] aEnd = new double[] { 2, 2, 2, 2 };

		final double[] bBegin = new double[] { 0.5, 0.5, 0.5, 0.5 };
		final double[] bEnd = new double[] { 1.5, 1.5, 1.5, 1.5 };

		STRectangle a = new STRectangle(new NDRectangle(new NDPoint(aBegin),
				new NDPoint(aEnd)), 0.0, basic);
		STRectangle b = new STRectangle(new NDRectangle(new NDPoint(bBegin),
				new NDPoint(bEnd)), 0.0, basic);

		assertTrue(a.intersects(b));
		assertTrue(b.intersects(a));
	}

	@Test
	public void testIntersectsOpen() {
		final double[] aBegin = new double[] { 0, 0, 0, 0 };
		final double[] aEnd = new double[] { 2, 2, 2, 2 };

		final double[] bBegin = new double[] { 0.5, 0.5, 0.5, 0.5 };
		final double[] bEnd = new double[] { 1.5, 1.5, STRectangle.UC,
				STRectangle.NOW };

		STRectangle a = new STRectangle(new NDRectangle(new NDPoint(aBegin),
				new NDPoint(aEnd)), 0.0, basic);
		STRectangle b = new STRectangle(new NDRectangle(new NDPoint(bBegin),
				new NDPoint(bEnd)), 0.0, basic);

		assertTrue(a.intersects(b));
		assertTrue(b.intersects(a));
	}

	@Test
	public void testVolumeClosed() {
		final double[] aBegin = new double[] { 0, 0, 0, 0 };
		final double[] aEnd = new double[] { 2, 2, 2, 2 };

		STRectangle a = new STRectangle(new NDRectangle(new NDPoint(aBegin),
				new NDPoint(aEnd)), 0.0, basic);

		assertEquals(5.656854249492, a.volume(), 0.00001);
	}

	@Test
	public void testVolumeOpen() {
		final double[] aBegin = new double[] { 0, 0, 0, 0 };
		final double[] aEnd = new double[] { 2, 2, STRectangle.UC,
				STRectangle.NOW };

		STRectangle a = new STRectangle(new NDRectangle(new NDPoint(aBegin),
				new NDPoint(aEnd)), 0.0, basic);

		assertEquals(5656854.249492, a.volume(), 0.00001);
	}

	@Test
	public void testBiTemporalArea() {
		final double[] aBegin = new double[] { 0, 0, 0, 0 };
		final double[] aEnd = new double[] { 2, 2, STRectangle.UC,
				STRectangle.NOW };

		STRectangle a = new STRectangle(new NDRectangle(new NDPoint(aBegin),
				new NDPoint(aEnd)), 0.0, basic);

		assertEquals(4000000, a.biTemporalArea(), 0.00001);
	}

	public void testSpatialArea() {
		final double[] aBegin = new double[] { 0, 0, 0, 0 };
		final double[] aEnd = new double[] { 2, 2, STRectangle.UC,
				STRectangle.NOW };

		STRectangle a = new STRectangle(new NDRectangle(new NDPoint(aBegin),
				new NDPoint(aEnd)), 0.0, basic);

		assertEquals(4, a.spatialVolume(), 0.00001);
	}

	@Test
	public void testGetNowValue() {
		final double[] aBegin = new double[] { 0, 0, 0, 0 };
		final double[] aEnd = new double[] { 2, 2, STRectangle.UC,
				STRectangle.NOW };

		STRectangle a = new STRectangle(new NDRectangle(new NDPoint(aBegin),
				new NDPoint(aEnd)), 0.0, basic);

		assertEquals(2000, a.getNowValue(), 0.00001);
	}

	@Test
	public void testUnion() {
		final double[] aBegin = new double[] { 0, 0, 0, 0 };
		final double[] aEnd = new double[] { 2, 2, 2, 2 };

		final double[] bBegin = new double[] { 0.5, 0.5, 0.5, 0.5 };
		final double[] bEnd = new double[] { 1.5, 1.5, STRectangle.UC,
				STRectangle.NOW };

		STRectangle a = new STRectangle(new NDRectangle(new NDPoint(aBegin),
				new NDPoint(aEnd)), 0.0, basic);
		STRectangle b = new STRectangle(new NDRectangle(new NDPoint(bBegin),
				new NDPoint(bEnd)), 0.0, basic);

		final double[] expected = new double[] { 2, 2, STRectangle.UC,
				STRectangle.NOW };

		STRectangle c = a.union(b);

		assertArrayEquals(a.getBegin().toArray(), c.getBegin().toArray(),
				0.0001);
		assertArrayEquals(expected, c.getEnd().toArray(), 0.0001);
	}

	@Test
	public void testIntersectToBox() {
		final double[] aBegin = new double[] { 0, 0, 0, 0 };
		final double[] aEnd = new double[] { 2, 2, 2, 2 };

		final double[] bBegin = new double[] { 0.5, 0.5, 0.5, 0.5 };
		final double[] bEnd = new double[] { 1.5, 1.5, STRectangle.UC,
				STRectangle.NOW };

		STRectangle a = new STRectangle(new NDRectangle(new NDPoint(aBegin),
				new NDPoint(aEnd)), 0.0, basic);
		STRectangle b = new STRectangle(new NDRectangle(new NDPoint(bBegin),
				new NDPoint(bEnd)), 0.0, basic);

		final double[] expected = new double[] { 1.5, 1.5, 2.0,
				2.0 };

		STRectangle c = a.intersectToBox(b);

		assertArrayEquals(b.getBegin().toArray(), c.getBegin().toArray(),
				0.0001);
		assertArrayEquals(expected, c.getEnd().toArray(), 0.0001);
	}

}
