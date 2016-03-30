package de.mmenning.db.index.evaluation;

import de.mmenning.db.index.*;
import de.mmenning.db.index.generate.IndexFiller;
import de.mmenning.db.index.generate.IndexFiller.DBOperation;
import de.mmenning.db.index.generate.NDRectangleGenerator;
import de.mmenning.util.math.RandomInterval;

import java.io.IOException;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class IndexEvaluation implements Serializable {

   private static final long serialVersionUID = -1467926528318964660L;

   public static final int BUILD_UP = 0;
   public static final int QUERY_CONTAINED = 1;
   public static final int QUERY_INTERSECT = 2;

   private final RectangleQuery evaluationQuery = new RectangleQuery() {
      @Override
      public boolean query(NDRectangleKey k) {
         return true;
      }
   };

   private double query(int queryType) {
      double start = goalFunc.value(this.index).doubleValue();

      NDRectangle queryRec;

      for (int j = 0; j < this.queries; j++) {

         queryRec = this.queryGenerator.getNextRectangle();
         switch (queryType) {
            case QUERY_CONTAINED:
               this.index.getContained(queryRec, this.evaluationQuery);
               break;
            case QUERY_INTERSECT:
               this.index.getIntersected(queryRec, this.evaluationQuery);
               break;
            default:
               throw new RuntimeException("unknown query type");
         }
      }
      return goalFunc.value(this.index).doubleValue() - start;
   }

   protected final EvaluationGoalFunction<?> goalFunc;
   protected final int queries;
   protected final NDRectangleKeyIndex index;
   protected final NDRectangleGenerator spatialRecGen;
   protected final NDRectangleGenerator queryGenerator;
   protected final ArrayList<NDRectangleKey> activeHistories;
   protected final NowGen now;
   protected final Random rand;
   protected final RandomInterval validTime;
   protected final double percentageStart;
   protected final double percentageEnd;
   protected final double percentageUpdate;

   public IndexEvaluation(
         EvaluationGoalFunction<?> goalFunc,
         int initialSize,
         int queries,
         NDRectangleKeyIndex index,
         NDRectangleGenerator spatialRecGen,
         NDRectangleGenerator queryGenerator,
         NowGen now,
         Random rand,
         RandomInterval validTime,
         double percentageStart,
         double percentageEnd,
         double percentageUpdate) {
      super();
      this.goalFunc = goalFunc;
      this.queries = queries;
      this.index = index;
      this.spatialRecGen = spatialRecGen;
      this.queryGenerator = queryGenerator;
      this.now = now;
      this.rand = rand;
      this.validTime = validTime;
      this.percentageStart = percentageStart;
      this.percentageEnd = percentageEnd;
      this.percentageUpdate = percentageUpdate;
      this.activeHistories = new ArrayList<NDRectangleKey>();
      init(initialSize);
   }

   private void init(int initialSize) {
      /*
       * initial
		 */
      List<DBOperation> operations = IndexFiller.createOperations(
            initialSize, 1.0, 0, 0, rand);
      IndexFiller.fillIndex(index, activeHistories, operations,
            now, spatialRecGen, validTime, rand);
   }

   public double[] evaluate(int insertedElements) throws IOException {
		/*
		 * buildUp
		 */
      List<DBOperation> operations = IndexFiller.createOperations(
            insertedElements, percentageStart, percentageUpdate,
            percentageEnd, rand);
      double start = goalFunc.value(index).doubleValue();
      IndexFiller.fillIndex(index, activeHistories, operations,
            now, spatialRecGen, validTime, rand);

      double buildUp = goalFunc.value(index).doubleValue() - start;

      /*
      query
       */

      double[] result = new double[3];
      result[BUILD_UP] = buildUp;
      result[QUERY_CONTAINED] = this.query(QUERY_CONTAINED);
      result[QUERY_INTERSECT] = this.query(QUERY_INTERSECT);

      return result;
   }
}
