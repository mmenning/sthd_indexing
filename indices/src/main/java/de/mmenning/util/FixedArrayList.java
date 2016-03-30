package de.mmenning.util;

import java.util.Arrays;
import java.util.Iterator;

public class FixedArrayList<T> implements Iterable<T> {

	private final T[] array;
	private int lastEntry;

	public FixedArrayList(final T[] array, final int lastEntry) {
		this.array = Arrays.copyOf(array, array.length);
		this.lastEntry = lastEntry;
	}

	public boolean add(final T elem) {
		if (this.isFull()) {
			return false;
		} else {
			this.array[this.lastEntry++] = elem;
			return true;
		}
	}
	
	public int capacity() {
		return this.array.length;
	}

	public void clear() {
		while (!this.isEmpty()) {
			this.removeLast();
		}
	}

	public boolean contains(final T elem) {
		return this.getIndex(elem) != -1;
	}

	public T get(final int i) {
		return this.array[i];
	}

	public int getIndex(final T elem) {
		for (int i = 0; i <= this.lastEntry; i++) {
			if (this.array[i].equals(elem)) {
				return i;
			}
		}
		return -1;
	}

	public T getLast() {
		return this.array[this.lastEntry];
	}

	public boolean isEmpty() {
		return this.lastEntry == -1;
	}

	public boolean isFull() {
		return this.lastEntry == this.array.length - 1;
	}

	@Override
	public Iterator<T> iterator() {
		return new FixedArrayListIterator();
	}

	public boolean remove(final int index) {
		if (this.isEmpty() || index > this.lastEntry || index < 0) {
			return false;
		} else {
			this.array[index] = this.array[this.lastEntry];
			this.array[this.lastEntry--] = null;
			return true;
		}
	}

	public boolean remove(final T elem) {
		if (this.isEmpty()) {
			return false;
		}
		final int index = this.getIndex(elem);
		if (index == -1) {
			return false;
		}
		this.array[index] = this.array[this.lastEntry];
		this.array[this.lastEntry--] = null;
		return true;
	}

	public T removeLast() {
		final T ret = this.array[this.lastEntry];
		this.array[this.lastEntry--] = null;
		return ret;
	}

	public void set(final T toSet, final int i) {
		this.array[i] = toSet;
	}

	public int size() {
		return this.lastEntry + 1;
	}

	public class FixedArrayListIterator implements Iterator<T> {

		private int actualIndex;

		public FixedArrayListIterator() {
			this.actualIndex = -1;
		}

		@Override
		public boolean hasNext() {
			return this.actualIndex != FixedArrayList.this.lastEntry;
		}

		@Override
		public T next() {
			return FixedArrayList.this.array[++this.actualIndex];
		}

		@Override
		public void remove() {
			throw new UnsupportedOperationException();
		}

	}

}
