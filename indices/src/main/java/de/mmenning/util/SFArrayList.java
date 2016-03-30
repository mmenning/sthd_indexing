package de.mmenning.util;

import java.util.Arrays;
import java.util.Comparator;
import java.util.Iterator;
import java.util.NoSuchElementException;

/*
 * Sorted Set, Shifting, fixed array List
 */

public class SFArrayList<E> implements Iterable<E> {

	private Object[] array;
	private Comparator<E> comp;
	private int lastEntry;
	private int mod;

	public SFArrayList(int capacity) {
		this(capacity, null);
	}

	public SFArrayList(int capacity, Comparator<E> comparator) {
		this.array = new Object[capacity];
		this.comp = comparator;
		this.lastEntry = -1;
		this.mod = 0;
	}

	public void remove(int index) {
		if (index > this.lastEntry + 1) {
			throw new IllegalArgumentException("index not in range");
		}
		Array.leftShift(this.array, index, this.lastEntry);
		this.mod++;
		this.lastEntry--;
	}

	public boolean empty() {
		return this.lastEntry == -1;
	}

	public boolean full() {
		return this.lastEntry == this.array.length - 1;
	}

	public int size() {
		return this.lastEntry + 1;
	}

	public boolean remove(E elem) {
		int index = findIndex(elem);

		if (index >= 0 && this.array[index].equals(elem)) {
			this.remove(index);
			return true;
		} else {
			return false;
		}
	}

	private int findIndex(E elem) {
		int index;
		if (comp == null) {
			index = Arrays.binarySearch(this.array, elem);
		} else {
			index = Arrays.binarySearch((E[]) this.array, elem, this.comp);
		}
		return index;
	}

	public boolean add(E elem) {
		if (this.full()) {
			throw new IllegalArgumentException("List is full");
		}

		int index = findIndex(elem);
		if (index >= 0 && this.array[index].equals(elem)) {
			return false;
		}
		index++;
		Array.rightShift(this.array, index, this.lastEntry);
		this.array[index] = elem;
		this.mod++;
		this.lastEntry++;
		return true;
	}

	public boolean contains(E elem) {
		int index = this.findIndex(elem);
		return index >= 0 && this.array[index].equals(elem);
	}

	public E get(int index) {
		return (E) this.array[index];
	}

	@Override
	public Iterator<E> iterator() {
		return null;
	}

	private class SFArrayListIterator implements Iterator<E> {

		private int currentMod = mod;
		private int current = 0;

		@Override
		public boolean hasNext() {
			return current <= lastEntry;
		}

		@Override
		public E next() {
			if (!hasNext()) {
				throw new NoSuchElementException();
			}
			return (E) array[current++];
		}

		@Override
		public void remove() {
			throw new UnsupportedOperationException();
		}

	}

	public int capacity() {
		return this.array.length;
	}

}
