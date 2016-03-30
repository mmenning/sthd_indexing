package de.mmenning.db.index.pyramid;

import static org.junit.Assert.assertTrue;

import java.util.Arrays;

import org.junit.Before;

import de.mmenning.db.index.NDPointKey;
import de.mmenning.db.index.generate.NDPointGenerator;
import de.mmenning.util.math.Uniform;

public class ExtendedPyramidTechniqueTestCase extends PyramidTechniqueTestCase {

	@Override
	@Before
	public void setUp() throws Exception {
		this.dim = 5;
		this.initialSize = 10000;
		this.tree = new ExtendedPyramidTechnique(this.dim,  4096);

		double[] median = new double[dim];
		Arrays.fill(median, 0.25);

		((ExtendedPyramidTechnique) this.tree).setMedian(median);
		this.pointGen = new NDPointGenerator(this.dim, new Uniform(0, 1));
		NDPointKey insert;

		for (int i = 0; i < this.initialSize; i++) {
			insert = this.nextNDPointKey();
			assertTrue(this.added.add(insert));
			assertTrue(this.tree.insert(insert));
		}
	}

}
