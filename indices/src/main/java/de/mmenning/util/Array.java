package de.mmenning.util;

import java.util.Comparator;

public class Array {

	/**
	 * Moves every entry one position to the right, beginning with
	 * <code>fromIndexndex</code>. Therefore the entry at position
	 * <code>toIndex +1 </code> will be overridden with the entry at position
	 * <code>toIndex</code> and so on, until <code>fromIndex</code>.
	 * <code>fromIndex</code> will be overridden by <code>null</code>
	 * 
	 * @param array
	 *            array in which entries should be shifted
	 * @param fromIndex
	 *            index from which the shifting begins
	 * @param toIndex
	 *            index where the shifting ends
	 * 
	 */
	public static <E> void rightShift(final E[] array, final int fromIndex,
			final int toIndex) {

		rangeCheck(array.length, fromIndex, toIndex+1);

		for (int i = toIndex+1; i > fromIndex; i--) {
			array[i] = array[i - 1];
		}
		array[fromIndex] = null;

	}

	/**
	 * Moves every entry one position to the left, beginning with
	 * <code>fromIndexndex</code>. Therefore the entry at position
	 * <code>fromIndex</code> will be overridden with the entry at position
	 * <code>fromIndex +1</code> and so on, until <code>toIndex</code>.
	 * <code>toIndex</code> will be overridden by <code>null</code>
	 * 
	 * @param array
	 *            array in which entries should be shifted
	 * @param fromIndex
	 *            index from which the shifting begins
	 * @param toIndex
	 *            index where the shifting ends
	 * 
	 */
	public static <E> void leftShift(final E[] array, final int fromIndex,
			final int toIndex) {
		rangeCheck(array.length, fromIndex, toIndex);

		for (int i = fromIndex; i < toIndex; i++) {
			array[i] = array[i + 1];
		}
		array[toIndex] = null;
	}

	public static <E> int binSearch(final E[] array, final E o, int begin,
			int end, final Comparator<? super E> comp) {
		
		rangeCheck(array.length, begin, end);

		while (begin <= end) {
			int middle = (begin + end) / 2;
			E mid = array[middle];

			int compare = comp.compare(mid, o);

			if (compare < 0) {
				begin = middle + 1;
			} else if (compare > 0) {
				end = middle - 1;
			} else {
				return middle;
			}
		}

		return -1;

	}

	public static <E extends Comparable<E>> int binSearch(final E[] array,
			final E o, int begin, int end) {
		rangeCheck(array.length, begin, end);

		while (begin <= end) {
			int middle = (begin + end) / 2;
			E mid = array[middle];

			int compare = mid.compareTo(o);

			if (compare < 0) {
				begin = middle + 1;
			} else if (compare > 0) {
				end = middle - 1;
			} else {
				return middle;
			}
		}

		return -1;
	}

	private static void rangeCheck(int length, int fromIndex, int toIndex) {
		if (fromIndex > toIndex) {
			throw new IllegalArgumentException("fromIndex(" + fromIndex
					+ ") > toIndex(" + toIndex + ")");
		}
		if (fromIndex < 0) {
			throw new ArrayIndexOutOfBoundsException(fromIndex);
		}
		if (toIndex > length) {
			throw new ArrayIndexOutOfBoundsException(toIndex);
		}
	}

}
