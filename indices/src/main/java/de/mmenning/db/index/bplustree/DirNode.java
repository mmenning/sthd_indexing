package de.mmenning.db.index.bplustree;

import java.util.Comparator;

import de.mmenning.db.storage.ObjectReference;

public class DirNode<K, V> extends BNode<K, ObjectReference> {

   DirNode(Comparator<? super K> comp, int size) {
      super(comp, size);
   }

   @Override
   boolean isLeaf() {
      return false;
   }

}
