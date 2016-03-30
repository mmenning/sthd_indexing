package de.mmenning.util;

import java.lang.reflect.Array;

public class Reflect {

	@SuppressWarnings("unchecked")
	public static <T> T[] createArray(final Class<T> c, final int size) {
		return (T[]) Array.newInstance(c, size);
	}

}
