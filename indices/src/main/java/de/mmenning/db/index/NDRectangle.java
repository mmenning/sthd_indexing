package de.mmenning.db.index;

import java.io.Serializable;

/**
 * A NDRectangle consists of two points, a begin and an end. That means, that
 * for every dimension the lesser value is stored in begin, and the greater
 * value is stored in end. As example, in the two dimensional case the begin is
 * the lower left value and the end is the upper right value.
 * 
 * @author Mathias Menninghaus (mathias.menninghaus@kit.edu)
 * 
 */
public class NDRectangle implements Serializable {

	private static final long serialVersionUID = 1157496260858762073L;

	private final NDPoint begin;
	private final NDPoint end;

	public NDRectangle(final NDPoint x, final NDPoint y) {
		if (x.getDim() != y.getDim()) {
			throw new IllegalArgumentException("length(x) " + x.getDim()
					+ " != length(y) " + y.getDim());
		}

		final double[] begin = new double[x.getDim()];
		final double[] end = new double[y.getDim()];

		/*
		 * Sort such that in each dimension the lower value is in x, and the
		 * higher is in y
		 */
		for (int i = 0; i < x.getDim(); i++) {

			if (Double.isNaN(x.getValue(i)) || Double.isNaN(y.getValue(i))
					|| x.getValue(i) < y.getValue(i)) {
				begin[i] = x.getValue(i);
				end[i] = y.getValue(i);
			} else {
				end[i] = x.getValue(i);
				begin[i] = y.getValue(i);
			}
		}
		this.begin = new NDPoint(begin);
		this.end = new NDPoint(end);
	}

	public NDRectangle(final NDRectangle old) {
		this.begin = new NDPoint(old.getBegin());
		this.end = new NDPoint(old.getEnd());
	}

	/**
	 * Get the start point of this NDRectangle. In the two-dimensional case it
	 * would be the lower left corner.
	 * 
	 * @return the begin of this NDRectangle
	 */
	public NDPoint getBegin() {
		return this.begin;
	}

	/**
	 * Returns the number of dimensions which can be choosen in this NDRectangle
	 * 
	 * @return
	 */
	public int getDim() {
		return this.begin.getDim();
	}

	/**
	 * Get the end point of this NDRectangle. In the two-dimensional case it
	 * would be the upper right corner.
	 * 
	 * @return the end of this NDRectangle
	 */
	public NDPoint getEnd() {
		return this.end;
	}

	@Override
	public String toString() {
		return "{" + this.begin.toString() + " , " + this.end.toString() + "}";
	}

	/**
	 * Compute the distance of the center of bounding box <TT>this</TT> and the
	 * center of the given bounding box <TT>another</TT> .
	 * 
	 * @param another
	 *            Bounding Box to which the distance should be calculated.
	 * 
	 * @return distance between the centers of <TT>this</TT> and
	 *         <TT>another</TT>
	 */
	public double quadraticCenterDistance(final NDRectangle another) {
		double tmp;
		double sum = 0;

		for (int dim = 0; dim < this.getDim(); dim++) {
			tmp = ((this.begin.getValue(dim) + this.end.getValue(dim)) / 2)
					- ((another.begin.getValue(dim) + another.end.getValue(dim)) / 2);
			tmp *= tmp;
			sum += tmp;
		}
		// changed to simpleCenterDistance without sqrt

		return sum;/* Math.sqrt(sum); */
	}

	public int compareTo(final int dim, final boolean greater,
			final NDRectangle another) {
		return greater ? this.end.compareTo(dim, another.end) : this.begin
				.compareTo(dim, another.begin);
	}

	public boolean contains(final NDPoint p) {
		for (int dim = 0; dim < this.getDim(); dim++) {
			if (p.greater(dim, this.end) || p.lesser(dim, this.begin)) {
				return false;
			}
		}
		return true;
	}

	/**
	 * Determine, if this NDRectangle contains another.
	 * 
	 * @param another
	 *            the other object that is tested for being contained in this
	 *            NDRectangle
	 * @return true, if another element is joined by this NDRectangle
	 */
	public boolean contains(final NDRectangle another) {
		for (int dim = 0; dim < this.getDim(); dim++) {
			if (!(this.begin.lesserEquals(dim, another.begin))
					|| !this.end.greaterEquals(dim, another.end)) {
				return false;
			}
		}
		return true;
	}

	public boolean disjoins(final NDPoint p) {
		for (int dim = 0; dim < this.getDim(); dim++) {
			if (p.greater(dim, this.end) || p.lesser(dim, this.begin)) {
				return true;
			}
		}
		return false;
	}

	/**
	 * Determine whether this rectangle disjoins another.
	 * 
	 * @param another
	 *            the other object to be disjoint to this
	 * @return true if the objects are disjoint, else false
	 */
	public boolean disjoins(final NDRectangle another) {
		for (int dim = 0; dim < this.getDim(); dim++) {
			if (this.begin.greater(dim, another.end)
					|| this.end.lesser(dim, another.begin)) {
				return true;
			}
		}
		return false;
	}

