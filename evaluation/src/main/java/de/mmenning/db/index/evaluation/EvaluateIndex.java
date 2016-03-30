package de.mmenning.db.index.evaluation;

import de.mmenning.db.index.NDRectangleKeyIndex;
import de.mmenning.db.index.NowGen;
import de.mmenning.db.index.SpatioTemporalIndexFactory;
import de.mmenning.db.index.evaluation.NDRandRecGenCreator.BorderType;
import de.mmenning.db.index.evaluation.xml.index.IndexBase;
import de.mmenning.db.index.evaluation.xml.setup.EvaluationSetup;
import de.mmenning.db.index.evaluation.xml.setup.Function;
import de.mmenning.db.index.generate.NDRandomRectangleGenerator;
import de.mmenning.db.storage.BufferedStorage;
import de.mmenning.db.storage.StorageManager;
import de.mmenning.util.math.RandomInterval;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBElement;
import javax.xml.bind.JAXBException;
import java.io.File;
import java.io.IOException;
import java.io.Writer;
import java.lang.reflect.InvocationTargetException;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.util.Random;

public class EvaluateIndex {

   public static final String SEPARATOR = "\t";

   public static EvaluationWriter getDestination(String dest) throws
         IOException {
      File f = new File(dest);
      File parentDir = f.getParentFile();

      if (parentDir != null) {
         parentDir.mkdirs();
      }
      final Writer writer = Files.newBufferedWriter(f.toPath(), Charset.forName
            ("UTF-8"));

      EvaluationWriter evaluationWriter = new EvaluationWriter() {
         @Override
         public void write(int dim, int blockSize, int bufferSize, int test,
                           int indexSize, double buildUp, double
                                 queryContained,
                           double queryIntersect) {
            try {
               writer.write(dim
                     + SEPARATOR + blockSize + SEPARATOR + bufferSize + SEPARATOR + test
                     + SEPARATOR + indexSize + SEPARATOR + buildUp + SEPARATOR
                     + queryContained + SEPARATOR + queryIntersect + "\n");
               writer.flush();
            } catch (IOException e) {
               e.printStackTrace();
            }
         }

         public void writeComment(String comment) {
            try {
               writer.write(comment);
               writer.flush();
            } catch (IOException e) {
               e.printStackTrace();
            }
         }
      };
      return evaluationWriter;
   }

   /**
    * @param args args[0] EvaluationSetup.xml, args[1] Index.xml, args[2]
    *             destination of evaluation data
    */
   public static void main(String[] args) throws IOException {
      new EvaluateIndex().evaluate(args[0], args[1], getDestination(args[2]));
   }

   public interface EvaluationWriter {
      public void write(int dim, int blockSize, int bufferSize, int test,
                        int indexSize, double buildUp, double queryContained,
                        double queryIntersect);

      public void writeComment(String comment);
   }


   public EvaluationGoalFunction<?> getEvaluationGoalFuncion(Function
                                                                   xmlFunction) {
      switch (xmlFunction) {
         case IO:
            return IOAccessFunction.getInstance();
         case USER_TIME:
            return UserTimeFunction.getInstance();
         default:
            throw new UnsupportedOperationException();
      }
   }

   public Random getRandom(long randomSeed) {
      return new Random(randomSeed);

   }

