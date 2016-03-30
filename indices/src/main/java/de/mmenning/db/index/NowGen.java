package de.mmenning.db.index;

import java.io.Serializable;
import java.util.Observable;

/**
 * Representation of the ongoing time, i.e. now. The time value {@link
 * #getNow} must be explicitly increased {@link #incNow} by a discrete value,
 * which may be set with initialisation (<code>timeUnit</code>). If one
 * timestep is not set, {@link #incNow} always increases the now-value by one.
 */
public class NowGen implements Serializable {

   private static final long serialVersionUID = 8922851008496222809L;

   private double time;
   private double timeUnit;

   public NowGen(double startTime, double timeUnit) {
      this.time = startTime;
      this.timeUnit = timeUnit;
      Double d;
   }

   public NowGen(double startTime) {
      this(startTime, 1.0);
   }

   public void incNow() {
      this.time += this.timeUnit;
   }

   public double getNow() {
      return this.time;
   }
}
