package de.mmenning.db.index.pyramid;

import de.mmenning.db.index.*;
import de.mmenning.db.index.bplustree.BPlusTree;
import de.mmenning.db.index.bplustree.Query;
import de.mmenning.db.storage.DefaultStorage;
import de.mmenning.db.storage.StorageManager;

import java.util.LinkedList;

public class PyramidTechnique implements NDPointKeyIndex {

   protected final BPlusTree<PyramidValue, NDPointKey> btree;

   protected final int dim;

   public PyramidTechnique(final int dim, final int blockSize) {
      this(dim, blockSize, DefaultStorage.getInstance());
   }

   public PyramidTechnique(final int dim, final int blockSize, StorageManager s) {
      this.dim = dim;
      /*
		 * variable size dependent on block size
		 */
      this.btree = new PyramidBPlusTree<NDPointKey>(blockSize, dim, s,
            dim * 8);
   }

   protected PyramidValue convert(NDPoint point) {
      return PyramidFunctions.convertToPyramidValue(point);
   }

   @Override
   public boolean contains(NDPointKey key) {
      return this.btree.contains(convert(key.getNDKey()), key);
   }

   @Override
   public boolean delete(NDPointKey key) {
      boolean delete = this.btree.remove(convert(key.getNDKey()), key);

      return delete;
   }

   @Override
   public int getDim() {
      return this.dim;
   }

   @Override
   public boolean insert(NDPointKey key) {
      boolean insert = this.btree.insert(convert(key.getNDKey()), key);

      return insert;
   }

   protected final void doQuery(final double[] qmin, final double[] qmax,
                                final NDRectangle region, final PointQuery q) {

      double[] qmincaret = PyramidFunctions.convertToQCaret(qmin);
      double[] qmaxcaret = PyramidFunctions.convertToQCaret(qmax);

      final Query<PyramidValue, NDPointKey> subQuery = new Query<PyramidValue, NDPointKey>() {
         @Override
         public boolean query(PyramidValue key, LinkedList<NDPointKey> values) {
            for (NDPointKey value : values) {
               if (region.contains(value.getNDKey())) {
                  if (!q.query(value)) {
                     return false;
                  }
               }
            }
            return true;
         }
      };

      for (int i = 0; i < 2 * this.dim; i++) {
         if (PyramidFunctions.intersectsPyramid(i, qmincaret, qmaxcaret)) {
            double[] hrange = PyramidFunctions.getHQueryInterval(i, qmin,
                  qmax);
            this.btree.rangeQuery(new PyramidValue(i,
                  hrange[PyramidFunctions.HLOW]), new PyramidValue(i,
                  hrange[PyramidFunctions.HHIGH]), subQuery);
         }
      }
   }

   @Override
   public void regionQuery(final NDRectangle region, final PointQuery q) {

      double[] qmin = region.getBegin().toArray();
      double[] qmax = region.getEnd().toArray();

      doQuery(qmin, qmax, region, q);

   }

   @Override
   public int size() {
      return this.btree.size();
   }

   @Override
   public boolean update(NDPointKey oldOne, NDPointKey newOne) {
      if (!this.delete(oldOne)) {
         return false;
      } else {
         if (!this.insert(newOne)) {
            throw new IllegalStateException("deleted but not inserted");
         } else {
            return true;
         }
      }
   }

   @Override
   public StorageManager getStorageManager() {
      return this.btree.getStorageManager();
   }
}
