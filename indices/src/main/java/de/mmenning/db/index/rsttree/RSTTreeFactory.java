package de.mmenning.db.index.rsttree;

import de.mmenning.db.index.SpatioTemporalIndexFactory;
import de.mmenning.db.index.NDRectangleKey;
import de.mmenning.db.index.NDRectangleKeyIndex;
import de.mmenning.db.index.NowGen;
import de.mmenning.db.storage.StorageManager;

public class RSTTreeFactory implements SpatioTemporalIndexFactory {

   private final double alpha;
   private final double alphaW;
   private final int maxK;

   private static final int ON_DISK = -1;

   public RSTTreeFactory(double alpha, double alphaW) {
      this(alpha, alphaW, ON_DISK);
   }

   public RSTTreeFactory(double alpha, double alphaW, int maxK) {
      this.alpha = alpha;
      this.alphaW = alphaW;
      this.maxK = maxK;
   }

   @Override
   public NDRectangleKeyIndex createIndex(int dims, int blockSize, StorageManager s, NowGen now) {
      if (maxK == ON_DISK) {
         return createRSTTreeWithTimeHorizon(blockSize, dims - 2, alpha, alphaW,
               now, s);
      } else {
         return createRSTTreeTimeH(maxK, dims - 2, alpha, alphaW, now, s);
      }
   }

   public static RSTTree createRSTTreeWithTimeHorizon(final int blockSize,
                                                      final int spatialDims, final double alpha, final double alphaW,
                                                      final NowGen now, final StorageManager s) {

      final OperationTracker ot = new OperationTracker();

      final RSTTree tree = new RSTTree(blockSize, spatialDims, s) {

         @Override
         public synchronized boolean insert(NDRectangleKey b) {
            ot.inserted();
            return super.insert(b);
         }
      };

      final TimeHorizonCalc timeH = new TimeHorizonCalc(tree, ot, now,
            tree.getMaxCapacity(), alphaW);

      final STConstants stConstants = new STConstants() {

         @Override
         public double getNowValue() {
            return now.getNow();
         }

         @Override
         public double getPValue() {
            return timeH.getTimeHorizon();
         }

         @Override
         public double getAlphaValue() {
            return alpha;
         }
      };

      tree.setSTConstants(stConstants);

      return tree;

   }

   public static RSTTree createRSTTreeWithConstantP(final int blockSize,
                                                    final int spatialDims, final double alpha, final double pValue,
                                                    final NowGen now, final StorageManager s) {

      STConstants constants = new STConstants() {

         @Override
         public double getNowValue() {
            return now.getNow();
         }

         @Override
         public double getPValue() {
            return pValue;
         }

         @Override
         public double getAlphaValue() {
            return alpha;
         }
      };

      RSTTree rsttree = new RSTTree(blockSize, spatialDims, s);

      rsttree.setSTConstants(constants);
      return rsttree;

   }

   public static RSTTree createRSTTree(final int maxCapacity,
                                       final int spatialDims, final double alpha, final double pValue,
                                       final NowGen now) {

      STConstants constants = new STConstants() {
         @Override
         public double getNowValue() {
            return now.getNow();
         }

         @Override
         public double getPValue() {
            return pValue;
         }

         @Override
         public double getAlphaValue() {
            return alpha;
         }
      };
      return new RSTTree(maxCapacity, 0.5, spatialDims, constants);
   }

   public static RSTTree createRSTTreeConstP(final int maxCapacity,
                                             final int spatialDims, final double alpha, final double pValue,
                                             final NowGen now, StorageManager s) {

      STConstants constants = new STConstants() {
         @Override
         public double getNowValue() {
            return now.getNow();
         }

         @Override
         public double getPValue() {
            return pValue;
         }

         @Override
         public double getAlphaValue() {
            return alpha;
         }
      };

      return new RSTTree(maxCapacity, 0.5, spatialDims, constants, s);
   }

   public static RSTTree createRSTTreeTimeH(final int maxCapacity,
                                            final int spatialDims, final double alpha, final double alphaW,
                                            final NowGen now, StorageManager s) {

      final OperationTracker ot = new OperationTracker();

      final RSTTree tree = new RSTTree(maxCapacity, 0.5, spatialDims, null, s) {

         @Override
         public synchronized boolean insert(NDRectangleKey b) {
            ot.inserted();
            return super.insert(b);
         }
      };

      final TimeHorizonCalc timeH = new TimeHorizonCalc(tree, ot, now,
            tree.getMaxCapacity(), alphaW);

      final STConstants stConstants = new STConstants() {

         @Override
         public double getNowValue() {
            return now.getNow();
         }

         @Override
         public double getPValue() {
            return timeH.getTimeHorizon();
         }

         @Override
         public double getAlphaValue() {
            return alpha;
         }
      };

      tree.setSTConstants(stConstants);

      return tree;
   }

}
