package de.mmenning.db.storage;

import java.io.Serializable;

public final class ObjectReference implements Serializable {

	private static final long serialVersionUID = 7025806404584420425L;

	private final long id;

	private Class<?> meta;

	private ObjectReference(long id, Class<?> meta) {
		this.id = id;
		this.meta = meta;
	}

	public long getID() {
		return id;
	}

	private static long global_id = 0;

	public static ObjectReference getReference(Object o) {
		return new ObjectReference(global_id++, o.getClass());
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + (int) (id ^ (id >>> 32));
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
		ObjectReference other = (ObjectReference) obj;
		if (id != other.id)
			return false;
		return true;
	}

	@Override
	public String toString() {
		return "" + this.id;
	}

	public Class<?> getMeta() {
		return this.meta;
	}

}
