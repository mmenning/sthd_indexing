package de.mmenning.db.index.rtptree;

import de.mmenning.db.index.*;
import de.mmenning.db.index.bplustree.KeyValuePair;
import de.mmenning.db.index.bplustree.Query;
import de.mmenning.db.index.pyramid.ExtendedPyramidFunctions;
import de.mmenning.db.index.pyramid.PyramidBPlusTree;
import de.mmenning.db.index.pyramid.PyramidFunctions;
import de.mmenning.db.index.pyramid.PyramidValue;
import de.mmenning.db.storage.StorageManager;

import java.util.*;

/*
 * TODO Median will not dynamically update
 */
public class RTPTree implements NDRectangleKeyIndex {

   private double[] median;

   protected final int dim;

   protected final int blockSize;

   protected PyramidBPlusTree<NDRectangleKey> btree;

   protected final NowGen now;

   private int mod;

   public RTPTree(final int dim, final int nodeSize_D, final int blockSize,
                  final StorageManager s, NowGen now) {
      this.dim = dim;
      this.blockSize = blockSize;
      this.btree = new PyramidBPlusTree<NDRectangleKey>(nodeSize_D,
            nodeSize_D, blockSize, this.dim * 2, s, this.dim * 16);
      this.median = new double[this.dim * 2];
      Arrays.fill(this.median, 0.5);
      this.now = now;
      this.mod = 0;
   }

   public RTPTree(final int dim, final int blockSize, final StorageManager s,
                  NowGen now) {
      this.dim = dim;
      this.blockSize = blockSize;
      this.btree = new PyramidBPlusTree<NDRectangleKey>(blockSize,
            this.dim * 2, s, this.dim * 16);
      this.median = new double[this.dim * 2];
      Arrays.fill(this.median, 0.5);
      this.now = now;
      this.mod = 0;
   }

   protected double[] convert(NDRectangle rectangle) {
      double[] point = new double[this.dim * 2];
      for (int dim = 0; dim < rectangle.getDim(); dim++) {

         // point[dim] = rectangle.getBegin().getValue(dim);
         // if (Double.compare(rectangle.getEnd().getValue(dim),
         // STFunctions.CURRENT) == 0) {
         // point[dim + rectangle.getDim()] = this.median[dim + this.dim];
         // } else {
         // point[dim + rectangle.getDim()] = Math.abs(rectangle.getEnd()
         // .getValue(dim) - rectangle.getBegin().getValue(dim));
         // }

         point[dim] = rectangle.getBegin().getValue(dim);
         if (Double.compare(rectangle.getEnd().getValue(dim),
               STFunctions.CURRENT) == 0) {
            point[dim + rectangle.getDim()] = this.median[dim + this.dim];
         } else {
            point[dim + rectangle.getDim()] = rectangle.getEnd().getValue(
                  dim);
         }
      }
      return point;
   }

   @Override
   public boolean contains(NDRectangleKey key) {
      double[] point = convert(key.getNDKey());
      point = ExtendedPyramidFunctions.scaleTi(point, this.median);
      PyramidValue p = PyramidFunctions.convertToPyramidValue(point);
      return this.btree.contains(p, key);
   }

   @Override
   public boolean delete(NDRectangleKey key) {
      double[] point = convert(key.getNDKey());
      point = ExtendedPyramidFunctions.scaleTi(point, this.median);
      PyramidValue p = PyramidFunctions.convertToPyramidValue(point);
      boolean delete = this.btree.remove(p, key);
      return delete;
   }

   @Override
   public void getContained(final NDRectangle region, final RectangleQuery q) {

      double[] minBegin = new double[this.dim];
      double[] minEnd = new double[this.dim];

      double[] maxBegin = new double[this.dim];
      double[] maxEnd = new double[this.dim];

      for (int dim = 0; dim < this.dim - 2; dim++) {

         maxBegin[dim] = minBegin[dim] = region.getBegin().getValue(dim);
         maxEnd[dim] = minEnd[dim] = region.getEnd().getValue(dim);
      }

      for (int dim = this.dim - 2; dim < this.dim; dim++) {

         minBegin[dim] = region.getBegin().getValue(dim);
         minEnd[dim] = region.getEnd().getValue(dim);
         if (STFunctions.isCurrent(minEnd[dim])) {
            minEnd[dim] = Math.max(this.median[dim + this.dim],
                  now.getNow());
         }

         maxBegin[dim] = region.getBegin().getValue(dim);
         maxEnd[dim] = region.getEnd().getValue(dim);
         if (STFunctions.isCurrent(maxEnd[dim])) {
            maxEnd[dim] = Math.max(median[dim + this.dim], now.getNow());
         } else if (maxBegin[dim] <= now.getNow()
               && maxEnd[dim] >= now.getNow()) {

            maxBegin[dim] = Math.min(maxBegin[dim], median[dim + this.dim]);
         }
      }

      final NDRectangle minRegion = new NDRectangle(new NDPoint(minBegin),
            new NDPoint(minEnd));
      final NDRectangle maxRegion = new NDRectangle(new NDPoint(maxBegin),
            new NDPoint(maxEnd));

      doDoubleQuery(minRegion, maxRegion,
            new Query<PyramidValue, NDRectangleKey>() {
               @Override
               public boolean query(PyramidValue key,
                                    LinkedList<NDRectangleKey> values) {
                  for (NDRectangleKey value : values) {
                     if (STFunctions.contains(region, value.getNDKey(),
                           now)) {
                        if (!q.query(value)) {
                           return false;
                        }
                     }
                  }
                  return true;
               }
            });
   }

