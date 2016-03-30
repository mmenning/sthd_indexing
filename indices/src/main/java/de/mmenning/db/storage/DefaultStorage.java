package de.mmenning.db.storage;

public class DefaultStorage {

	private static StorageManager instance;

	public static StorageManager getInstance() {
		if (instance == null) {
			instance = new BufferedStorage(10, 4096);
		}
		return instance;
	}
}
