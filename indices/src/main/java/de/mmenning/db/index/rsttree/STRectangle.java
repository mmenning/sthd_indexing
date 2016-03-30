package de.mmenning.db.index.rsttree;

import de.mmenning.db.index.STFunctions;
import de.mmenning.db.index.NDPoint;
import de.mmenning.db.index.NDRectangle;

public class STRectangle extends NDRectangle {

	private static final long serialVersionUID = 2706940453768924961L;

	private final double delta;

	private STConstants constants;

	public static final int STATIC = 0;

	public static final int GROWING_REC = 1;

	public static final int GROWING_STAIR = 2;

	public static final double UC = STFunctions.CURRENT;
	public static final double NOW = STFunctions.CURRENT;

	public STRectangle(NDRectangle rec, double delta, STConstants constants) {
		super(rec);

		if (rec.getDim() < 2) {
			throw new IllegalArgumentException(
					"must have at least 2 dimensions");
		}

		this.delta = delta;
		this.constants = constants;
	}

	public STRectangle(NDRectangle rec, STConstants constants) {
		this(rec, 0.0, constants);
	}

	@Override
	public double quadraticCenterDistance(NDRectangle another) {
		return quadraticCenterDistance((STRectangle) another);
	}

	@Override
	public int compareTo(int dim, boolean greater, NDRectangle another) {
		return this.compareTo(dim, greater, (STRectangle) another);
	}

	public int compareTo(final int dim, final boolean greater,
			final STRectangle another) {

		if (dim < this.getSpatialDim()) {
			return super.compareTo(dim, greater, another);
		}
		if (dim == this.getTTDim()) {
			if (greater) {
				return Double.compare(this.getTtMax(), another.getTtMax());
			} else {
				return Double.compare(this.getTtMin(), another.getTtMin());
			}
		}
		if (dim == this.getVTDim()) {
			if (greater) {
				return Double.compare(this.getVtMax(), another.getVtMax());
			} else {
				return Double.compare(this.getVtMin(), another.getVtMin());
			}
		}
		throw new IllegalArgumentException("Unkown dimension: " + dim);
	}

	@Override
	public boolean contains(NDPoint p) {
		throw new UnsupportedOperationException();
	}

	@Override
	public boolean contains(NDRectangle another) {
		return this.contains((STRectangle) another);
	}

	public boolean contains(final STRectangle another) {
		for (int dim = 0; dim < this.getSpatialDim(); dim++) {
			if (!(this.getBegin().lesserEquals(dim, another.getBegin()))
					|| !this.getEnd().greaterEquals(dim, another.getEnd())) {
				return false;
			}
		}
		/*
		 * Transaction Time
		 */
		if (this.getTtMin() > another.getTtMin()
				|| this.getTtMax() < another.getTtMax()) {
			return false;
		}
		/*
		 * Valid Time
		 */
		if (this.getVtMin()

		> another.getVtMin() || this.getVtMax()

		< another.getVtMax()) {
			return false;
		}
		return true;
	}

	public boolean intersects(final STRectangle another) {
		for (int dim = 0; dim < this.getSpatialDim(); dim++) {
			if (this.getBegin().greater(dim, another.getEnd())
					|| this.getEnd().lesser(dim, another.getBegin())) {
				return false;
			}
		}
		/*
		 * Transaction Time
		 */
		if (this.getTtMin() > another.getTtMax()
				|| this.getTtMax() < another.getTtMin()) {
			return false;
		}
		/*
		 * Valid Time
		 */
		if (this.getVtMin() > another.getVtMax()
				|| this.getVtMax() < another.getVtMin()) {
			return false;
		}

		return true;
	}

	@Override
	public boolean disjoins(NDPoint p) {
		throw new UnsupportedOperationException();
	}

	@Override
	public boolean disjoins(NDRectangle another) {
		throw new UnsupportedOperationException();
	}

	public STConstants getConstants() {
		return constants;
	}

	public double getDelta() {
		return this.delta;
	}

