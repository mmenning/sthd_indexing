package de.mmenning.util.math;

/**
 * @author Mathias Menninghaus (mathias.mennighaus@uos.de)
 */
public class Combined extends RandomGenerator {

   private final RandomGenerator[] generators;

   public Combined(RandomGenerator... generators) {
      if (generators.length == 0) {
         throw new IllegalArgumentException("must at least contain one " +
               "concrete generator");
      }
      this.generators = generators;
   }


   @Override
   public double getNext() {
      double result = generators[0].getNext();

      for (int i = 1; i < generators.length; i++) {
         result += generators[i].getNext();
      }
      return result;
   }
}
