package de.mmenning.util.math;

/**
 * @author Mathias Menninghaus (mathias.mennighaus@uos.de)
 */
public class SkewNormal extends RandomGenerator {

   public double getMean() {
      return mean;
   }

   public double getSkew() {
      return skew;
   }

   private final double mean;
   private final double skew;
   private final double variance;

   public SkewNormal(long seed, double mean, double stddev, double skew) {
      super(seed);
      this.mean = mean;
      this.skew = skew;
      this.variance = stddev * stddev;
   }

   @Override
   public double getNext() {

      double y = this.getRandom().nextGaussian();
      if (Math.abs(skew) > 0.0) {
         y = (1.0 - Math.exp(-skew * y)) / skew;
      }
      double result = mean + variance * y;
      return result;
   }
}