	@Override
	public double getIntervalLength(final int dim) {
		if (dim < this.getSpatialDim()) {
			return super.getIntervalLength(dim);
		} else {
			if (dim == this.getTTDim()) {
				return Math.abs(this.getTtMax() - this.getTtMin());
			} else if (dim == this.getVTDim()) {
				return Math.abs(this.getVtMax() - this.getVtMin());
			} else {
				throw new IllegalArgumentException("Unknown dimension " + dim);
			}
		}

	}

	public double getNowValue() {
		return this.getConstants().getNowValue()
				+ this.getConstants().getPValue();
	}

	public int getSpatialDim() {
		return this.getDim() - 2;
	}

	public int getTTDim() {
		return this.getDim() - 2;
	}

	protected double getTtMax() {
		if (this.isUC()) {
			return this.getNowValue();
		} else {
			return this.getEnd().getValue(this.getTTDim());
		}
	}

	public double getTtMin() {
		return this.getBegin().getValue(this.getTTDim());
	}

	public int getType() {
		if (this.isNow() && this.isUC()) {
			return GROWING_STAIR;
		} else if (this.isUC()) {
			return GROWING_REC;
		} else {
			return STATIC;
		}
	}

	protected int getVTDim() {
		return this.getDim() - 1;
	}

	public double getVtMax() {
		if (this.isNow()) {
			return this.getNowValue() + this.getDelta();
		} else {
			return this.getEnd().getValue(this.getVTDim());
		}
	}

	public double getVtMin() {
		return this.getBegin().getValue(this.getVTDim());
	}

	@Override
	public double intersect(NDRectangle another) {
		return this.intersect((STRectangle) another);
	}

	public double intersect(final STRectangle another) {
		final STRectangle result = this.intersectToBox(another);
		return result == null ? 0 : result.volume();
	}

	@Override
	public boolean intersects(NDRectangle another) {
		return this.intersects((STRectangle) another);
	}

	@Override
	public NDRectangle intersectToBox(NDRectangle another) {
		throw new UnsupportedOperationException();
	}

	public STRectangle intersectToBox(final STRectangle another) {

		if (!this.intersects(another)) {
			return null;
		}

		final double[] begin = new double[this.getDim()];
		final double[] end = new double[this.getDim()];

		for (int dim = 0; dim < this.getSpatialDim(); dim++) {
			begin[dim] = Math.max(this.getBegin().getValue(dim), another
					.getBegin().getValue(dim));
			end[dim] = Math.min(this.getEnd().getValue(dim), another.getEnd()
					.getValue(dim));
		}

		double ttMin = Math.max(this.getTtMin(), another.getTtMin());
		double ttMax = Math.min(this.getTtMax(), another.getTtMax());

		double vtMin, vtMax;
		vtMin = Math.max(this.getVtMin(), another.getVtMin());

		double delta;

		if (this.isNow() == another.isNow()) {
			if (!this.isNow()) {
				delta = 0.0;
				vtMax = Math.min(this.getVtMax(), another.getVtMax());
			} else {
				delta = Math.min(this.getDelta(), another.getDelta());
				vtMax = NOW;
			}
		} else {
			vtMax = Math.min(this.getVtMax(), another.getVtMax());
			delta = 0.0;
		}
		begin[this.getTTDim()] = ttMin;
		begin[this.getVTDim()] = vtMin;
		end[this.getTTDim()] = ttMax;
		end[this.getVTDim()] = vtMax;

		return new STRectangle(new NDRectangle(new NDPoint(begin), new NDPoint(
				end)), delta, this.getConstants());
	}

	public boolean isNow() {
		return Double.compare(this.getEnd().getValue(this.getVTDim()), NOW) == 0;
	}

	public boolean isUC() {
		return Double.compare(this.getEnd().getValue(this.getTTDim()), UC) == 0;
	}

	@Override
	public double separation(NDRectangle another, int dim) {
		throw new UnsupportedOperationException();
	}

	public void setConstants(STConstants constants) {
		this.constants = constants;
	}

	@Override
	public double simpleMargin() {
		double alpha = this.getConstants().getAlphaValue();
		
		if (alpha <= 0.0) {
			return Math.pow(this.biTemporalMargin(), alpha + 1.0)
					+ this.spatialMargin();
		} else {
			return this.biTemporalMargin()
					+ Math.pow(this.spatialMargin(), 1.0 - alpha);
		}
		
	}

