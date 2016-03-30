package de.mmenning.db.storage;

import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.NoSuchElementException;

public class BufferedStorage implements IterableStorageManager {

   public static StorageManager getStorageManager(int bufferCapacity,
                                                  int blockSize) {

      if (bufferCapacity == 0) {
         return new CountingStorage(blockSize);
      } else {
         return new BufferedStorage(bufferCapacity, blockSize);
      }
   }

   @Override
   public Iterator<ObjectReference> iterator() {
      final Iterator<ObjectReference> iterator = this.memory.keySet()
            .iterator();
      // new Iterator to exclude remove() operation
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

   private class MemoryEntry {

      final boolean dirty;
      final Storable st;

      MemoryEntry(Storable st, boolean dirty) {
         this.st = st;
         this.dirty = dirty;
      }

      @Override
      public boolean equals(Object obj) {
         if (this == obj)
            return true;
         if (obj == null)
            return false;
         if (getClass() != obj.getClass())
            return false;
         MemoryEntry other = (MemoryEntry) obj;
         if (st == null) {
            if (other.st != null)
               return false;
         } else if (!st.equals(other.st))
            return false;
         return true;
      }

      @Override
      public int hashCode() {
         return st.hashCode();
      }

      private BufferedStorage getOuterType() {
         return BufferedStorage.this;
      }
   }

   private final int blockSize;

   private final int bufferCapacity;

   private int bufferSize;

   private final StorageManager hd;

   private HashMap<ObjectReference, MemoryEntry> memory;
   private LinkedList<Storable> priority;

   /**
    * @param bufferCapacity capacity of this Buffer in blocks
    * @param blockSize      size of one block in bytes
    */
   public BufferedStorage(int bufferCapacity, int blockSize) {
      if (bufferCapacity < 1) {
         throw new IllegalArgumentException(
               "Buffer at least must contain one block");
      }
      if (blockSize <= 0) {
         throw new IllegalArgumentException("blockSize must be positive");
      }

      this.memory = new HashMap<>();
      this.priority = new LinkedList<>();
      this.blockSize = blockSize;
      this.hd = new CountingStorage(this.blockSize);
      this.bufferCapacity = bufferCapacity;
      this.setBufferSize(0);
   }

   public StorageManager getStorage() {
      return this.hd;
   }

   @Override
   public void delete(final ObjectReference or) {

      if (this.memory.containsKey(or)) {
         Storable st = this.memory.remove(or).st;
         if (!this.priority.remove(st)) {
            throw new IllegalStateException("Element isn`t contained");
         }

         this.setBufferSize(this.getBufferSize()
               - IOUtils.bytesToBlocks(st.getBytes(), this.blockSize));
      }

      this.hd.delete(or);

   }

   @Override
   public Storable load(ObjectReference or) {

      MemoryEntry m = this.memory.get(or);

      Storable st = null;

      if (m == null) {
         st = this.hd.load(or);

         int blocksToMove = IOUtils.bytesToBlocks(st.getBytes(),
               this.blockSize);

         try {
            trimToSize(blocksToMove);
         } catch (NoSuchElementException ex) {
            throw ex;
         }

         this.priority.add(st);
         if (this.memory.put(or, new MemoryEntry(st, false)) != null) {
            throw new IllegalStateException("element already contained");
         }
         this.setBufferSize(this.getBufferSize() + blocksToMove);
      } else {
         /*
          * When loading a Storable, only change its priority. The Storable
			 * itself must be changed by a call of store().
			 */
         int oldIndex = this.priority.indexOf(m.st);
         Storable old = this.priority.get(oldIndex);

         this.priority.remove(oldIndex);
         this.priority.addFirst(old);
         st = old;
      }

      return st;
   }

   @Override
   public void store(final Storable st) {

      if (this.memory.containsKey(st.getObjectReference())) {
         /*
          * when storing an already inserted element anew, mark it as dirty
			 */
         if (this.memory.put(st.getObjectReference(), new MemoryEntry(st,
               true)) == null) {
            throw new IllegalStateException("not contained");
         }

			/*
          * correct the size of the element
			 */
         int indexOld = this.priority.indexOf(st);

         long oldSize = this.priority.get(indexOld).getBytes();

         this.setBufferSize(this.getBufferSize()
               - IOUtils.bytesToBlocks(oldSize, this.blockSize));

         this.setBufferSize(this.getBufferSize()
               + IOUtils.bytesToBlocks(st.getBytes(), this.blockSize));
         /*
			 * reinsert element in priority list
			 */
         if (this.priority.remove(indexOld) == null) {
            throw new IllegalStateException("not contained");
         }
         this.priority.addFirst(st);
      } else {
			/*
			 * when storing an non-existend node, put a new node into buffer and
			 * priority list and mark it as dirty.
			 */
         int blocksToMove = IOUtils.bytesToBlocks(st.getBytes(),
               this.blockSize);

         this.trimToSize(blocksToMove);
         this.priority.addFirst(st);
         if (this.memory.put(st.getObjectReference(), new MemoryEntry(st,
               true)) != null) {
            throw new IllegalStateException(
                  "does already contain element, which is inserted as new one");
         }
         this.setBufferSize(this.getBufferSize() + blocksToMove);
      }
   }

   private void checkBuffer() {

      if (this.priority.size() != this.memory.size()) {
         throw new IllegalStateException(
               "priority size does not equal memory size");
      }

      int bufferSize = 0;
      for (Storable st : this.priority) {
         bufferSize += IOUtils.bytesToBlocks(st.getBytes(), this.blockSize);
      }
      if (this.bufferSize != 0 && bufferSize != this.bufferSize) {
         throw new IllegalStateException("buffer size illegal");
      }

   }

   private void setBufferSize(int bufferSize) {

      if (bufferSize < 0) {
         throw new IllegalArgumentException("Buffer size is negative: "
               + bufferSize + " old Size " + this.getBufferSize());
      }

      this.bufferSize = bufferSize;

      if (priority.isEmpty() && this.bufferSize != 0) {
         throw new IllegalStateException(
               "no elements, but buffer is not empty");
      }

   }

   private void trimToSize(int requestedBlocks) {

      while (this.getBufferSize() > this.bufferCapacity - requestedBlocks) {

         // allow unlimited size for one and only one element.
         if (this.priority.size() == 0) {
            break;
         }

         Storable toRemove = null;

         toRemove = this.priority.removeLast();

         int blocksToMove = IOUtils.bytesToBlocks(toRemove.getBytes(),
               this.blockSize);

         this.setBufferSize(this.getBufferSize() - blocksToMove);

         boolean dirty = this.memory.remove(toRemove.getObjectReference()).dirty;
         // if the deleted storable wasn`t marked as dirty, it can be removed
         // without writing the changes on disc.

         if (dirty) {
            this.hd.store(toRemove);
         }
      }
   }

   @Override
   public void cleanUp() {
      this.hd.cleanUp();
      this.memory.clear();
      this.priority.clear();
      this.bufferSize = 0;
   }

   public int getBufferSize() {
      return this.bufferSize;
   }

   public int memorySize() {
      return this.memory.size();
   }

}
