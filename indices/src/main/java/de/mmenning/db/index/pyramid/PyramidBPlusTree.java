package de.mmenning.db.index.pyramid;

import de.mmenning.db.index.bplustree.BPlusTree;
import de.mmenning.db.index.bplustree.DirNode;
import de.mmenning.db.index.bplustree.KeyValuePair;
import de.mmenning.db.index.bplustree.LeafNode;
import de.mmenning.db.storage.IOUtils;
import de.mmenning.db.storage.SimpleStorable;
import de.mmenning.db.storage.Storable;
import de.mmenning.db.storage.StorageManager;

import java.util.Iterator;
import java.util.LinkedList;

public class PyramidBPlusTree<E> extends BPlusTree<PyramidValue, E> {

   protected final int dim;
   protected final int blockSize;

   protected final int elementByteSize;

   public PyramidBPlusTree(
         Iterator<KeyValuePair<PyramidValue, LinkedList<E>>> iter,
         int blockSize, int dim, StorageManager s, int elementSize) {
      this(blockSize, dim, s, elementSize);
      super.bulkLoad(iter);
   }

   public PyramidBPlusTree(PyramidBPlusTree<E> toBulkLoad) {
      super(toBulkLoad.iterator(), toBulkLoad.nodeCapacity / 2,
            toBulkLoad.leafCapacity / 2, null, toBulkLoad
                  .getStorageManager());
      this.elementByteSize = toBulkLoad.elementByteSize;
      this.dim = toBulkLoad.dim;
      this.blockSize = toBulkLoad.blockSize;
   }

   public PyramidBPlusTree(int dirNode_D, int leafNode_D, int blockSize,
                           int dim, StorageManager s, int elementByteSize) {

      super(dirNode_D, leafNode_D, null, s);

      this.elementByteSize = elementByteSize;
      this.dim = dim;
      this.blockSize = blockSize;
   }


   public PyramidBPlusTree(int blockSize, int dim, StorageManager s,
                           int elementByteSize) {
      super(calcDirNodeSize(blockSize) / 2, calcLeafNodeSize(blockSize, dim,
            elementByteSize) / 2, s);
      this.elementByteSize = elementByteSize;
      this.dim = dim;
      this.blockSize = blockSize;
   }

   private static int calcDirNodeSize(int blockSize) {

      return blockSize / (IOUtils.referenceByteSize() + 8);
   }

   private static int calcLeafNodeSize(int blockSize, int dim,
                                       int elementByteSize) {
      /*
		 * size of a leaf node: entries * (pyramidvalue(8byte) + (element in
		 * every dimension(elementybteSize)) + reference on object (8byte) +
		 * reference on right neighbor (8byte))
		 */

      return blockSize
            / ((IOUtils.referenceByteSize() * 2) + elementByteSize + 8);
   }

   @Override
   protected Storable leafNodeToStorable(LeafNode<PyramidValue, E> n) {

      int byteSize = 0;

		/*
		 * calculate the byte size of a leaf node
		 */
		/*
		 * every leaf node has a reference on the neighor node
		 */
      byteSize += IOUtils.referenceByteSize();

		/*
		 * every entry may vary in its byte size
		 */
      for (int i = 0; i < n.size(); i++) {

			/*
			 * every entry has a pyramid value
			 */
         byteSize += 8;

			/*
			 * every entry has a reference to the stored object or a linked list
			 * of stored objects with the same key
			 */
         byteSize += IOUtils.referenceByteSize();

         final int entrySize = n.get(i).getValue().size();

         if (entrySize < 1) {
            throw new IllegalStateException("entry size is 0.");
         }

         if (entrySize == 1) {
				/*
				 * if entry only contains one element: it contains the key
				 * element -> the eleme
				 */
            byteSize += this.elementByteSize;

         } else {

				/*
				 * if it is linked list of elements calculate the space that is
				 * available in a block of that list
				 */
            double listblockspace = this.blockSize
                  - IOUtils.referenceByteSize();
				/*
				 * calculate how many blocks will be needed for all elements
				 */
				/*
				 * number of entries * (key element -> (e.g.)NDPoint + reference to
				 * object)
				 */
            double spaceNeeded = entrySize
                  * (this.elementByteSize + IOUtils.referenceByteSize());

            double blocks = (spaceNeeded / listblockspace);

            if ((blocks - (int) blocks) > 0) {
               blocks += 1;
            }

				/*
				 * add size of calulated blocks
				 */
            byteSize += ((int) blocks) * this.blockSize;
         }
      }

      return new SimpleStorable(n.getObjectReference(), n, byteSize);
   }

   @Override
   protected Storable dirNodeToStorable(DirNode<PyramidValue, E> n) {
		/*
		 * dir node consists of entries * (pyramid value (8byte) + reference to
		 * subNode (8byte))
		 */
      int byteSize = n.size() * (IOUtils.referenceByteSize() + 8);

      return new SimpleStorable(n.getObjectReference(), n, byteSize);
   }

   @Override
   protected DirNode<PyramidValue, E> getDirNodeFromStorable(Storable st) {

      return (DirNode<PyramidValue, E>) st.getObject();
   }

   @Override
   protected LeafNode<PyramidValue, E> getLeafNodeFromStorable(Storable st) {
      return (LeafNode<PyramidValue, E>) st.getObject();
   }
}
