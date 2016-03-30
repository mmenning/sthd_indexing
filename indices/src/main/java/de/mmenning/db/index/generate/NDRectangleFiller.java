package de.mmenning.db.index.generate;

import de.mmenning.db.index.NDRectangleKey;
import de.mmenning.db.index.NDRectangleKeyIndex;

import java.util.Collection;
import java.util.Collections;
import java.util.LinkedList;

public class NDRectangleFiller {

	public static  void mixUp(final NDRectangleKeyIndex index,
			final Collection<NDRectangleKey> insertionCandidates,
			final int estimatedSize) {

		if (estimatedSize > insertionCandidates.size()) {
			throw new IllegalArgumentException(
					"Cannot create Index with more than the given Keys");
		}

		final LinkedList<NDRectangleKey> toInsert = new LinkedList<NDRectangleKey>();

		toInsert.addAll(insertionCandidates);
		Collections.shuffle(toInsert);

		final LinkedList<NDRectangleKey> inserted = new LinkedList<NDRectangleKey>();

		int toDelete = insertionCandidates.size() - estimatedSize;

		final int maxInsertOrDelete = (int) (toDelete * Math.random());

		while (toDelete > 0 || !toInsert.isEmpty()) {


			if (!toInsert.isEmpty()) {
				int insertNow = (int) (Math.random() * maxInsertOrDelete);

				while (!toInsert.isEmpty() && insertNow > 0) {
					index.insert(toInsert.peek());
					inserted.add(toInsert.poll());
					insertNow--;
				}

			}
			if (toDelete > 0) {
				Collections.shuffle(inserted);
				int deleteNow = (int) (Math.random() * maxInsertOrDelete);

				while (!inserted.isEmpty() && toDelete > 0 && deleteNow > 0) {
					index.delete(inserted.poll());
					toDelete--;
					deleteNow--;
				}
			}
		}
	}

}
