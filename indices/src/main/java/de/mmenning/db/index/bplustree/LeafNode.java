package de.mmenning.db.index.bplustree;

import java.util.Comparator;
import java.util.LinkedList;

import de.mmenning.db.storage.ObjectReference;

public class LeafNode<K, V> extends BNode<K, LinkedList<V>> {

	ObjectReference getRightSibling() {
		return rightSibling;
	}

	void setRightSibling(ObjectReference rightSibling) {
		this.rightSibling = rightSibling;
	}

	private ObjectReference rightSibling;

	LeafNode(Comparator<? super K> comp, int size) {
		super(comp, size);
	}

	@Override
	boolean isLeaf() {
		return true;
	}

}
