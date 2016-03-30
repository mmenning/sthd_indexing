package de.mmenning.db.index.bplustree;

import java.util.LinkedList;

import de.mmenning.db.index.bplustree.*;
import de.mmenning.db.storage.DefaultStorage;
import de.mmenning.db.storage.ObjectReference;
import de.mmenning.db.storage.SimpleStorable;
import de.mmenning.db.storage.Storable;

public class BPlusTreeRemoveBugTracker {

	private static BPlusTree<Integer, ObjectReference> tree;

	private static ObjectReference[] added;

	private static final int initialSize = 20;

	private static void setUp() {
		tree = new BPlusTree<Integer, ObjectReference>(2, 2) {

			@Override
			protected Storable leafNodeToStorable(
					LeafNode<Integer, ObjectReference> n) {
				return new SimpleStorable(n.getObjectReference(), n, 8);
			}

			@Override
			protected Storable dirNodeToStorable(
					DirNode<Integer, ObjectReference> n) {
				return new SimpleStorable(n.getObjectReference(), n, 8);
			}

			@Override
			protected DirNode<Integer, ObjectReference> getDirNodeFromStorable(
					Storable st) {
				return (DirNode<Integer, ObjectReference>) st.getObject();
			}

			@Override
			protected LeafNode<Integer, ObjectReference> getLeafNodeFromStorable(
					Storable st) {
				return (LeafNode<Integer, ObjectReference>) st.getObject();
			}

		};
		added = new ObjectReference[initialSize];

		for (int i = 0; i < added.length; i++) {
			added[i] = ObjectReference.getReference(new Object());
		}

		for (int i = 0; i < added.length; i++) {
			if (!tree.insert(i, added[i])) {
				System.err.println("not inserted");
			}
			;
		}
	}

	public static void main(String[] args) {

		setUp();
		printTree();

		// System.out.println();
		// Integer pos = 0;
		// tree.remove(pos);
		// printTree();

		for (int i = 0; i < initialSize; i++) {
			System.out.println();
			tree.remove(i);
			printTree();
		}

	}

	private static void printContents() {
		int[] valueNumbers = new int[initialSize];

		int i = 0;

		for (KeyValuePair<Integer, LinkedList<ObjectReference>> k : tree) {
			valueNumbers[k.getKey()] += k.getValue().size();
			i += k.getValue().size();
		}
		for (int j = 0; j < valueNumbers.length; j++) {
			System.out.print(valueNumbers[j]);
		}
		System.out.println();

	}

	private static void printTree() {
		for (int i = 0; i < tree.getHeight(); i++) {
			printLevel(tree.getRoot(), i);
			System.out.println();
		}
	}

	private static void printLevel(BNode n, int level) {
		if (level == 0) {
			System.out.print("| " + (n) + " |");
		} else {
			for (int i = 0; i < n.size(); i++) {
				printLevel(
						(BNode) DefaultStorage.getInstance()
								.load((ObjectReference) n.get(i).getValue())
								.getObject(), level - 1);
			}
		}
	}
}
