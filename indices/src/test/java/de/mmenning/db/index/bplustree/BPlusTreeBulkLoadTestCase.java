package de.mmenning.db.index.bplustree;

import static org.junit.Assert.assertTrue;

import org.junit.Before;

import de.mmenning.db.storage.ObjectReference;
import de.mmenning.db.storage.SimpleStorable;
import de.mmenning.db.storage.Storable;

public class BPlusTreeBulkLoadTestCase extends BPlusTreeTestCase {

	@Override
	public BPlusTree<Integer, ObjectReference> getTree() {
		return this.tree;
	}

	@Override
	@Before
	public void setUp() {
		BPlusTree<Integer, ObjectReference> tree = new BPlusTree<Integer, ObjectReference>(
				2, 2) {

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
		added = new ObjectReference[this.initialSize];

		for (int i = 0; i < added.length; i++) {
			added[i] = ObjectReference.getReference(new Object());
		}

		for (int i = 0; i < added.length; i++) {
			assertTrue(tree.insert(i, added[i]));
		}

		this.tree = new BPlusTree<Integer, ObjectReference>(tree.iterator(),2, 2, null,
				tree.getStorageManager()) {
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
	}

}
