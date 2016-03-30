package de.mmenning.db.index;

import de.mmenning.db.storage.StorableIndex;

/**
 * A NDKeyIndex is an Index that stores NDPointKeys.
 * 
 * @author Mathias Menninghaus (mathias.menninghaus@kit.edu)
 * 
 */
public interface NDPointKeyIndex extends StorableIndex {

	/**
	 * Determine whether this NDKeyIndex contains the specified key or not.
	 * 
	 * @param key
	 *            NDPointKey to be searched for
	 * @return true if this NDPointKey Index contains the given key, false if
	 *         not
	 */
	public boolean contains(NDPointKey key);

	/**
	 * Delete the given NDPointKey.
	 * 
	 * @param key
	 *            NDPointKey to be deleted
	 * @return true if success, false if key does not exist
	 */
	public boolean delete(NDPointKey key);

	/**
	 * Get the dimension of this NDPointKeyIndex
	 * 
	 * @return dimension of this NDPointKeyIndex
	 */
	public int getDim();

	/**
	 * Insert a NDPointKey element.
	 * 
	 * @param key
	 *            NDPointKey to be inserted
	 * @return true if success, false if key already exists
	 */
	public boolean insert(NDPointKey key);

	/**
	 * 
	 * @param region
	 *            NDRectangle which defines the search region
	 */
	public void regionQuery(NDRectangle region, PointQuery q);

	/**
	 * Returns the size of this NDKeyIndex.
	 * 
	 * @return the amount of NDPointKey elements stored in this NDKeyIndex.
	 */
	public int size();

	/**
	 * Updates an element which key has been altered. Updates
	 * <code>oldOne</code> with <code>newOne</code>. An trivial implementation
	 * may only {@link #delete(NDPointKey)} <code>oldOne</code> and
	 * {@link #insert(NDPointKey)} <code>newOne</code>. Returns
	 * <code>false</code> if <code>oldOne</code> has not already been inserted
	 * into this index or if <code>newOne</code> already is inserted into this
	 * index.
	 * 
	 * @param oldOne
	 *            key element already inserted in this index
	 * @param newOne
	 *            key element which shall replace <code>oldOne</code>
	 * @return <code>true</code> if the update has been successful, else false.
	 *         Also <code>false</code> if <code>oldOne</code> has not already
	 *         been inserted into this index.
	 */
	public boolean update(NDPointKey oldOne, NDPointKey newOne);

}
