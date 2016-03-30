package de.mmenning.db.storage;

public class ObjectWrapper implements Referable {

	private final ObjectReference or;
	private final Object o;

	public ObjectWrapper(Object o) {
		this.or = ObjectReference.getReference(o);
		this.o = o;
	}

	@Override
	public ObjectReference getObjectReference() {
		return this.or;
	}

}
