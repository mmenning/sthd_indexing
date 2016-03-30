package de.mmenning.util.math;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.TreeSet;

import de.mmenning.util.math.Median;
import org.junit.Before;
import org.junit.Test;

public class MedianTestCase {

	private static final int SIZE = 100;
	private ArrayList<Double> l;

	private Comparator<Double> doubleComp = new Comparator<Double>() {

		@Override
		public int compare(Double arg0, Double arg1) {
			return arg0.compareTo(arg1);
		}

	};

	@Before
	public void setUp() {
		TreeSet<Double> ts = new TreeSet<Double>(doubleComp);
		while(ts.size()!=SIZE){
			ts.add(Math.random());
		}
		l = new ArrayList<Double>(SIZE);
		l.addAll(ts);
	}

	@Test
	public void testMedian() {
		double m = Median.select(l, SIZE / 2, doubleComp);
		int greater = 0;
		int lesser = 0;
		int equals = 0;

		for (double d : l) {
			if (d > m) {
				greater++;
			} else if (d < m) {
				lesser++;
			} else {
				equals++;
			}
		}

		Collections.sort(l, doubleComp);
		assertTrue(l.get(l.size()/2).doubleValue()==m);
		assertEquals(greater + lesser + equals, SIZE);
		assertTrue(greater <= SIZE && lesser <= SIZE && equals <= SIZE);

	}

}
