package de.mmenning.db.index.bplustree;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.NoSuchElementException;

import de.mmenning.db.storage.ObjectReference;
import de.mmenning.db.storage.Referable;

abstract class BNode<K, V> implements Referable, Serializable {

   private final Comparator<? super K> comp;

   @Override
   public ObjectReference getObjectReference() {
      return or;
   }

   private final ObjectReference or;

   private ArrayList<KeyValuePair<K, V>> entries;

   BNode(Comparator<? super K> comp, int size) {
      this.comp = comp;
      this.entries = new ArrayList<>();
      this.or = ObjectReference.getReference(this);
   }

   private int binarySearch(K key) {
      int low = 0;
      int high = this.entries.size() - 1;

      while (low <= high) {
         int mid = (low + high) >>> 1;

         int cmp;
         if (this.comp == null) {
            cmp = ((Comparable<? super K>) this.entries.get(mid).getKey())
                  .compareTo(key);
         } else {
            cmp = this.comp.compare(this.entries.get(mid).getKey(), key);
         }

         if (cmp < 0)
            low = mid + 1;
         else if (cmp > 0)
            high = mid - 1;
         else
            return mid; // key found
      }
      return -(low + 1); // key not found
   }

   private boolean comparableIndexIsCorrect(KeyValuePair<K, V> e, int index) {

      Comparable<? super K> key = (Comparable<? super K>) e.getKey();

      if (index == 0) {
         if (this.size() == 0) {
            return true;
         }
         // try to insert at first position - must be lesser than first
         return key.compareTo(this.entries.get(0).getKey()) < 0;
      } else if (index == this.size()) {
         // try to insert at last position - must be greater than last
         return key.compareTo(this.entries.get(this.size() - 1).getKey()) > 0;
      } else {
         // try to insert at a middle position - must be greater than current
         // left neighbor of index and lesser than current index
         return !(key.compareTo(this.entries.get(index - 1).getKey()) <= 0
               || key.compareTo(this.entries.get(index).getKey()) >= 0);
      }
   }

   private boolean comparatorIndexIsCorrect(KeyValuePair<K, V> e, int index) {

      K key = e.getKey();

      if (index == 0) {
         if (this.size() == 0) {
            return true;
         }
         // try to insert at first position - must be lesser than first
         return this.comp.compare(key, this.entries.get(0).getKey()) < 0;
      } else if (index == this.size()) {
         // try to insert at last position - must be greater than last
         return this.comp.compare(key, this.entries.get(this.size() - 1)
               .getKey()) > 0;
      } else {
         // try to insert at a middle position - must be greater than current
         // left neighbor of index and lesser than current index
         return !(this.comp.compare(key, this.entries.get(index - 1).getKey()) <= 0
               || this.comp.compare(key, this.entries.get(index).getKey()) >= 0);
      }
   }

   void addFirst(KeyValuePair<K, V> e) {
      if (indexIsCorrect(e, 0)) {
         this.entries.add(0, e);
      } else {
         throw new IllegalArgumentException(
               "e is not smaller than the first entry in this node");
      }
   }

   void addLast(KeyValuePair<K, V> e) {
      if (indexIsCorrect(e, this.size())) {
         this.entries.add(e);
      } else {
         throw new IllegalArgumentException(
               "e is not greater than the last entry in this node");
      }
   }

   boolean contains(K key) {
      return this.getIndex(key) >= 0;
   }


   KeyValuePair<K, V> get(K key) {
      int index = this.getIndex(key);
      if (index >= 0) {
         return this.get(index);
      } else {
         return null;
      }
   }

   public KeyValuePair<K, V> get(int index) {
      return this.entries.get(index);
   }

   V getChild(int index) {
      return this.entries.get(index).getValue();
   }

   K getKey(int index) {
      return this.entries.get(index).getKey();
   }

   KeyValuePair<K, V> getFirst() {
      return this.entries.get(0);
   }

   /**
    * Determines the position of e in this node or the inverted position where
    * it should be inserted: -('index of first element greater than the key' -
    * 1).
    *
    * @param key
    * @return
    */
   int getIndex(K key) {
      return this.binarySearch(key);
   }

   KeyValuePair<K, V> getLast() {
      return this.entries.get(this.entries.size() - 1);
   }

   boolean indexIsCorrect(KeyValuePair<K, V> e, int index) {

      if (index < 0 || index > this.size()) {
         throw new NoSuchElementException();
      }
      if (this.comp != null) {
         return comparatorIndexIsCorrect(e, index);
      } else {
         return comparableIndexIsCorrect(e, index);
      }
   }

   boolean insert(KeyValuePair<K, V> e) {
      int index = this.getIndex(e.getKey());
      if (index >= 0) {
         // key already exists
         return false;
      } else {
         this.entries.add(-(index + 1), e);
         return true;
      }
   }

   boolean insert(KeyValuePair<K, V> e, int index) {

      if (indexIsCorrect(e, index)) {
         this.entries.add(index, e);
         return true;
      } else {
         return false;
      }
   }

   abstract boolean isLeaf();

   KeyValuePair<K, V> remove(int index) {
      return this.entries.remove(index);
   }

   boolean remove(K key) {
      int index = this.getIndex(key);
      if (index < 0) {
         // key does not exist
         return false;
      } else {
         this.entries.remove(index);
         return true;
      }
   }

   KeyValuePair<K, V> removeFirst() {
      return this.entries.remove(0);
   }

   KeyValuePair<K, V> removeLast() {
      if (this.entries.isEmpty()) {
         throw new NoSuchElementException();
      } else {
         return this.entries.remove(this.entries.size() - 1);
      }
   }

   void replace(KeyValuePair<K, V> kv, int index) {
      if (proofReplacement(kv.getKey(), index)) {
         this.entries.set(index, kv);
      } else {
         throw new IllegalArgumentException("cannot put " + kv.getKey()
               + " at pos " + index + " in " + this.toString());
      }
   }

   private boolean proofReplacement(K key, int index) {

      K left = (index <= 0) ? null : this.get(index - 1).getKey();
      K right = (index >= (this.size() - 1)) ? null : this.get(index + 1)
            .getKey();

      int lftcmp;
      int rightcmp;

      if (this.comp == null) {
         Comparable<? super K> comparable = (Comparable<? super K>) key;
         lftcmp = (left == null) ? 1 : comparable.compareTo(left);
         rightcmp = (right == null) ? -1 : comparable.compareTo(right);
      } else {
         lftcmp = (left == null) ? 1 : this.comp.compare(key, left);
         rightcmp = (right == null) ? -1 : this.comp.compare(key, right);
      }
      return lftcmp > 0 && rightcmp < 0;

   }

   @Override
   public String toString() {
      StringBuilder buf = new StringBuilder();

      for (KeyValuePair<K, V> kv : this.entries) {
         buf.append(kv.getKey());
         buf.append(",");
      }

      return buf.toString();
   }

   public int size() {
      return this.entries.size();
   }
}
