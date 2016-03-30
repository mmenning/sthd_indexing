package de.mmenning.db.index.pyramid;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import de.mmenning.db.index.pyramid.PyramidFunctions;
import org.junit.Test;

public class PyramidFunctionsTestCase {

	@Test
	public void testAllRectangleIntersectLowerPyramid() {

		double[] qmin = new double[] { 0.25, 0.25 };
		double[] qmax = new double[] { 0.5, 0.5 };

		double[] qcaretmin = PyramidFunctions.convertToQCaret(qmin);
		double[] qcaretmax = PyramidFunctions.convertToQCaret(qmax);

		assertTrue(PyramidFunctions.intersectsLowerPyramid(0, qcaretmin,
				qcaretmax));
		assertTrue(PyramidFunctions.intersectsLowerPyramid(1, qcaretmin,
				qcaretmax));
	}

	@Test
	public void test0RectangleIntersectLowerPyramid() {

		double[] qmin = new double[] { 0.5, 0.25 };
		double[] qmax = new double[] { 0.5, 0.25 };

		double[] qcaretmin = PyramidFunctions.convertToQCaret(qmin);
		double[] qcaretmax = PyramidFunctions.convertToQCaret(qmax);

		assertFalse(PyramidFunctions.intersectsLowerPyramid(0, qcaretmin,
				qcaretmax));
		assertTrue(PyramidFunctions.intersectsLowerPyramid(1, qcaretmin,
				qcaretmax));
	}

	@Test
	public void test12RectangleIntersectLowerPyramid() {

		double[] qmin = new double[] { 0.25, 0.5 };
		double[] qmax = new double[] { 0.25, 0.5 };

		double[] qcaretmin = PyramidFunctions.convertToQCaret(qmin);
		double[] qcaretmax = PyramidFunctions.convertToQCaret(qmax);

		assertTrue(PyramidFunctions.intersectsLowerPyramid(0, qcaretmin,
				qcaretmax));
		assertFalse(PyramidFunctions.intersectsLowerPyramid(1, qcaretmin,
				qcaretmax));
	}

	@Test
	public void testAllRectangleIntersectHigherPyramid() {

		double[] qmin = new double[] { 0.25, 0.25 };
		double[] qmax = new double[] { 0.75, 0.75 };

		double[] qcaretmin = PyramidFunctions.convertToQCaret(qmin);
		double[] qcaretmax = PyramidFunctions.convertToQCaret(qmax);

		assertTrue(PyramidFunctions.intersectsHigherPyramid(2, qcaretmin,
				qcaretmax));
		assertTrue(PyramidFunctions.intersectsHigherPyramid(3, qcaretmin,
				qcaretmax));
	}

	@Test
	public void test2RectangleIntersectHigherPyramid() {

		double[] qmin = new double[] { 0.75, 0.5 };
		double[] qmax = new double[] { 0.75, 0.5 };

		double[] qcaretmin = PyramidFunctions.convertToQCaret(qmin);
		double[] qcaretmax = PyramidFunctions.convertToQCaret(qmax);

		assertTrue(PyramidFunctions.intersectsHigherPyramid(2, qcaretmin,
				qcaretmax));
		assertFalse(PyramidFunctions.intersectsHigherPyramid(3, qcaretmin,
				qcaretmax));
	}

	@Test
	public void test3RectangleIntersectHigherPyramid() {

		double[] qmin = new double[] { 0.5, 0.75 };
		double[] qmax = new double[] { 0.5, 0.75 };

		double[] qcaretmin = PyramidFunctions.convertToQCaret(qmin);
		double[] qcaretmax = PyramidFunctions.convertToQCaret(qmax);

		assertFalse(PyramidFunctions.intersectsHigherPyramid(2, qcaretmin,
				qcaretmax));
		assertTrue(PyramidFunctions.intersectsHigherPyramid(3, qcaretmin,
				qcaretmax));
	}

	@Test
	public void testSinglePointIntersection0() {

		double[] qmin = new double[] { 0.25, 0.5 };
		double[] qmax = new double[] { 0.25, 0.5 };

		double[] qcaretmin = PyramidFunctions.convertToQCaret(qmin);
		double[] qcaretmax = PyramidFunctions.convertToQCaret(qmax);

		assertTrue(PyramidFunctions.intersectsPyramid(0, qcaretmin, qcaretmax));
		assertFalse(PyramidFunctions.intersectsPyramid(1, qcaretmin, qcaretmax));
		assertFalse(PyramidFunctions.intersectsPyramid(2, qcaretmin, qcaretmax));
		assertFalse(PyramidFunctions.intersectsPyramid(3, qcaretmin, qcaretmax));
	}

