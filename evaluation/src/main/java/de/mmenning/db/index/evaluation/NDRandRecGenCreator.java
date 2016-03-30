package de.mmenning.db.index.evaluation;

import de.mmenning.db.index.NowGen;
import de.mmenning.db.index.evaluation.xml.setup.Cluster;
import de.mmenning.db.index.evaluation.xml.setup.Distribution;
import de.mmenning.db.index.evaluation.xml.setup.Skewed;
import de.mmenning.db.index.generate.NDRandomRectangleGenerator;
import de.mmenning.util.math.*;

import java.util.Random;

public class NDRandRecGenCreator {

   public static enum BorderType {
      CLOSED, HALF_OPEN;
   }

   public static NDRandomRectangleGenerator createGen(int dim,
                                                      Distribution dist, BorderType borderType, Random rand,
                                                      double currentLikelihood, double elementSize, NowGen now) {

      RandomInterval[] intervals = new RandomInterval[dim];

      switch (borderType) {
         case CLOSED:
            for (int i = 0; i < intervals.length; i++) {
               intervals[i] = createRandomInterval(dist, borderType,
                     elementSize, rand, currentLikelihood, now);
            }
            break;
         case HALF_OPEN:
            for (int i = 0; i < intervals.length - 2; i++) {
               intervals[i] = createRandomInterval(dist, BorderType.CLOSED,
                     elementSize, rand, currentLikelihood, now);
            }
            intervals[intervals.length - 2] = createRandomInterval(dist,
                  BorderType.HALF_OPEN, elementSize, rand, currentLikelihood,
                  now);
            intervals[intervals.length - 1] = createRandomInterval(dist,
                  BorderType.HALF_OPEN, elementSize, rand, currentLikelihood,
                  now);
            break;
      }

      return new NDRandomRectangleGenerator(intervals);
   }

   public static RandomInterval createUniformClosed(double maxExpand,
                                                    Random rand) {
      return new RandomInterval(new Uniform(maxExpand, 1 - maxExpand * 2,
            rand.nextLong()), new Uniform(0, maxExpand, rand.nextLong()));
   }

   public static RandomInterval createClusteredClosed(Cluster c,
                                                      double maxExpand, Random rand) {
      return new RandomInterval(new TrimmedGaussian(c.getStDev(),
            c.getMean(), maxExpand, 1 - maxExpand, rand.nextLong()),
            new Uniform(0, maxExpand));
   }

   public static RandomInterval createUniformOpen(double maxExpand,
                                                  Random rand, double currentLikelihood, NowGen now) {
      return new STRandomInterval(new Uniform(maxExpand, 1 - maxExpand * 2,
            rand.nextLong()), new Uniform(0, maxExpand, rand.nextLong()),
            currentLikelihood, new Uniform(0, 1, rand.nextLong()), now);
   }

   public static RandomInterval createClusteredOpen(Cluster c,
                                                    double maxExpand, Random rand, double currentLikelihood, NowGen now) {
      return new STRandomInterval(
            new TrimmedGaussian(c.getStDev(), c.getMean(), maxExpand, 1 - maxExpand, rand.nextLong()),
            new Uniform(0, maxExpand),
            currentLikelihood,
            new Uniform(0, 1,rand.nextLong()),
            now);
   }

   public static RandomInterval createSkewedOpen(Skewed s, double maxExpand,
                                                 Random rand, double
                                                       currentlikelihood,
                                                 NowGen now) {
      return new STRandomInterval(
            new TrimmedSkew(s.getStDev(), s.getMean(), s.getSkew(), maxExpand, 1 - maxExpand, rand.nextLong()),
            new Uniform(0, maxExpand),
            currentlikelihood,
            new Uniform(0, 1, rand.nextLong()),
            now);
   }

   public static RandomInterval createSkewedClosed(Skewed s, double
         maxExpand, Random rand) {
      return new RandomInterval(
            new TrimmedSkew(s.getStDev(), s.getMean(), s.getSkew(), maxExpand, 1 - maxExpand, rand.nextLong()),
            new Uniform(0, maxExpand));
   }


   public static RandomInterval createRandomInterval(Distribution dist,
                                                     BorderType borderType, double maxExpand, Random rand,
                                                     double currentLikelihood, NowGen now) {
      switch (borderType) {

         case CLOSED:
            if (dist instanceof de.mmenning.db.index.evaluation.xml.setup.Uniform) {
               return createUniformClosed(maxExpand, rand);
            } else if (dist instanceof Cluster) {
               Cluster c = (Cluster) dist;
               return createClusteredClosed(c, maxExpand, rand);
            } else if (dist instanceof Skewed) {
               Skewed s = (Skewed) dist;
               return createSkewedClosed(s, maxExpand, rand);
            }
            break;
         case HALF_OPEN:
            if (dist instanceof de.mmenning.db.index.evaluation.xml.setup.Uniform) {
               return createUniformOpen(maxExpand, rand, currentLikelihood,
                     now);
            } else if (dist instanceof Cluster) {
               Cluster c = (Cluster) dist;
               return createClusteredOpen(c, maxExpand, rand,
                     currentLikelihood, now);
            } else if (dist instanceof Skewed) {
               Skewed s = (Skewed) dist;
               return createSkewedOpen(s, maxExpand, rand, currentLikelihood,
                     now);
            }
      }
      throw new UnsupportedOperationException("Unknown Distribution");
   }

}
