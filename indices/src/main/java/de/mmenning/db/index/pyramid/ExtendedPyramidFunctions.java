package de.mmenning.db.index.pyramid;

import de.mmenning.db.index.NDPoint;
import de.mmenning.util.math.DoubleMath;

public class ExtendedPyramidFunctions {

   public static double[] scaleTi(NDPoint point, double[] median) {
      if (point.getDim() != median.length) {
         throw new IllegalArgumentException(
               "point and median must have the same length");
      }

      double[] converted = new double[point.getDim()];

      for (int i = 0; i < point.getDim(); i++) {
         converted[i] = calcTi(point.getValue(i), median[i]);
      }
      return converted;
   }

   public static double[] scaleTi(double[] point, double[] median) {
      if (point.length != median.length) {
         throw new IllegalArgumentException(
               "point (" + point.length + ") and median (" + median.length + ") must have the same length");
      }

      double[] converted = new double[point.length];
      for (int i = 0; i < point.length; i++) {
         converted[i] = calcTi(point[i], median[i]);
      }
      return converted;
   }

   public static double calcTi(double x, double median) {
      return Math.pow(x, -(1 / DoubleMath.log2(median)));
   }
}