	@Test
	public void testSinglePointIntersection1() {

		double[] qmin = new double[] { 0.5, 0.25 };
		double[] qmax = new double[] { 0.5, 0.25 };

		double[] qcaretmin = PyramidFunctions.convertToQCaret(qmin);
		double[] qcaretmax = PyramidFunctions.convertToQCaret(qmax);

		assertFalse(PyramidFunctions.intersectsPyramid(0, qcaretmin, qcaretmax));
		assertTrue(PyramidFunctions.intersectsPyramid(1, qcaretmin, qcaretmax));
		assertFalse(PyramidFunctions.intersectsPyramid(2, qcaretmin, qcaretmax));
		assertFalse(PyramidFunctions.intersectsPyramid(3, qcaretmin, qcaretmax));
	}

	@Test
	public void testSinglePointIntersection2() {

		double[] qmin = new double[] { 0.75, 0.5 };
		double[] qmax = new double[] { 0.75, 0.5 };

		double[] qcaretmin = PyramidFunctions.convertToQCaret(qmin);
		double[] qcaretmax = PyramidFunctions.convertToQCaret(qmax);

		assertFalse(PyramidFunctions.intersectsPyramid(0, qcaretmin, qcaretmax));
		assertFalse(PyramidFunctions.intersectsPyramid(1, qcaretmin, qcaretmax));
		assertTrue(PyramidFunctions.intersectsPyramid(2, qcaretmin, qcaretmax));
		assertFalse(PyramidFunctions.intersectsPyramid(3, qcaretmin, qcaretmax));
	}

	@Test
	public void testSinglePointIntersection3() {

		double[] qmin = new double[] { 0.5, 0.75 };
		double[] qmax = new double[] { 0.5, 0.75 };

		double[] qcaretmin = PyramidFunctions.convertToQCaret(qmin);
		double[] qcaretmax = PyramidFunctions.convertToQCaret(qmax);

		assertFalse(PyramidFunctions.intersectsPyramid(0, qcaretmin, qcaretmax));
		assertFalse(PyramidFunctions.intersectsPyramid(1, qcaretmin, qcaretmax));
		assertFalse(PyramidFunctions.intersectsPyramid(2, qcaretmin, qcaretmax));
		assertTrue(PyramidFunctions.intersectsPyramid(3, qcaretmin, qcaretmax));
	}

	@Test
	public void testLineIntersection03() {

		double[] qmin = new double[] { 0.25, 0.75 };
		double[] qmax = new double[] { 0.5, 0.75 };

		double[] qcaretmin = PyramidFunctions.convertToQCaret(qmin);
		double[] qcaretmax = PyramidFunctions.convertToQCaret(qmax);

		assertTrue(PyramidFunctions.intersectsPyramid(0, qcaretmin, qcaretmax));
		assertFalse(PyramidFunctions.intersectsPyramid(1, qcaretmin, qcaretmax));
		assertFalse(PyramidFunctions.intersectsPyramid(2, qcaretmin, qcaretmax));
		assertTrue(PyramidFunctions.intersectsPyramid(3, qcaretmin, qcaretmax));
	}

	@Test
	public void testLineIntersection12() {

		double[] qmin = new double[] { 0.5, 0.25 };
		double[] qmax = new double[] { 0.75, 0.25 };

		double[] qcaretmin = PyramidFunctions.convertToQCaret(qmin);
		double[] qcaretmax = PyramidFunctions.convertToQCaret(qmax);

		assertFalse(PyramidFunctions.intersectsPyramid(0, qcaretmin, qcaretmax));
		assertTrue(PyramidFunctions.intersectsPyramid(1, qcaretmin, qcaretmax));
		assertTrue(PyramidFunctions.intersectsPyramid(2, qcaretmin, qcaretmax));
		assertFalse(PyramidFunctions.intersectsPyramid(3, qcaretmin, qcaretmax));
	}

	@Test
	public void testRightIntersection() {

		double[] qmin = new double[] { 0.75, 0.0 };
		double[] qmax = new double[] { 1.0, 1.0 };

		double[] qcaretmin = PyramidFunctions.convertToQCaret(qmin);
		double[] qcaretmax = PyramidFunctions.convertToQCaret(qmax);

		assertFalse(PyramidFunctions.intersectsPyramid(0, qcaretmin, qcaretmax));
		assertTrue(PyramidFunctions.intersectsPyramid(1, qcaretmin, qcaretmax));
		assertTrue(PyramidFunctions.intersectsPyramid(2, qcaretmin, qcaretmax));
		assertTrue(PyramidFunctions.intersectsPyramid(3, qcaretmin, qcaretmax));
	}

	@Test
	public void testLeftIntersection() {

		double[] qmin = new double[] { 0.0, 0.0 };
		double[] qmax = new double[] { 0.25, 1.0 };

		double[] qcaretmin = PyramidFunctions.convertToQCaret(qmin);
		double[] qcaretmax = PyramidFunctions.convertToQCaret(qmax);

		assertTrue(PyramidFunctions.intersectsPyramid(0, qcaretmin, qcaretmax));
		assertTrue(PyramidFunctions.intersectsPyramid(1, qcaretmin, qcaretmax));
		assertFalse(PyramidFunctions.intersectsPyramid(2, qcaretmin, qcaretmax));
		assertTrue(PyramidFunctions.intersectsPyramid(3, qcaretmin, qcaretmax));
	}

