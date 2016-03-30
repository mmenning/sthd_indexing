package de.mmenning.db.index;

import de.mmenning.db.storage.ObjectReference;

/**
 * 
 * @author Mathias Menninghaus (mathias.menninghaus@uos.de)
 * 
 */
public class NDRectangleKey {

	private final ObjectReference object;
	private final NDRectangle key;

	public NDRectangleKey(final ObjectReference object, final NDRectangle key) {
		this.object = object;
		this.key = key;
	}

	/**
	 * Identify the Object by a {@link NDRectangle}. This may not be unique. Use
	 * {@link #getObject} to get a unique key.
	 * 
	 * @return NDKey of this object.
	 */
	public NDRectangle getNDKey() {
		return this.key;
	}

	public ObjectReference getObject() {
		return this.object;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((key == null) ? 0 : key.hashCode());
		result = prime * result + ((object == null) ? 0 : object.hashCode());
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
		NDRectangleKey other = (NDRectangleKey) obj;
		if (key == null) {
			if (other.key != null)
				return false;
		} else if (!key.equals(other.key))
			return false;
		if (object == null) {
			if (other.object != null)
				return false;
		} else if (!object.equals(other.object))
			return false;
		return true;
	}

}
