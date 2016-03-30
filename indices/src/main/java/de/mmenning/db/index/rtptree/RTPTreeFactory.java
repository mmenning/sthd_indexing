package de.mmenning.db.index.rtptree;

import de.mmenning.db.index.NDRectangleKeyIndex;
import de.mmenning.db.index.NowGen;
import de.mmenning.db.index.SpatioTemporalIndexFactory;
import de.mmenning.db.storage.StorageManager;

/**
 * @author Mathias Menninghaus (mathias.mennighaus@uos.de)
 */
public class RTPTreeFactory implements SpatioTemporalIndexFactory {


   private final double spatialMinMedian;
   private final double spatialMaxMedian;

   private final double vtMinMedian;
   private final double vtMaxMedian;

   private final double ttMinMedian;
   private final double ttMaxMedian;

   private static final int ON_DISK = -1;

   private final int nodeSize;

   public RTPTreeFactory(double spatialMinMedian, double spatialMaxMedian, double vtMinMedian, double vtMaxMedian, double ttMinMedian, double ttMaxMedian) {
      this(spatialMinMedian, spatialMaxMedian, vtMinMedian, vtMaxMedian, ttMinMedian,
            ttMaxMedian, ON_DISK);
   }

   public RTPTreeFactory(double spatialMinMedian, double spatialMaxMedian, double vtMinMedian, double vtMaxMedian, double ttMinMedian, double ttMaxMedian, int nodeSize) {
      this.spatialMinMedian = spatialMinMedian;
      this.spatialMaxMedian = spatialMaxMedian;
      this.vtMinMedian = vtMinMedian;
      this.vtMaxMedian = vtMaxMedian;
      this.ttMinMedian = ttMinMedian;
      this.ttMaxMedian = ttMaxMedian;
      this.nodeSize = nodeSize;
   }

   @Override
   public NDRectangleKeyIndex createIndex(int dims, int blockSize, StorageManager s, NowGen now) {

      RTPTree tree;

      if (nodeSize == ON_DISK) {
         tree = new RTPTree(dims, blockSize, s, now);
      } else {
         tree = new RTPTree(dims, nodeSize, blockSize, s, now);
      }

      double[] medianMin = new double[dims];
      double[] medianMax = new double[dims];

      for (int dim = 0; dim < dims - 2; dim++) {
         medianMin[0] = spatialMinMedian;
         medianMax[0] = spatialMaxMedian;
      }

      medianMin[dims - 2] = ttMinMedian;
      medianMax[dims - 2] = ttMaxMedian;

      medianMin[dims - 1] = vtMinMedian;
      medianMax[dims - 1] = vtMaxMedian;

      tree.setMedian(medianMin, medianMax);
      return tree;

   }
}
