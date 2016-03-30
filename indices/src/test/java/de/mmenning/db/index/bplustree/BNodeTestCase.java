package de.mmenning.db.index.bplustree;

import static org.junit.Assert.assertEquals;

import java.util.LinkedList;

import de.mmenning.db.index.bplustree.KeyValuePair;
import de.mmenning.db.index.bplustree.LeafNode;
import org.junit.Test;

public class BNodeTestCase {

	@Test(expected = IllegalArgumentException.class)
	public void testAddFirst() {

		LeafNode<Integer, Integer> test = new LeafNode<>(null, 4);

		test.addFirst(new KeyValuePair<Integer, LinkedList<Integer>>(1,
				new LinkedList<Integer>()));
		test.addFirst(new KeyValuePair<Integer, LinkedList<Integer>>(0,
				new LinkedList<Integer>()));
		test.addFirst(new KeyValuePair<Integer, LinkedList<Integer>>(1,
				new LinkedList<Integer>()));

	}

	@Test(expected = IllegalArgumentException.class)
	public void testAddLast() {

		LeafNode<Integer, Integer> test = new LeafNode<>(null, 4);

		test.addLast(new KeyValuePair<Integer, LinkedList<Integer>>(1,
				new LinkedList<Integer>()));
		test.addLast(new KeyValuePair<Integer, LinkedList<Integer>>(2,
				new LinkedList<Integer>()));
		test.addLast(new KeyValuePair<Integer, LinkedList<Integer>>(1,
				new LinkedList<Integer>()));

	}

	@Test
	public void testGetIndex() {

		LeafNode<Integer, Integer> test = new LeafNode<>(null, 4);

		test.addLast(new KeyValuePair<Integer, LinkedList<Integer>>(1,
				new LinkedList<Integer>()));
		test.addLast(new KeyValuePair<Integer, LinkedList<Integer>>(3,
				new LinkedList<Integer>()));
		test.addLast(new KeyValuePair<Integer, LinkedList<Integer>>(5,
				new LinkedList<Integer>()));

		assertEquals(-1, test.getIndex(0));
		assertEquals(-2, test.getIndex(2));
		assertEquals(-3, test.getIndex(4));
		assertEquals(-4, test.getIndex(6));
		assertEquals(0, test.getIndex(1));
		assertEquals(1, test.getIndex(3));
		assertEquals(2, test.getIndex(5));

	}

}
