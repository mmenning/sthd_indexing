package de.mmenning.db.index.sequential;

import de.mmenning.db.index.*;
import de.mmenning.db.storage.*;

/**
 * Wrapper to emulate SequentialScan on a {@link IterableStorageManager}.
 *
 * @author Mathias Menninghaus (mathias.mennighaus@uos.de)
 */
public class SequentialAccess implements NDRectangleKeyIndex {

   protected IterableStorageManager storageManager;
   protected final int dim;
   protected final int byteSize;
   private int size;
   private NowGen now;

   public SequentialAccess(int dim, StorageManager storageManager,
                           NowGen now) {
      if (!(storageManager instanceof IterableStorageManager)) {
         throw new IllegalArgumentException("Only Iterable Storage Managers " +
               "are allowed for Sequential Access Structure.");
      }
      this.storageManager = (IterableStorageManager) storageManager;
      this.size = 0;
      this.dim = dim;
      this.byteSize = (IOUtils.referenceByteSize() + (this.getDim() * 8 * 2));
      this.now = now;
   }

   @Override
   public boolean contains(NDRectangleKey key) {

      for (ObjectReference reference : storageManager) {
         NDRectangleKey stored = (NDRectangleKey) storageManager.load(reference).getObject();
         if (stored.equals(key)) {
            return true;
         }
      }
      return false;

   }

   @Override
   public boolean delete(NDRectangleKey key) {
      for (ObjectReference reference : storageManager) {
         NDRectangleKey stored = (NDRectangleKey) storageManager.load(reference).getObject();
         if (stored.equals(key)) {
            storageManager.delete(reference);
            this.size--;
            return true;
         }
      }
      return false;
   }

   @Override
   public void getContained(NDRectangle region, RectangleQuery q) {
      for (ObjectReference reference : storageManager) {
         NDRectangleKey key = (NDRectangleKey) storageManager.load(reference).getObject();
         if (STFunctions.contains(region, key.getNDKey(), now)) {
            if (!q.query(key)) {
               return;
            }
         }
      }
   }

   @Override
   public int getDim() {
      return this.dim;
   }

   @Override
   public void getIntersected(NDRectangle region, RectangleQuery q) {
      for (ObjectReference reference : storageManager) {
         NDRectangleKey key = (NDRectangleKey) storageManager.load(reference).getObject();

         if (STFunctions.intersects(region, key.getNDKey(), now)) {
            if (!q.query(key)) {
               return;
            }
         }
      }
   }

   @Override
   public boolean insert(NDRectangleKey key) {
      if (contains(key)) {
         return false;
      } else {
         this.storageManager.store(new SimpleStorable(ObjectReference
               .getReference(key), key, byteSize));
         this.size++;
         return true;
      }
   }

   @Override
   public boolean update(NDRectangleKey oldOne, NDRectangleKey newOne) {
      if (!this.delete(oldOne)) {
         return false;
      } else {
         return this.insert(newOne);
      }
   }

   @Override
   public int size() {
      return this.size;
   }

   @Override
   public IterableStorageManager getStorageManager() {
      return storageManager;
   }
}
