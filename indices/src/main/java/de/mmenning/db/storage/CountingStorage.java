package de.mmenning.db.storage;

import java.util.HashMap;
import java.util.Iterator;
import java.util.NoSuchElementException;

public class CountingStorage implements IterableStorageManager {

   private HashMap<ObjectReference, Storable> file;

   private final int blockSize;

   public CountingStorage(int blockSize) {
      this.file = new HashMap<>();
      this.iocount = new IOCounter();
      this.blockSize = blockSize;
   }

   private IOCounter iocount;

   public IOCounter getIOCounter() {
      return this.iocount;
   }

   @Override
   public Storable load(ObjectReference or) {
      Storable st = file.get(or);
      if (st == null) {
         throw new NoSuchElementException();
      }
      iocount.incReads(IOUtils.bytesToBlocks(st.getBytes(), this.blockSize));
      return st;
   }

   @Override
   public void delete(ObjectReference or) {
      Storable st = file.remove(or);
      iocount.incWrites(1);
   }

   @Override
   public void store(Storable st) {
      file.put(st.getObjectReference(), st);
      iocount.incWrites(IOUtils.bytesToBlocks(st.getBytes(), this.blockSize));
   }

   @Override
   public void cleanUp() {
      this.file.clear();
   }

   @Override
   public Iterator<ObjectReference> iterator() {
      final Iterator<ObjectReference> iterator = file.keySet().iterator();
      return new Iterator<ObjectReference>() {
         @Override
         public boolean hasNext() {
            return iterator.hasNext();
         }

         @Override
         public ObjectReference next() {
            return iterator.next();
         }
      };
   }
}
