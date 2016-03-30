package de.mmenning.db.index.generate;

import de.mmenning.db.storage.ObjectReference;
import de.mmenning.db.storage.Referable;

public class StatsObject implements Referable{

	private static int ID = 0;
	private final int id;

	private final ObjectReference or; 
	
	public StatsObject() {
		this.id = ID++;
		this.or = ObjectReference.getReference(this);
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + id;
		return result;
	}

	public int getID(){
		return this.id;
	}
	
	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		StatsObject other = (StatsObject) obj;
		if (id != other.id)
			return false;
		return true;
	}
	
	@Override
	public String toString(){
		return ""+id ;
	}

	@Override
	public ObjectReference getObjectReference() {
		return this.or;
	}
	
	
}
