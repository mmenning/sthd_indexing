package de.mmenning.db.index.sequential;

import de.mmenning.db.index.*;
import de.mmenning.db.index.generate.IndexFiller;
import de.mmenning.db.index.generate.NDRandomRectangleGenerator;
import de.mmenning.db.storage.CountingStorage;
import de.mmenning.db.storage.DefaultStorage;
import de.mmenning.db.storage.IterableStorageManager;
import de.mmenning.db.storage.ObjectReference;
import de.mmenning.util.math.RandomInterval;
import de.mmenning.util.math.STRandomInterval;
import de.mmenning.util.math.Uniform;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.util.*;

import static org.junit.Assert.*;

/**
 * @author Mathias Menninghaus (mathias.mennighaus@uos.de)
 */
public class SequentialAccessTestCase {

   protected int dim;
   protected int size;
   protected int initialSize;
   private SequentialAccess sequentialAccess;
   protected Set<NDRectangleKey> added = new HashSet<NDRectangleKey>();
   protected NDRandomRectangleGenerator spatialGen;
   protected RandomInterval validTime;
   protected double EPSILON;
   protected NowGen now;
   protected Random rand;


   public SequentialAccess getSequentialAccess() {
      return this.sequentialAccess;
   }

   public NDRectangleKey nextNDRectangleKey() {
      double[] vt = validTime.getNext();
      NDRectangle k = IndexFiller.buildSTRectangle(
            spatialGen.getNextRectangle(), now.getNow(),
            STFunctions.CURRENT, vt[0], vt[1]);
      return new NDRectangleKey(ObjectReference.getReference(new Object()), k);
   }


   /*
    * set up with a tree with 50 boxables
    */
   @Before
   public void setUp() throws Exception {

      rand = new Random();
      dim = 5;
      initialSize = 1000;
      size = 1000;

      EPSILON = 0.001;

      now  = new NowGen(1.0, 0.0001);

      this.sequentialAccess = new SequentialAccess(dim, new CountingStorage
            (4096), now);

      validTime = new STRandomInterval(new Uniform(5, 990, rand.nextLong()),
            new Uniform(0, 5, rand.nextLong()), 0.1, new Uniform(0, 1,
            rand.nextLong()), now);
      added = new HashSet<NDRectangleKey>();

      RandomInterval[] randIs = new RandomInterval[dim - 2];
      for (int i = 0; i < dim - 2; i++) {
         randIs[i] = new RandomInterval(
               new Uniform(5, 990, rand.nextLong()), new Uniform(0, 5,
               rand.nextLong()));
      }
      spatialGen = new NDRandomRectangleGenerator(randIs);
      RandomInterval validTime = new STRandomInterval(new Uniform(5, 990,
            rand.nextLong()), new Uniform(0, 5, rand.nextLong()), 0.1,
            new Uniform(0, 1, rand.nextLong()), now);

      ArrayList<NDRectangleKey> activeHistories = new ArrayList<NDRectangleKey>();

		/*
       * initialSetup
		 */
      IndexFiller.fillIndex(sequentialAccess, activeHistories,
            IndexFiller.createOperations(initialSize, 1, 0, 0, rand), now,
            spatialGen, validTime, rand);
		/*
		 * fill Index
		 */
      IndexFiller.fillIndex(sequentialAccess, activeHistories,
            IndexFiller.createOperations(size - initialSize, 0, 1, 0, rand),
            now, spatialGen, validTime, rand);


      IterableStorageManager storageManager = this.getSequentialAccess()
            .getStorageManager();
      for (ObjectReference objectReference : storageManager) {
         added.add((NDRectangleKey) storageManager.load(objectReference)
               .getObject());
      }
   }

   @After
   public void tearDown() throws Exception {

      DefaultStorage.getInstance().cleanUp();
   }

   @Test
   public void testContains() {
      for (final NDRectangleKey b : this.added) {
         assertTrue(this.getSequentialAccess().contains(b));
      }
      assertFalse(this.getSequentialAccess().contains(this.nextNDRectangleKey()));
   }

   @Test
   public void testDelete() {
      for (final NDRectangleKey b : this.added) {
         assertTrue(this.getSequentialAccess().delete(b));
         assertFalse(this.getSequentialAccess().contains(b));
      }
      assertFalse(this.getSequentialAccess().delete(this.nextNDRectangleKey()));
      assertEquals(0, this.getSequentialAccess().size());
   }