   @Override
   public void getIntersected(final NDRectangle region, final RectangleQuery q) {

      double[] minBegin = new double[this.dim];
      double[] minEnd = new double[this.dim];

      double[] maxBegin = new double[this.dim];
      double[] maxEnd = new double[this.dim];

      for (int dim = 0; dim < this.dim - 2; dim++) {

         minBegin[dim] = 0;
         minEnd[dim] = region.getEnd().getValue(dim);
         maxBegin[dim] = region.getBegin().getValue(dim);
         maxEnd[dim] = 1;

      }

      for (int dim = this.dim - 2; dim < this.dim; dim++) {

         minBegin[dim] = 0;
         minEnd[dim] = region.getEnd().getValue(dim);
         maxBegin[dim] = region.getBegin().getValue(dim);
         maxEnd[dim] = 1;

         if (STFunctions.isCurrent(minEnd[dim])) {
            minEnd[dim] = Math.max(this.median[dim + this.dim],
                  now.getNow());
            maxBegin[dim] = Math.min(
                  Math.min(now.getNow(), this.median[dim + this.dim]),
                  maxBegin[dim]);
         } else {
            minEnd[dim] = Math
                  .max(this.median[dim + this.dim], minEnd[dim]);
            maxBegin[dim] = Math.min(this.median[dim + this.dim],
                  maxBegin[dim]);
         }

      }
      final NDRectangle minRegion = new NDRectangle(new NDPoint(minBegin),
            new NDPoint(minEnd));
      final NDRectangle maxRegion = new NDRectangle(new NDPoint(maxBegin),
            new NDPoint(maxEnd));

      doDoubleQuery(minRegion, maxRegion,
            new Query<PyramidValue, NDRectangleKey>() {
               @Override
               public boolean query(PyramidValue key,
                                    LinkedList<NDRectangleKey> values) {
                  for (NDRectangleKey value : values) {
                     if (STFunctions.intersects(region,
                           value.getNDKey(), now)) {
                        if (!q.query(value)) {
                           return false;
                        }
                     }
                  }
                  return true;
               }
            });
   }

   private void doDoubleQuery(final NDRectangle minQuery,
                              final NDRectangle maxQuery,
                              final Query<PyramidValue, NDRectangleKey> q) {

      double[] qmin = new double[this.dim * 2];
      double[] qmax = new double[this.dim * 2];

      for (int i = 0; i < minQuery.getDim(); i++) {
         qmin[i] = minQuery.getBegin().getValue(i);
         qmax[i] = minQuery.getEnd().getValue(i);
         qmin[i + minQuery.getDim()] = maxQuery.getBegin().getValue(i);
         qmax[i + minQuery.getDim()] = maxQuery.getEnd().getValue(i);
      }

      qmin = ExtendedPyramidFunctions.scaleTi(qmin, this.median);
      qmax = ExtendedPyramidFunctions.scaleTi(qmax, this.median);

      performDoubleQuery(qmin, qmax, q);
   }

   private void performDoubleQuery(final double[] qmin, final double[] qmax,
                                   final Query<PyramidValue, NDRectangleKey> q) {

      double[] qmincaret = PyramidFunctions.convertToQCaret(qmin);
      double[] qmaxcaret = PyramidFunctions.convertToQCaret(qmax);

      for (int i = 0; i < 2 * this.dim * 2; i++) {
         if (PyramidFunctions.intersectsPyramid(i, qmincaret, qmaxcaret)) {

            double[] hrange = PyramidFunctions.getHQueryInterval(i, qmin,
                  qmax);
            this.btree.rangeQuery(new PyramidValue(i,
                  hrange[PyramidFunctions.HLOW]), new PyramidValue(i,
                  hrange[PyramidFunctions.HHIGH]), q);
         }
      }
   }

   @Override
   public boolean insert(NDRectangleKey key) {
      double[] point = convert(key.getNDKey());
      point = ExtendedPyramidFunctions.scaleTi(point, this.median);
      PyramidValue p = PyramidFunctions.convertToPyramidValue(point);
      boolean insert = this.btree.insert(p, key);
      return insert;
   }

   @Override
   public boolean update(NDRectangleKey oldOne, NDRectangleKey newOne) {
      if (!this.delete(oldOne)) {
         return false;
      } else {
         if (!this.insert(newOne)) {
            throw new IllegalStateException();
         } else {
            return true;
         }
      }
   }

   public void setMedian(double[] medianMin, double[] medianMax) {
      double[] median = new double[this.dim * 2];
      System.arraycopy(medianMin, 0, median, 0, medianMin.length);
      System.arraycopy(medianMax, 0, median, medianMin.length,
            medianMax.length);

      this.median = median;
   }

   public Set<NDRectangleKey> getAll() {
      Set<NDRectangleKey> all = new HashSet<NDRectangleKey>();

      for (KeyValuePair<PyramidValue, LinkedList<NDRectangleKey>> k : btree) {
         if (!all.addAll(k.getValue())) {
            throw new IllegalStateException("Duplicate element");
         }
      }
      return all;

   }

   /**
    * Bulk load internal B+-tree.
    */
   public void optimize() {

      this.btree = new PyramidBPlusTree<NDRectangleKey>(
            (PyramidBPlusTree<NDRectangleKey>) this.btree);

   }

   @Override
   public StorageManager getStorageManager() {
      return this.btree.getStorageManager();
   }

   @Override
   public int getDim() {
      return this.dim;
   }

   @Override
   public int size() {
      return this.btree.size();
   }

   public List<PyramidValue> getAllPyramids() {
      LinkedList<PyramidValue> l = new LinkedList<>();

      for (KeyValuePair<PyramidValue, LinkedList<NDRectangleKey>> kv : this.btree) {
         l.add(kv.getKey());
      }

      return l;

   }
}
