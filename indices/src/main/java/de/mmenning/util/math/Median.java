package de.mmenning.util.math;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;

public class Median {

	public static final int SELECT_MIN_LENGTH = 50;
	public static final int SELECT_GROUP_LENGTH = 5;

	public static <E> E select(final ArrayList<E> s, final int k,
			final Comparator<E> comp) {
		final int n = s.size();
		if (n < SELECT_MIN_LENGTH) {
			Collections.sort(s, comp);
			return s.get(k);
		} else {
			final ArrayList<E> lower = new ArrayList<E>(n / 2 + 1);
			final ArrayList<E> equals = new ArrayList<E>(n / 2 + 1);
			final ArrayList<E> higher = new ArrayList<E>(n / 2 + 1);

			final E[] sArray = (E[]) s.toArray();

			final ArrayList<E> medians = new ArrayList<E>(n / 5 + 1);

			int j = 0;
			for (; j < n - SELECT_GROUP_LENGTH; j += SELECT_GROUP_LENGTH) {
				Arrays.sort(sArray, j, j + SELECT_GROUP_LENGTH, comp);

				medians.add(sArray[j + SELECT_GROUP_LENGTH / 2]);

			}

			Arrays.sort(sArray, j, n - 1, comp);
			medians.add(sArray[(j + (n)) / 2]);

			final E medianOfMedians = select(medians, n
					/ (SELECT_MIN_LENGTH / SELECT_GROUP_LENGTH), comp);

			int compResult;
			for (int i = 0; i < n; i++) {
				compResult = comp.compare(sArray[i], medianOfMedians);
				if (compResult < 0) {
					lower.add(sArray[i]);
				} else if (compResult > 0) {
					higher.add(sArray[i]);
				} else {
					equals.add(sArray[i]);
				}
			}
			if (lower.size() > k) {
				return select(lower, k, comp);
			} else if ((lower.size() + equals.size()) > k) {
				return medianOfMedians;
			} else {
				return select(higher, k - lower.size() - equals.size(), comp);
			}
		}
	}

}