   @Test
   public void testInsert() {
      assertTrue(this.getSequentialAccess().insert(this.nextNDRectangleKey()));
   }

   @Test(expected = NullPointerException.class)
   public void testInsertNull() throws NullPointerException {
      this.getSequentialAccess().insert(null);
   }

   @Test
   public void testSize() {
      assertEquals(initialSize, this.getSequentialAccess().size());

      assertTrue(this.getSequentialAccess().insert(this.nextNDRectangleKey()));
      assertEquals(this.getSequentialAccess().size(), initialSize + 1);

      assertTrue(this.getSequentialAccess().delete(this.added.iterator().next()));
      assertEquals(this.getSequentialAccess().size(), initialSize);
   }

   @Test
   public void testContained() {

      LinkedList<NDRectangleKey> l = new LinkedList<>();

      IterableStorageManager storageManager = this.getSequentialAccess()
            .getStorageManager();
      for (ObjectReference objectReference : storageManager) {
         l.add((NDRectangleKey) storageManager.load(objectReference)
               .getObject());
      }


      NDRectangle testRegion = new NDRectangle(new NDPoint(new double[]{5,
            -1000, -1000, -1000, -1000}), new NDPoint(new double[]{
            10000, 10000, 10000, 10000, 10000}));
      int contained = 0;
      for (NDRectangleKey k : added) {
         if (STFunctions.contains(testRegion,k.getNDKey(),now)) {
            contained++;
         }
      }

      l.clear();

      RectangleQuery q = new RectangleQuery() {

         @Override
         public boolean query(NDRectangleKey k) {
            l.add(k);
            return true;
         }

      };

      this.getSequentialAccess().getContained(testRegion, q);

      assertEquals(contained, l.size());
   }

   @Test
   public void testContainingDisjoining() {
      final LinkedList l = new LinkedList();

      IterableStorageManager storageManager = this.getSequentialAccess()
            .getStorageManager();
      for (ObjectReference objectReference : storageManager) {
         l.add((NDRectangleKey) storageManager.load(objectReference)
               .getObject());
      }
      NDRectangle testRegion = new NDRectangle(new NDPoint(new double[]{
            1000, 1000, 1000, 1000, 11000}), new NDPoint(new double[]{
            10000, 10000, 10000, 10000, 10000}));
      int contained = 0;
      for (NDRectangleKey k : added) {
         if (STFunctions.contains(testRegion,k.getNDKey(),now)) {
            contained++;
         }
      }

      l.clear();
      RectangleQuery q = new RectangleQuery() {

         @Override
         public boolean query(NDRectangleKey k) {
            l.add(k);
            return true;
         }

      };

      this.getSequentialAccess().getContained(testRegion, q);

      assertEquals(contained, l.size());
   }

   @Test
   public void testIntersected() {
      final LinkedList l = new LinkedList();

      IterableStorageManager storageManager = this.getSequentialAccess()
            .getStorageManager();
      for (ObjectReference objectReference : storageManager) {
         l.add((NDRectangleKey) storageManager.load(objectReference)
               .getObject());
      }
      NDRectangle testRegion = new NDRectangle(new NDPoint(new double[]{5,
            5, 5, 5, 5}), new NDPoint(new double[]{50, 50, 50, 50, 50}));
      int intersected = 0;
      for (NDRectangleKey k : added) {
         if (STFunctions.intersects(testRegion, k.getNDKey(), now)) {
            intersected++;
         }
      }

      l.clear();

      RectangleQuery q = new RectangleQuery() {

         @Override
         public boolean query(NDRectangleKey k) {
            l.add(k);
            return true;
         }

      };

      this.getSequentialAccess().getIntersected(testRegion, q);

      assertEquals(intersected, l.size());
   }

   @Test
   public void testUpdate() {

      NDRectangleKey newOne = nextNDRectangleKey();

      NDRectangleKey oldOne = added.iterator().next();

      assertTrue(this.getSequentialAccess().update(oldOne, newOne));

      assertTrue(this.getSequentialAccess().contains(newOne));
      assertFalse(this.getSequentialAccess().contains(oldOne));

      assertFalse(this.getSequentialAccess().update(oldOne, added.iterator().next()));

   }
}
