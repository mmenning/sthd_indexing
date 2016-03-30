package de.mmenning.db.index.bplustree;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.util.Iterator;
import java.util.LinkedList;

import de.mmenning.db.index.bplustree.*;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import de.mmenning.db.storage.DefaultStorage;
import de.mmenning.db.storage.ObjectReference;
import de.mmenning.db.storage.SimpleStorable;
import de.mmenning.db.storage.Storable;

public class BPlusTreeTestCase {

	protected BPlusTree<Integer, ObjectReference> tree;

	protected ObjectReference[] added;

	protected final int initialSize = 1000;

	public BPlusTree<Integer, ObjectReference> getTree() {
		return this.tree;
	}

	@Before
	public void setUp() {
		this.tree = new BPlusTree<Integer, ObjectReference>(2, 2) {

			@Override
			protected Storable leafNodeToStorable(
					LeafNode<Integer, ObjectReference> n) {

				for (int i = 0; i < n.size(); i++) {
					assertTrue(n.get(i).getValue().size() > 0);
				}

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
			assertTrue(this.getTree().insert(i, added[i]));
		}
	}

	@After
	public void tearDown() throws Exception {
		DefaultStorage.getInstance().cleanUp();
	}

	@Test
	public void testSize() {
		assertEquals(this.initialSize, this.getTree().size());
	}

	@Test
	public void testInsert() {
		ObjectReference o = ObjectReference.getReference(new Object());

		assertTrue(this.getTree().insert(this.initialSize, o));
		assertFalse(this.getTree().insert(this.initialSize, o));
		assertTrue(this.getTree().insert(this.initialSize + 1, o));
	}

	@Test
	public void testContains() {
		ObjectReference o = ObjectReference.getReference(new Object());

		assertTrue(this.getTree().contains(0, added[0]));
		assertFalse(this.getTree().contains(0, o));

		assertTrue(this.getTree().contains(0));
		assertFalse(this.getTree().contains(this.initialSize));
		for (int i = 0; i < added.length; i++) {
			assertTrue(this.getTree().contains(i));
		}
	}

	@Test
	public void testRangeQueryIterator() {

		int[] valueNumbers = new int[this.initialSize];

		int i = 0;

		Iterator<KeyValuePair<Integer, LinkedList<ObjectReference>>> iter = this
				.getTree().rangeQuery(0, initialSize / 2);

		while (iter.hasNext()) {
			KeyValuePair<Integer, LinkedList<ObjectReference>> k = iter.next();
			assertTrue(k.getKey().compareTo(initialSize / 2) <= 0);
			valueNumbers[k.getKey()] += k.getValue().size();
			i += k.getValue().size();
		}
		assertEquals(this.initialSize / 2 + 1, i);

	}

	@Test
	public void testIterator() {

		int[] valueNumbers = new int[this.initialSize];

		int i = 0;

		for (KeyValuePair<Integer, LinkedList<ObjectReference>> k : this
				.getTree()) {
			valueNumbers[k.getKey()] += k.getValue().size();
			i += k.getValue().size();
		}
		assertEquals(this.initialSize, i);

	}

	@Test
	public void testSorted() {

		Query<Integer, ObjectReference> q = new Query<Integer, ObjectReference>() {

			Integer last;

			@Override
			public boolean query(Integer key, LinkedList<ObjectReference> list) {
				if (last == null) {
					last = key;
				} else {
					assertTrue(last.compareTo(key) < 0);
					last = key;
				}
				return true;
			}
		};

		this.getTree().rangeQuery(0, this.initialSize, q);
	}

	@Test
	public void testRemove() {
		ObjectReference o = ObjectReference.getReference(new Object());

		assertFalse(this.getTree().insert(0, added[0]));
		assertFalse(this.getTree().remove(this.initialSize));

		for (int i = 0; i < added.length; i++) {

			try {
				assertTrue(this.getTree().remove(i));
			} catch (IndexOutOfBoundsException ex) {
				System.out.println(i);
			}
			;
		}

		assertEquals(0, this.getTree().size());

		assertTrue(this.getTree().insert(0, added[0]));
	}

	@Test
	public void testInsertEqual() {
		ObjectReference o1 = ObjectReference.getReference(new Object());
		ObjectReference o2 = ObjectReference.getReference(new Object());

		assertTrue(this.getTree().insert(0, o1));
		assertTrue(this.getTree().insert(0, o2));
		assertEquals(this.initialSize + 2, this.getTree().size());
	}

	@Test
	public void testRemoveEqual() {
		ObjectReference o1 = ObjectReference.getReference(new Object());
		ObjectReference o2 = ObjectReference.getReference(new Object());

		assertTrue(this.getTree().insert(0, o1));
		assertTrue(this.getTree().insert(0, o2));

		assertTrue(this.getTree().remove(0, added[0]));

		assertEquals(this.initialSize + 1, this.getTree().size());

		assertTrue(this.getTree().remove(0));
		assertEquals(this.initialSize - 1, this.getTree().size());
	}

	@Test
	public void testRangeQueryAfterMerge() {
		final LinkedList<ObjectReference> l = new LinkedList<>();

		for (int i = initialSize / 2; i < this.initialSize; i++) {
			assertTrue(this.getTree().remove(i, added[i]));
		}

		Query<Integer, ObjectReference> q = new Query<Integer, ObjectReference>() {

			@Override
			public boolean query(Integer key, LinkedList<ObjectReference> values) {
				l.addAll(values);
				return true;
			}

		};

		this.getTree().rangeQuery(0, this.initialSize, q);

		assertEquals(this.initialSize / 2, l.size());

	}

	@Test
	public void testRangeQuery() {

		final LinkedList<ObjectReference> l = new LinkedList<>();

		Query<Integer, ObjectReference> q = new Query<Integer, ObjectReference>() {

			@Override
			public boolean query(Integer key, LinkedList<ObjectReference> values) {
				l.addAll(values);
				return true;
			}

		};

		this.getTree().rangeQuery(0, this.initialSize, q);

		assertEquals(this.initialSize, l.size());

		l.clear();

		this.getTree().rangeQuery(-10, 0, q);

		assertEquals(1, l.size());

		l.clear();

		this.getTree().rangeQuery(1000, 10001, q);

		assertEquals(0, l.size());

		l.clear();

		this.getTree().rangeQuery(10, 0, q);

		assertEquals(0, l.size());

		l.clear();

		this.getTree().rangeQuery(10, 100, q);

		assertEquals(91, l.size());
	}
}
