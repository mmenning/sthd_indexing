package de.mmenning.util.math;

public class TrimmedSkew extends SkewNormal {

   private static final long serialVersionUID = -3948784137821675480L;

   private final double left;
   private final double right;

   public TrimmedSkew(final double stdDev, final double mean, final double skew,
                      final double left, final double right, long seed) {
      super(seed, mean, stdDev, skew);
      this.left = left;
      this.right = right;
   }

   public double getLeft() {
      return this.left;
   }

   @Override
   public double getNext() {
      double next = super.getNext();
      while (next < this.left || next > this.right) {
         next = super.getNext();
      }
      return next;
   }

   public double getRight() {
      return this.right;
   }

}