   public void evaluate(EvaluationSetup es, SpatioTemporalIndexFactory
         abstractFac, EvaluationWriter dest) throws IOException {

      EvaluationGoalFunction<?> evalFunc = getEvaluationGoalFuncion(es
            .getEvalFunction());

      final Random rand = getRandom(es.getRandomSeed());

      int maxSize = es.getInitialSize();
      for (Integer size : es.getIncSize()) {
         maxSize += size;
      }

      for (Integer dim : es.getDim()) {
         for (Integer blockSize : es.getBlockSize()) {
            for (Integer bufferSize : es.getBufferSize()) {
               for (int test = 0; test < es.getTests(); test++) {

                  // long start = System.nanoTime();
                  StorageManager storageManager = BufferedStorage
                        .getStorageManager(bufferSize, blockSize);
                  NowGen now = new NowGen(0.25, (double) 0.5
                        / (maxSize + 1));

                  RandomInterval validTime = NDRandRecGenCreator
                        .createRandomInterval(
                              es.getValidTimeDistribution(),
                              BorderType.HALF_OPEN,
                              es.getMaxValidTimeLength(), rand,
                              es.getVtInfinityProbability(), now);

                  RandomInterval transactionTimeQueryInterval = NDRandRecGenCreator
                        .createRandomInterval(
                              es.getQueryDistribution(),
                              BorderType.HALF_OPEN,
                              es.getQuerySize(), rand,
                              0.5/*es.getStartPercentage()*/, now);

                  RandomInterval validTimeQueryInterval = NDRandRecGenCreator
                        .createRandomInterval(
                              es.getValidTimeDistribution(),
                              BorderType.HALF_OPEN,
                              es.getQuerySize(), rand,
                              0.5/*es.getVtInfinityProbability()*/, now);

                  NDRandomRectangleGenerator spatialGen = NDRandRecGenCreator
                        .createGen(dim - 2,
                              es.getSpatialDistribution(),
                              BorderType.CLOSED, rand, -1,
                              es.getMaxSpatialElementSize(), now);

                  NDRandomRectangleGenerator spatialQuery = NDRandRecGenCreator
                        .createGen(dim,
                              es.getSpatialDistribution(),
                              BorderType.CLOSED, rand, -1,
                              es.getQuerySize(), now);

                  RandomInterval[] query = new RandomInterval[dim];

                  System.arraycopy(spatialQuery.getRandomIntervals(),
                        0, query, 0, dim - 2);
                  query[dim - 2] = transactionTimeQueryInterval;
                  query[dim - 1] = validTimeQueryInterval;

                  NDRandomRectangleGenerator queryGen = new NDRandomRectangleGenerator(
                        query);

                  NDRectangleKeyIndex index = setupIndex(abstractFac, dim,
                        blockSize,
                        storageManager, now);

                  IndexEvaluation eval = new IndexEvaluation(
                        evalFunc, es.getInitialSize(),
                        es.getQueries(), index, spatialGen,
                        queryGen, now, rand, validTime,
                        es.getStartPercentage(),
                        es.getEndPercentage(),
                        es.getUpdatePercentage());

                  for (Integer insertedElements : es.getIncSize()) {

                     double[] ergs = eval.evaluate(insertedElements);

                     dest.write(dim, blockSize, bufferSize, test, index.size()
                           ,
                           ergs[IndexEvaluation.BUILD_UP],

                           ergs[IndexEvaluation.QUERY_CONTAINED]
                           ,
                           ergs[IndexEvaluation.QUERY_INTERSECT]
                     );
                  }
               }
            }
         }
      }
   }

   public NDRectangleKeyIndex setupIndex(SpatioTemporalIndexFactory factory,
                                         int dims, int blockSize,
                                         StorageManager storageManager,
                                         NowGen now) {
      return factory.createIndex(dims, blockSize, storageManager, now);
   }


   public void evaluate(String xmlSetup, String xmlIndex,
                        EvaluationWriter writer) {
      try {
         /*
          * parse Evaluation Setup
			 */
         JAXBContext jbc = JAXBContext.newInstance(EvaluationSetup.class
               .getPackage().getName());
         File setupFile = new File(xmlSetup);
         JAXBElement jelem = (JAXBElement) jbc.createUnmarshaller()
               .unmarshal(setupFile);
         EvaluationSetup es = (EvaluationSetup) jelem.getValue();
         /*
          * parse Index-Factory and Index Setup
			 */
         jbc = JAXBContext.newInstance(IndexBase.class.getPackage()
               .getName());
         File indexFile = new File(xmlIndex);
         jelem = (JAXBElement) jbc.createUnmarshaller().unmarshal(indexFile);
         IndexBase ib = (IndexBase) jelem.getValue();

         Class<? extends AbstractXMLIndexFactory> facGen = (Class<? extends AbstractXMLIndexFactory>) EvaluateIndex.class
               .getClassLoader().loadClass(ib.getFactoryGen());
         AbstractXMLIndexFactory abstractFac = facGen.getConstructor(
               String.class).newInstance(ib.getIndex());

			/*
          * Setup evaluation
			 */
         writer.writeComment("Evaluation Setup: " + setupFile.getAbsolutePath()
               + " \n");
         writer.writeComment("Index Base: " + indexFile.getAbsolutePath() + "" +
               " \n");

         evaluate(es, abstractFac, writer);

      } catch (IllegalAccessException e) {
         e.printStackTrace();
      } catch (InstantiationException e) {
         e.printStackTrace();
      } catch (JAXBException e) {
         e.printStackTrace();
      } catch (IOException e) {
         e.printStackTrace();
      } catch (NoSuchMethodException e) {
         e.printStackTrace();
      } catch (InvocationTargetException e) {
         e.printStackTrace();
      } catch (ClassNotFoundException e) {
         e.printStackTrace();
      }
   }
}
