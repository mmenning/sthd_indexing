package de.mmenning.db.index.pyramid;

import java.io.Serializable;

public class PyramidValue extends Number implements Comparable<PyramidValue>,
		Serializable {

	private static final long serialVersionUID = -2586125565838378757L;
	private final double value;

	public PyramidValue(int pyramid, double height) {
		if (pyramid < 0) {
			throw new IllegalArgumentException("pyramid negative :" + pyramid);
		}

		if (height > 0.5 || height < 0) {
			throw new IllegalArgumentException("height illegal: " + height);
		}
		this.value = pyramid + height;
	}

	public int getPyramid() {
		return (int) this.value;
	}

	public double getHeight() {
		return this.value - this.getPyramid();
	}

	@Override
	public int compareTo(PyramidValue other) {
		return Double.compare(this.value, other.value);
	}

	@Override
	public double doubleValue() {
		return this.value;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		PyramidValue other = (PyramidValue) obj;
		if (Double.doubleToLongBits(value) != Double
				.doubleToLongBits(other.value))
			return false;
		return true;
	}

	@Override
	public float floatValue() {
		throw new UnsupportedOperationException();
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		long temp;
		temp = Double.doubleToLongBits(value);
		result = prime * result + (int) (temp ^ (temp >>> 32));
		return result;
	}

	@Override
	public int intValue() {
		return (int) this.value;
	}

	@Override
	public long longValue() {
		throw new UnsupportedOperationException();
	}

}
