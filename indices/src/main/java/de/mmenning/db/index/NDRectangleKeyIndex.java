package de.mmenning.db.index;

import de.mmenning.db.storage.StorableIndex;

public interface NDRectangleKeyIndex extends StorableIndex{

	public boolean contains(NDRectangleKey key);

	public boolean delete(NDRectangleKey key);

	public void getContained(NDRectangle region, RectangleQuery q);
	
	public int getDim();

	public void getIntersected(NDRectangle region, RectangleQuery q);
	
	/**
	 * Inserts the specified key element. May not update an already inserted
	 * element. Returns <code>true</code> if <code>key</code> has successfully
	 * been inserted and <code>false</code> else, i.e. returns
	 * <code>false</code> if <code>key</code> already has been inserted into
	 * this index.
	 * 
	 * @param key
	 *            key-value pair to be inserted.
	 * @return <code>true</code> if <code>key</code> has succesfully been
	 *         inserted, <code>false</code> if <code>key</code> already has been
	 *         inserted into this index
	 */
	public boolean insert(NDRectangleKey key);

	/**
	 * Updates an element which key has been altered. Updates
	 * <code>oldOne</code> with <code>newOne</code>. An trivial implementation
	 * may only {@link #delete(NDRectangleKey)} <code>oldOne</code> and
	 * {@link #insert(NDRectangleKey)} <code>newOne</code>. Returns
	 * <code>false</code> if <code>oldOne</code> has not already been inserted
	 * into this index or if <code>newOne</code> already is inserted into this
	 * index.
	 * 
	 * @param oldOne
	 *            key element already inserted in this index
	 * @param newOne
	 *            key element which shall replace <code>oldOne</code>
	 * @return <code>true</code> if the update has been succesfull, else false.
	 *         Also <code>false</code> if <code>oldObe</code> has not already
	 *         been inserted into this index.
	 */
	public boolean update(NDRectangleKey oldOne, NDRectangleKey newOne);

	public int size();

}
