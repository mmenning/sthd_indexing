package de.mmenning.db.index.pyramid;

import de.mmenning.db.index.NDPointKey;
import de.mmenning.db.index.NDRectangle;
import de.mmenning.db.index.PointQuery;
import de.mmenning.db.storage.DefaultStorage;
import de.mmenning.db.storage.StorageManager;

import java.util.Arrays;

/*
 * Median will not dynamically update
 */
public class ExtendedPyramidTechnique extends PyramidTechnique {

	private final double[] median;

	public ExtendedPyramidTechnique(final int dim, final int blockSize) {
		this(dim, blockSize, DefaultStorage.getInstance());
	}

	public ExtendedPyramidTechnique(final int dim, final int blockSize,
			StorageManager s) {
		super(dim, blockSize, s);
		/*
		 * variable size dependent on block size
		 */
		this.median = new double[this.dim];
		Arrays.fill(this.median, 0.5);
	}

	public double getMedian(int dim) {
		return this.median[dim];
	}

	public void setMedian(double[] median) {
		if (this.size() == 0) {
			if (median.length != this.dim) {
				throw new IllegalArgumentException(
						"median must have the same dimension " + this.dim);
			}
			System.arraycopy(median, 0, this.median, 0, median.length);
		} else {
			throw new IllegalStateException();
		}

	}

	protected double[] scaleTi(double[] point) {
		return ExtendedPyramidFunctions.scaleTi(point, median);
	}

	protected double calcTi(double x, int i) {
		return ExtendedPyramidFunctions.calcTi(x, median[i]);
	}

	protected PyramidValue convert(double[] point) {
		point = ExtendedPyramidFunctions.scaleTi(point, this.median);

		int pyramid = PyramidFunctions.getPyramid(point);
		double height = PyramidFunctions.getHeight(point, pyramid);

		return new PyramidValue(pyramid, height);
	}

	@Override
	public boolean insert(NDPointKey toInsert) {
		double[] point = toInsert.getNDKey().toArray();
		/*
	    * TODO
		 * at this point the original point should be inserted into a histogram,
		 * form which the median may be calculated.
		 *
		 * if median differs from actual median with a threshold of more than
		 * th. the median changes and the whole structure needs to be
		 * re-builded.
		 */

		/*
		 * convert point according to the actual median
		 */
		PyramidValue key = convert(point);

		boolean insert = this.btree.insert(key, toInsert);

		return insert;
	}

	@Override
	public boolean delete(NDPointKey key) {

		boolean deleted = this.btree.remove(convert(key.getNDKey().toArray()),
				key);

		/*
		 * TODO
		 * if deletion is successful update the histogram
		 */
		return deleted;
	}

	@Override
	public boolean contains(NDPointKey key) {
		return this.btree.contains(convert(key.getNDKey().toArray()), key);
	}

	@Override
	public void regionQuery(NDRectangle region, PointQuery q) {

		double[] qmin = scaleTi(region.getBegin().toArray());
		double[] qmax = scaleTi(region.getEnd().toArray());

		doQuery(qmin, qmax, region, q);
	}
}
