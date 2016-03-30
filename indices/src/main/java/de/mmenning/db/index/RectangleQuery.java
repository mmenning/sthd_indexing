package de.mmenning.db.index;

/**
 * A query on a NDRectangleKeyIndex. {@link #query} will be called for every
 * visited element and must return <code>true</code> if the query should be
 * continued.
 */
public interface RectangleQuery {

	public boolean query(NDRectangleKey k);

}