	@Test
	public void testTopIntersection() {

		double[] qmin = new double[] { 0.0, 0.75 };
		double[] qmax = new double[] { 1.0, 1.0 };

		double[] qcaretmin = PyramidFunctions.convertToQCaret(qmin);
		double[] qcaretmax = PyramidFunctions.convertToQCaret(qmax);

		assertTrue(PyramidFunctions.intersectsPyramid(0, qcaretmin, qcaretmax));
		assertFalse(PyramidFunctions.intersectsPyramid(1, qcaretmin, qcaretmax));
		assertTrue(PyramidFunctions.intersectsPyramid(2, qcaretmin, qcaretmax));
		assertTrue(PyramidFunctions.intersectsPyramid(3, qcaretmin, qcaretmax));
	}

	private final double EPSILON = 0.001;


	@Test
	public void testBottomHQueryInterval() {

		double[] qmin = new double[] { 0.0, 0.0 };
		double[] qmax = new double[] { 1.0, 0.25 };

		assertDoubleArrayEquals(new double[] { 0.25, 0.5 },
				PyramidFunctions.getHQueryInterval(0, qmin, qmax), EPSILON);

		assertDoubleArrayEquals(new double[] { 0.25, 0.5 },
				PyramidFunctions.getHQueryInterval(1, qmin, qmax), EPSILON);

		assertDoubleArrayEquals(new double[] { 0.25, 0.5 },
				PyramidFunctions.getHQueryInterval(2, qmin, qmax), EPSILON);
	}

	@Test
	public void testRightHQueryInterval() {

		double[] qmin = new double[] { 0.75, 0.0 };
		double[] qmax = new double[] { 1.0, 1.0 };

		assertDoubleArrayEquals(new double[] { 0.25, 0.5 },
				PyramidFunctions.getHQueryInterval(1, qmin, qmax), EPSILON);

		assertDoubleArrayEquals(new double[] { 0.25, 0.5 },
				PyramidFunctions.getHQueryInterval(2, qmin, qmax), EPSILON);

		assertDoubleArrayEquals(new double[] { 0.25, 0.5 },
				PyramidFunctions.getHQueryInterval(3, qmin, qmax), EPSILON);
	}

	@Test
	public void testLeftHQueryInterval() {

		double[] qmin = new double[] { 0.0, 0.0 };
		double[] qmax = new double[] { 0.25, 1.0 };

		assertDoubleArrayEquals(new double[] { 0.25, 0.5 },
				PyramidFunctions.getHQueryInterval(0, qmin, qmax), EPSILON);

		assertDoubleArrayEquals(new double[] { 0.25, 0.5 },
				PyramidFunctions.getHQueryInterval(1, qmin, qmax), EPSILON);

		assertDoubleArrayEquals(new double[] { 0.25, 0.5 },
				PyramidFunctions.getHQueryInterval(3, qmin, qmax), EPSILON);
	}

	@Test
	public void testTopHQueryInterval() {

		double[] qmin = new double[] { 0.0, 0.75 };
		double[] qmax = new double[] { 1.0, 1.0 };

		assertDoubleArrayEquals(new double[] { 0.25, 0.5 },
				PyramidFunctions.getHQueryInterval(0, qmin, qmax), EPSILON);

		assertDoubleArrayEquals(new double[] { 0.25, 0.5 },
				PyramidFunctions.getHQueryInterval(2, qmin, qmax), EPSILON);

		assertDoubleArrayEquals(new double[] { 0.25, 0.5 },
				PyramidFunctions.getHQueryInterval(3, qmin, qmax), EPSILON);
	}


	@Test
	public void testHQueryIntervalComplete() {

		double[] qmin = new double[] { 0.25, 0.25 };
		double[] qmax = new double[] { 0.75, 0.75 };

		assertDoubleArrayEquals(new double[] { 0, 0.25 },
				PyramidFunctions.getHQueryInterval(0, qmin, qmax), EPSILON);

		assertDoubleArrayEquals(new double[] { 0, 0.25 },
				PyramidFunctions.getHQueryInterval(1, qmin, qmax), EPSILON);

		assertDoubleArrayEquals(new double[] { 0, 0.25 },
				PyramidFunctions.getHQueryInterval(2, qmin, qmax), EPSILON);

		assertDoubleArrayEquals(new double[] { 0, 0.25 },
				PyramidFunctions.getHQueryInterval(3, qmin, qmax), EPSILON);

	}



	private static void assertDoubleArrayEquals(double[] expected,
			double[] actual, double epsilon) {
		for (int i = 0; i < expected.length; i++) {
			assertEquals(expected[i], actual[i], epsilon);
		}
	}

}