	/**
	 * Get the length of Interval at dimension dim
	 * 
	 * @param dim
	 *            index of dimension to get Interval length of.
	 * @return Interval length at specified dimension
	 */
	public double getIntervalLength(final int dim) {
		return Math.abs(this.end.getValue(dim) - this.begin.getValue(dim));
	}

	/**
	 * Calculates the intersect between <TT>this</TT> and <TT>another</TT> as
	 * <TT>double</TT> i.e. the volume of the intersectToBox result.
	 * 
	 * @param another
	 *            <TT> NDRectangle </TT> object to overlap with
	 * @return Overlap as <TT>double</TT>
	 */
	public double intersect(final NDRectangle another) {
		final NDRectangle result = this.intersectToBox(another);
		return result == null ? 0 : result.volume();
	}

	/**
	 * Determine, if two <TT>NDRectangle</TT> intersect each other.
	 * 
	 * @param another
	 *            the other object to test the intersection with
	 * @return true, if there is an intersection, false otherwise.
	 */
	public boolean intersects(final NDRectangle another) {
		for (int dim = 0; dim < this.getDim(); dim++) {
			if (this.begin.greater(dim, another.end)
					|| this.end.lesser(dim, another.begin)) {
				return false;
			}
		}
		return true;
	}

	/**
	 * intersects <TT>this</TT> and <TT>another</TT> object.
	 * 
	 * @param another
	 *            the <TT> NDRectangle </TT> object to be intersected with
	 * @return intersection of the to <TT>T</TT> objects.
	 */
	public NDRectangle intersectToBox(final NDRectangle another) {

		final double[] begin = new double[this.getDim()];
		final double[] end = new double[this.getDim()];

		for (int dim = 0; dim < this.getDim(); dim++) {
			if (this.end.lesser(dim, another.begin)
					|| this.begin.greater(dim, another.end)) {
				return null;
			} else {
				begin[dim] = Math.max(this.begin.getValue(dim),
						another.begin.getValue(dim));
				end[dim] = Math.min(this.end.getValue(dim),
						another.end.getValue(dim));
			}
		}
		return new NDRectangle(new NDPoint(begin), new NDPoint(end));
	}

	/**
	 * Calculate the separation between this and another MBBox.
	 * 
	 * @param another
	 *            the other object to calculate the separation to
	 * @param dim
	 *            Dimension at which the separation should be calculated
	 * @return value of separation between the two MBBox
	 */
	public double separation(final NDRectangle another, final int dim) {

		final double middleThis = (this.begin.getValue(dim) + this.end
				.getValue(dim)) / 2;
		final double middleAnother = (another.begin.getValue(dim) + another.end
				.getValue(dim)) / 2;

		return Math.abs(middleThis - middleAnother);
	}

	/**
	 * Calculates the simple margin of <TT>this</TT> as <TT>double</TT>. That
	 * means, that only the lengths of each dimension are summed up
	 * 
	 * @return "simple" margin of this <TT> NDRectangle </TT> as <TT>double</TT>
	 */
	public double simpleMargin() {
		double simpleMargin = 0;
		for (int dim = 0; dim < this.getDim(); dim++) {
			simpleMargin += this.getIntervalLength(dim);
		}
		return simpleMargin;
	}

	/**
	 * Calculates the union of <TT>this</TT> and <TT>another</TT> object. Union
	 * means the enclosing Rectangle of several rectangles of any dimension.
	 * 
	 * @param another
	 *            <TT> NDRectangle </TT> object to be enclosed together with
	 *            <TT> this </TT>
	 * @return Union enclosing <TT> another </TT> and <TT> this </TT>
	 */
	public NDRectangle union(final NDRectangle another) {

		final double[] begin = new double[this.getDim()];
		final double[] end = new double[this.getDim()];

		for (int dim = 0; dim < this.getDim(); dim++) {
			begin[dim] = Math.min(this.begin.getValue(dim),
					another.begin.getValue(dim));
			end[dim] = Math.max(this.end.getValue(dim),
					another.end.getValue(dim));
		}

		return new NDRectangle(new NDPoint(begin), new NDPoint(end));
	}

	/**
	 * Calculates the volume of this <TT>object</TT> as <TT>double</TT>.
	 * 
	 * @return volume of <TT>this</TT>
	 */
	public double volume() {
		double volume = 1.0;
		for (int dim = 0; dim < this.getDim(); dim++) {
			volume *= this.getIntervalLength(dim);
		}
		return volume;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((begin == null) ? 0 : begin.hashCode());
		result = prime * result + ((end == null) ? 0 : end.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		NDRectangle other = (NDRectangle) obj;
		if (begin == null) {
			if (other.begin != null)
				return false;
		} else if (!begin.equals(other.begin))
			return false;
		if (end == null) {
			if (other.end != null)
				return false;
		} else if (!end.equals(other.end))
			return false;
		return true;
	}

}
