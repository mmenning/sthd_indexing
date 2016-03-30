package de.mmenning.db.index.rtree;

import java.io.Serializable;

import de.mmenning.db.index.NDRectangle;
import de.mmenning.db.index.NDRectangleKey;
import de.mmenning.db.storage.ObjectReference;

/**
 * Class Entry consists of a child Element and a Minimum Bounding Box enclosing
 * this Element.
 * 
 * @author Mathias Menninghaus (mathias.menninghaus@uos.de)
 * 
 */
public final class Entry implements Serializable {

	private static final long serialVersionUID = -2780616838906529323L;
	private final ObjectReference child;

	public boolean isNodeEntry() {
		return this.child.getMeta() == Node.class
				|| this.child.getMeta().getSuperclass() == Node.class;
	}

	private NDRectangle mbbox;

	public Entry(final NDRectangle mbbox, final Node n) {
		this.setMBBox(mbbox);
		this.child = n.getObjectReference();
	}

	/**
	 * Construct a new Entry with no father but b as its child, and the bounding
	 * box of b as the bounding box of this Entry.
	 * 
	 * @param b
	 *            element which should be enclosed by this Entry
	 */
	public Entry(final NDRectangleKey b) {
		this.setMBBox(b.getNDKey());
		this.child = b.getObject();
	}

	public boolean equals(final Entry e) {
		return this.child.equals(e.child);
	}

	/**
	 * Return the minimum bounding box of this Entry.
	 * 
	 * @return minimum bounding box this Entry.
	 */
	public NDRectangle getMBBox() {
		return this.mbbox;
	}

	public ObjectReference getChild() {
		return this.child;
	}

	@Override
	public String toString() {
		return "" + this.child;
	}

	public void setMBBox(final NDRectangle r) {
		if (r == null) {
			throw new NullPointerException();
		}
		this.mbbox = r;
	}

}
