package de.mmenning.db.storage;

/**
 * @author Mathias Menninghaus (mathias.mennighaus@uos.de)
 */
public interface IterableStorageManager extends StorageManager,
      Iterable<ObjectReference> {
}
