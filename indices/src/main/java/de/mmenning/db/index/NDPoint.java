package de.mmenning.db.index;

import java.io.Serializable;
import java.util.Arrays;
import java.util.Comparator;

public class NDPoint implements Serializable {

   public static Comparator<NDPoint> createComparator(final int dim) {
      return new Comparator<NDPoint>() {
         @Override
         public int compare(final NDPoint o1, final NDPoint o2) {
            return o1.compareTo(dim, o2);
         }
      };
   }

   private final double[] values;

   public NDPoint(final double value, int size) {
      this.values = new double[size];
      Arrays.fill(this.values, value);
   }

   public NDPoint(final double... values) {
      this.values = Arrays.copyOf(values, values.length);
   }

   public NDPoint(final NDPoint p) {
      this.values = p.values;
   }

   public int compareTo(final int dim, final NDPoint another) {
      return Double.compare(this.values[dim], another.values[dim]);
   }

   public boolean equals(final int dim, final NDPoint another) {
      return this.values[dim] == another.values[dim];
   }

   public int getDim() {
      return this.values.length;
   }

   public double getValue(final int dim) {
      return this.values[dim];
   }

   public boolean greater(final int dim, final NDPoint another) {
      return this.values[dim] > another.values[dim];
   }

   public boolean greaterEquals(final int dim, final NDPoint another) {
      return this.values[dim] >= another.values[dim];
   }

   public boolean lesser(final int dim, final NDPoint another) {
      return this.values[dim] < another.values[dim];
   }

   public boolean lesserEquals(final int dim, final NDPoint another) {
      return this.values[dim] <= another.values[dim];
   }

   public boolean notEquals(final int dim, final NDPoint another) {
      return Double.compare(this.values[dim], another.values[dim]) != 0;
   }

   public double[] toArray() {
      return Arrays.copyOf(this.values, this.values.length);
   }

   @Override
   public String toString() {
      final StringBuffer buf = new StringBuffer();
      buf.append("{");
      for (final double d : this.values) {
         buf.append(" " + d + ",");
      }
      buf.deleteCharAt(buf.length() - 1);
      buf.append("}");
      return buf.toString();
   }

   @Override
   public int hashCode() {
      final int prime = 31;
      int result = 1;
      result = prime * result + Arrays.hashCode(values);
      return result;
   }

   @Override
   public boolean equals(Object obj) {
      if (this == obj)
         return true;
      if (obj == null)
         return false;
      if (getClass() != obj.getClass())
         return false;
      NDPoint other = (NDPoint) obj;
      if (!Arrays.equals(values, other.values))
         return false;
      return true;
   }


}
