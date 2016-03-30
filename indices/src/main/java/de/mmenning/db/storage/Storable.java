package de.mmenning.db.storage;

public abstract class Storable {

	public abstract Object getObject();

	public abstract int getBytes();
	
	public Storable(ObjectReference or) {
		this.or = or;
	}

	private final ObjectReference or;

	public ObjectReference getObjectReference() {
		return or;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + (int) (this.or.getID() ^ (this.or.getID() >>> 32));
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
		Storable other = (Storable) obj;
		if (this.or.getID() != other.or.getID())
			return false;
		return true;
	}
}
