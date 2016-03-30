package de.mmenning.db.index.generate;

import java.util.LinkedList;

import de.mmenning.db.index.NDRectangle;
import de.mmenning.db.index.NDRectangleKey;

public class DatasetGeneration {

	public static LinkedList<LinkedList<NDRectangleKey>> createKeys(
			int amount, int size, NDRectangleGenerator gen) {

		LinkedList<LinkedList<NDRectangleKey>> l = new LinkedList<>();

		for (int i = 0; i < amount; i++) {
			l.add(createKeys(size, gen));
		}

		return l;

	}

	public static LinkedList<LinkedList<NDRectangle>> createListOfRectangles(
			int amount, int size, NDRectangleGenerator gen) {
		LinkedList<LinkedList<NDRectangle>> l = new LinkedList<>();

		for (int i = 0; i < amount; i++) {
			l.add(createRectangles(size, gen));
		}

		return l;

	}

	public static LinkedList<NDRectangle> createRectangles(int size,
			NDRectangleGenerator gen) {
		LinkedList<NDRectangle> l = new LinkedList<>();

		for (int i = 0; i < size; i++) {

			l.add(gen.getNextRectangle());

		}

		return l;
	}

	public static LinkedList<NDRectangleKey> createKeys(int size,
			final NDRectangleGenerator gen) {
		LinkedList<NDRectangleKey> l = new LinkedList<>();

		for (int i = 0; i < size; i++) {

			l.add(new NDRectangleKey(new StatsObject().getObjectReference(), gen
					.getNextRectangle()));
		}

		return l;
	}
	
}