	protected double biTemporalMargin() {
		double vt = Math.abs(this.getVtMax() - this.getVtMin());
		double tt = Math.abs(this.getTtMax() - this.getTtMin());
		return vt + tt;
	}

	protected double spatialMargin() {
		double simpleMargin = 0;
		for (int dim = 0; dim < this.getSpatialDim(); dim++) {
			simpleMargin += this.getIntervalLength(dim);
		}
		return simpleMargin;
	}

	public double spatialVolume() {
		double volume = 1.0;
		for (int dim = 0; dim < this.getSpatialDim(); dim++) {
			volume *= this.getIntervalLength(dim);
		}
		return volume;
	}

	@Override
	public NDRectangle union(NDRectangle another) {
		return this.union((STRectangle) another);
	}

	public STRectangle union(final STRectangle another) {

		final double[] begin = new double[this.getDim()];
		final double[] end = new double[this.getDim()];

		for (int dim = 0; dim < this.getSpatialDim(); dim++) {
			begin[dim] = Math.min(this.getBegin().getValue(dim), another
					.getBegin().getValue(dim));
			end[dim] = Math.max(this.getEnd().getValue(dim), another.getEnd()
					.getValue(dim));
		}

		final double vtMin = Math.min(this.getVtMin(), another.getVtMin());
		final double ttMin = Math.min(this.getTtMin(), another.getTtMin());
		final double ttMax;

		final double vtMax;
		final double delta;

		if (this.isUC() || another.isUC()) {
			ttMax = UC;
		} else {
			ttMax = Math.max(this.getTtMax(), another.getTtMax());
		}

		final double deltab;
		final double thisDeltab = this.isNow() ? (this.getDelta()) : (this
				.getVtMax() - this.getTtMin());
		final double anotherDeltab = another.isNow() ? (another.getDelta())
				: (another.getVtMax() - another.getTtMin());
		deltab = Math.max(thisDeltab, anotherDeltab);

		final double vtb;
		final double thisVtb = this.isNow() ? ((this.isUC() ? this
				.getNowValue() : this.getTtMax()) + this.getDelta()) : (this
				.getVtMax());
		final double anotherVtb = another.isNow() ? ((another.isUC() ? another
				.getNowValue() : another.getTtMax()) + another.getDelta())
				: (another.getVtMax());
		vtb = Math.max(thisVtb, anotherVtb);

		final boolean existsOneStair = (this.isNow() && this.isUC())
				|| (another.isNow() && another.isUC());

		final boolean allNotGrowing = !this.isUC() && !another.isUC();

		if (existsOneStair
				|| (allNotGrowing && area(ttMin, ttMax, vtMin,
						this.getNowValue() + deltab) < area(ttMin, ttMax,
						vtMin, vtb))) {
			vtMax = NOW;
			delta = deltab
			// fix due to java double uncertainty
			+ 0.00000000000001;
		} else {
			vtMax = vtb;
			delta = 0.0
			// fix due to java double uncertainty
			+ 0.00000000000001;
		}

		// end original algorithm

		begin[this.getTTDim()] = ttMin;
		begin[this.getVTDim()] = vtMin;
		end[this.getTTDim()] = ttMax;
		end[this.getVTDim()] = vtMax;

		return new STRectangle(new NDRectangle(new NDPoint(begin), new NDPoint(
				end)), delta, this.getConstants());
	}

	@Override
	public double volume() {
		double alpha = this.getConstants().getAlphaValue();

		if (alpha <= 0.0) {
			return Math.pow(this.biTemporalArea(), alpha + 1.0)
					* this.spatialVolume();
		} else {
			return this.biTemporalArea()
					* Math.pow(this.spatialVolume(), 1.0 - alpha);
		}
	}

	protected double area(double xMin, double xMax, double yMin, double yMax) {
		return Math.abs(xMax - xMin) * Math.abs(yMax - yMin);
	}

	protected double biTemporalArea() {
		double vt = Math.abs(this.getVtMax() - this.getVtMin());
		double tt = Math.abs(this.getTtMax() - this.getTtMin());
		return vt * tt;
	}

	@Override
	public String toString() {
		return super.toString() + " " + this.getDelta();
	}

}
