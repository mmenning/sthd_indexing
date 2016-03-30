package de.mmenning.db.index.evaluation;

import de.mmenning.db.index.NDRectangleKeyIndex;

import java.lang.management.ManagementFactory;
import java.lang.management.ThreadMXBean;

public class UserTimeFunction implements EvaluationGoalFunction<Long> {

   private UserTimeFunction() {
   }

   private static UserTimeFunction instance;

   public static UserTimeFunction getInstance() {
      if (instance == null) {
         instance = new UserTimeFunction();
      }
      return instance;
   }

  
   @Override
   public Long value(NDRectangleKeyIndex index) {
      return System.nanoTime();//getUserTime();
   }

   static {
      bean = ManagementFactory.getThreadMXBean();
   }

   final static ThreadMXBean bean;

   /**
    * Get CPU time in nanoseconds.
    */
   public static long getCpuTime() {
      return bean.isCurrentThreadCpuTimeSupported() ? bean
            .getCurrentThreadCpuTime() : 0L;
   }

   /**
    * Get user time in nanoseconds.
    */
   public static long getUserTime() {
      return bean.isCurrentThreadCpuTimeSupported() ? bean
            .getCurrentThreadUserTime() : 0L;
   }

   /**
    * Get system time in nanoseconds.
    */
   public static long getSystemTime() {
      return bean.isCurrentThreadCpuTimeSupported() ? (bean
            .getCurrentThreadCpuTime() - bean.getCurrentThreadUserTime())
            : 0L;
   }
}
