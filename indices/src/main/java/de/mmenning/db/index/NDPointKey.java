package de.mmenning.db.index;

import java.io.Serializable;
import java.util.Comparator;

import de.mmenning.db.storage.ObjectReference;

/**
 * An n-dimensional key.
 * 
 * @author Mathias Menninghaus (mathias.menninghaus@kit.edu)
 * 
 */
public class NDPointKey implements Serializable {

	public static  Comparator<NDPointKey> createComparator(final int dim) {
		return new Comparator<NDPointKey>() {
			@Override
			public int compare(final NDPointKey o1, final NDPointKey o2) {
				return o1.compareObjectReference(dim, o2);
			}

		};
	}

	private final ObjectReference object;

	private final NDPoint key;

	public NDPointKey(final ObjectReference object, final NDPoint key) {
		this.object = object;
		this.key = key;
	}

	public int compareObjectReference(final int dim, final NDPointKey another) {
		return this.key.compareTo(dim, another.key);
	}

	/**
	 * Get the n-dimensional Key of this Object. An NDKey is not a primary key,
	 * the object itself may not be identified unique by the NDKey. ObjectReference identify
	 * an unique object use {@link #getObject()}
	 * 
	 * @return NDKey of this object.
	 */
	public NDPoint getNDKey() {
		return this.key;
	}

	public ObjectReference getObject() {
		return this.object;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
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
		NDPointKey other = (NDPointKey) obj;
		if (object == null) {
			if (other.object != null)
				return false;
		} else if (!object.equals(other.object))
			return false;
		return true;
	}
}
