package de.mmenning.db.index;

public class STFunctions {

   public static final Double CURRENT = Double.NaN;

   /**
    * Does one contain another in a spatio-temporal manner?
    */
   public static boolean contains(final NDRectangle one,
                                  final NDRectangle another, final NowGen now) {

      double oneBegin, oneEnd, anotherBegin, anotherEnd;

      for (int dim = 0; dim < one.getDim(); dim++) {

         oneBegin = one.getBegin().getValue(dim);
         oneEnd = getCurrentValue(one.getEnd().getValue(dim), now);
         anotherBegin = another.getBegin().getValue(dim);
         anotherEnd = getCurrentValue(another.getEnd().getValue(dim), now);

         if (oneBegin > anotherBegin || oneEnd < anotherEnd) {
            return false;
         }
      }
      return true;
   }

   public static double getCurrentValue(double val, NowGen now) {
      if (Double.compare(val, CURRENT) == 0) {
         return now.getNow();
      } else {
         return val;
      }
   }

   /**
    * Does one intersect another in a spatio-temporal manner?
    */
   public static boolean intersects(final NDRectangle one,
                                    final NDRectangle another, final NowGen now) {

      double oneBegin, oneEnd, anotherBegin, anotherEnd;

      for (int dim = 0; dim < one.getDim(); dim++) {

         oneBegin = one.getBegin().getValue(dim);
         oneEnd = getCurrentValue(one.getEnd().getValue(dim), now);
         anotherBegin = another.getBegin().getValue(dim);
         anotherEnd = getCurrentValue(another.getEnd().getValue(dim), now);

         if (oneBegin > anotherEnd || oneEnd < anotherBegin) {
            return false;
         }
      }
      return true;

   }

   public static boolean isCurrent(double d) {
      return Double.compare(CURRENT, d) == 0;
   }

}
