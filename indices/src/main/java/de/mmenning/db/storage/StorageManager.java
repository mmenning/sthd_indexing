package de.mmenning.db.storage;

public interface StorageManager {

   public Storable load(ObjectReference or);

   public void delete(ObjectReference or);

   public void store(Storable st);

   public void cleanUp();
}
