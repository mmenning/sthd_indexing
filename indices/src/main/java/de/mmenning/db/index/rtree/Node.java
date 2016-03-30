package de.mmenning.db.index.rtree;

import java.io.Serializable;
import java.util.Arrays;
import java.util.Comparator;

import de.mmenning.db.storage.ObjectReference;
import de.mmenning.db.storage.Referable;
import de.mmenning.util.FixedArrayList;
import de.mmenning.util.Sort;

/**
 * Class Node represents a Node in the NDRTree-tree. A Node may be a leaf or the
 * root - Node. Except the root Node, every Node must have an Entry element as a
 * father. A Node consists of a number of Entry elements, which may contain
 * references to other Node or NDRectangle elements. The entries must contain
 * only elements of the same type. If the entries contain NDRectangle elements
 * the node is considered to be a leaf. Except the root-Node a Node must contain
 * between NDRTree.minCapacity and NDRTree.maxCapacity entries.
 * 
 * @author Mathias Menninghaus (mathias.menninghaus@kit.edu)
 */
public class Node implements Referable, Serializable {

	private static final long serialVersionUID = -1226887512558238972L;

	/**
	 * An array of Entry elements. Its length is the maximum Capacity in this
	 * NDRTree plus one to compute a efficient splitting method.
	 */
	private final Entry[] entries;

	/**
	 * Last index in the entries array which contains an Entry which should be
	 * part of this sNode.
	 */
	private int lastEntry;

	public int getMaxCapacity() {
		return this.entries.length - 1;
	}

	/**
	 * Construct a Node using the given parameters.
	 * 
	 * 
	 * @param entries
	 *            Array of Entry elments which contain the childs of this Node.
	 * @param lastEntry
	 *            Entry elements used in this Node are from index 0 up to
	 *            lastEntry.
	 */
	private Node(final Entry[] entries, final int lastEntry) {
		this.entries = entries;
		this.lastEntry = lastEntry;
		this.or = ObjectReference.getReference(this);
	}

	/**
	 * Construct a empty Node with father as the father element
	 * 
	 */
	@SuppressWarnings("unchecked")
	public Node(final int capacity) {
		this(new Entry[capacity + 1], -1);
		// this((Entry[]) Array.newInstance(Entry.class, capacity + 1), -1);
	}

	/**
	 * Get Entry at position index
	 * 
	 * @param index
	 *            index of the entry
	 * @return Entry at position index
	 */
	public Entry get(final int index) {
		if (index > this.lastEntry || index < 0) {
			throw new IllegalArgumentException("index " + index + " size "
					+ this.size());
		}
		return this.entries[index];
	}

	/**
	 * Returns all Entries in this Node a List.
	 * 
	 * @return all Entries.
	 */
	public FixedArrayList<Entry> getAll() {
		return new FixedArrayList<Entry>(Arrays.copyOf(this.entries,
				this.entries.length), this.lastEntry);
	}

	/**
	 * Test if this Node is empty or not.
	 * 
	 * @return true, if this Node does not cont
	 */
	public boolean isEmpty() {
		return this.lastEntry == -1;
	}

	/**
	 * Test if this Node is Full or not. A Node is considered to be full, if it
	 * contains NDRTree.maxCapacity+1 Elements.
	 * 
	 * @return true, if the Node is full
	 */
	public boolean isFull() {
		return this.size() == this.entries.length;
	}

	/**
	 * Determine whether this Node is a leaf-node or not
	 * 
	 * @return true, if this is a leaf-node
	 */
	public boolean isLeaf() {
		if (!this.isEmpty()) {
			return !this.entries[0].isNodeEntry();
		} else {
			return true;
		}
	}

	public Entry removeLast() {
		final Entry ret = this.entries[this.lastEntry];
		this.entries[this.lastEntry--] = null;
		return ret;
	}

	/**
	 * Return the number of Entry elements in this Node
	 * 
	 * @return count of Entry elements in this Node.
	 */
	public int size() {
		return this.lastEntry + 1;
	}

	/**
	 * Sort the Entry elements in this Node by the given Comparator.
	 * 
	 * @param comparator
	 *            Comparator over Entry elements which defines the sort Method
	 */
	public void sort(final Comparator<Entry> comparator) {
		Arrays.sort(this.entries, comparator);
	}

	/**
	 * Sort the Entry elements in this Node by the given values descending. It
	 * is assumed, that the double at index i represents the Entry elements at
	 * index i in sthis Node.
	 * 
	 * @param values
	 */
	public void sort(final double[] values) {
		Sort.sort(this.entries, values);
	}

	@Override
	public String toString() {
		return this.getObjectReference() + " " + (this.size()) + "/"
				+ this.entries.length;
	}

	/**
	 * Add the given Entry to this Node. The father reference will be updated,
	 * the bounding box will not be updated.
	 * 
	 * @param toAdd
	 *            Entry to be added.
	 */
	public void addEntry(final Entry toAdd) {
		if (toAdd.isNodeEntry()) {

		} else {
			if (!this.isLeaf()) {
				throw new RuntimeException(
						"Cannot add non-Node in non-leaf-Node");
			}
		}

		if (!this.isFull()) {
			this.entries[++this.lastEntry] = toAdd;
		} else {
			throw new RuntimeException("Node " + this + " is full!");
		}
	}

	/**
	 * Get the Index of a given Entry in this Node.
	 * 
	 * @param e
	 *            Entry to be searched for
	 * @return the index of the given Entry or -1 if it was not found
	 */
	public int findEntryIndex(final Entry e) {
		for (int i = 0; i <= this.lastEntry; i++) {
			if (this.entries[i].equals(e)) {
				return i;
			}
		}
		return -1;
	}

	/**
	 * Removes Entry e from this node. e might contain a leaf or another node
	 * 
	 * @param e
	 *            Entry to be deleted
	 * @return true if this contained e, false otherwise
	 */
	public boolean removeEntry(final Entry e) {
		return null != this.removeEntry(this.findEntryIndex(e));
	}

	/**
	 * Removes Entry at index entryIndex from this node. It might be a leaf or a
	 * another node.
	 * 
	 * @param entryIndex
	 *            index, at which the Entry should be deleted.
	 * @return true if this contained an Entry at entryIndex, false otherwise
	 */
	public Entry removeEntry(final int entryIndex) {
		if (entryIndex != -1) {
			Entry removed = this.entries[entryIndex];
			if (entryIndex != this.lastEntry) {
				this.entries[entryIndex] = this.entries[this.lastEntry];
			}
			this.entries[this.lastEntry--] = null;

			return removed;
		} else {
			return null;
		}
	}

	private final ObjectReference or;

	@Override
	public ObjectReference getObjectReference() {
		return this.or;
	}
}
