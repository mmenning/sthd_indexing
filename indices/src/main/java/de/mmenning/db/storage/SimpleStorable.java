package de.mmenning.db.storage;

public class SimpleStorable extends Storable {

	public SimpleStorable(ObjectReference or, Object object, int byteSize) {
		super(or);
		this.object = object;
		this.byteSize = byteSize;
	}

	private final int byteSize;
	
	private final Object object;

	@Override
	public Object getObject() {
		return this.object;
	}

	@Override
	public int getBytes() {
		return this.byteSize;
	}

}
