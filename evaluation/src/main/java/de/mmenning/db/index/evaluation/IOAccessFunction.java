package de.mmenning.db.index.evaluation;

import de.mmenning.db.index.NDRectangleKeyIndex;
import de.mmenning.db.storage.BufferedStorage;
import de.mmenning.db.storage.CountingStorage;

public class IOAccessFunction
		implements EvaluationGoalFunction<Long> {

	private static IOAccessFunction instance;

	private IOAccessFunction() {
	}

	public static IOAccessFunction getInstance() {
		if (instance == null) {
			instance = new IOAccessFunction();
		}
		return instance;
	}


   public Long value(NDRectangleKeyIndex index) {

		CountingStorage c;

		if (index.getStorageManager().getClass() == BufferedStorage.class) {
			c = (CountingStorage) ((BufferedStorage) index.getStorageManager())
					.getStorage();
		} else if (index.getStorageManager().getClass() == CountingStorage.class) {
			c = (CountingStorage) index.getStorageManager();
		} else {

			throw new UnsupportedOperationException(
					"unsupported StorageManager type");
		}
		return c.getIOCounter().getReads() + c.getIOCounter().getWrites();
	}

}
