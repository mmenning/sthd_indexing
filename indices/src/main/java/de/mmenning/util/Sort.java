package de.mmenning.util;

import java.util.ArrayList;

public class Sort {

	public static <T> void sort(final ArrayList<T> toSort, final double[] values) {
		quickSort(toSort, values, 0, values.length - 1);
	}

	public static <T> void sort(final T[] toSort, final double[] values) {
		quickSort(toSort, values, 0, values.length - 1);

	}

	public static <T> void sort(final T[] toSort, final double[] values,
			final int begin, final int end) {
		quickSort(toSort, values, begin, end);

	}

	private static <T> void quickSort(final ArrayList<T> toSort,
			final double[] values, final int begin, final int end) {
		T tmp;
		double help;
		int i = begin;
		int j = end;
		final double x = values[(begin + end) / 2];

		do {
			while (Double.compare(values[i], x) < 0) {
				i++;
			}
			while (Double.compare(values[j], x) > 0) {
				j--;
			}
			if (i <= j) {
				tmp = toSort.get(i);
				toSort.set(i, toSort.get(j));
				toSort.set(j, tmp);

				help = values[i];
				values[i] = values[j];
				values[j] = help;

				i++;
				j--;
			}

		} while (i <= j);

		if (begin < j) {
			quickSort(toSort, values, begin, j);
		}

		if (i < end) {
			quickSort(toSort, values, i, end);
		}
	}

	private static <T> void quickSort(final T[] toSort, final double[] values,
			final int begin, final int end) {
		T tmp;
		double help;
		int i = begin;
		int j = end;
		final double x = values[(begin + end) / 2];

		do {
			while (Double.compare(values[i], x) < 0) {
				i++;
			}
			while (Double.compare(values[j], x) > 0) {
				j--;
			}
			if (i <= j) {
				tmp = toSort[i];
				toSort[i] = toSort[j];
				toSort[j] = tmp;

				help = values[i];
				values[i] = values[j];
				values[j] = help;

				i++;
				j--;
			}

		} while (i <= j);

		if (begin < j) {
			quickSort(toSort, values, begin, j);
		}

		if (i < end) {
			quickSort(toSort, values, i, end);
		}
	}
}
