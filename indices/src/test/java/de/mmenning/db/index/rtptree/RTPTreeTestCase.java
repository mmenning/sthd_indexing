package de.mmenning.db.index.rtptree;

import de.mmenning.db.index.*;
import de.mmenning.db.index.generate.IndexFiller;
import de.mmenning.db.index.generate.NDRandomRectangleGenerator;
import de.mmenning.db.storage.DefaultStorage;
import de.mmenning.db.storage.ObjectReference;
import de.mmenning.util.math.RandomInterval;
import de.mmenning.util.math.STRandomInterval;
import de.mmenning.util.math.Uniform;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.util.*;

import static org.junit.Assert.*;

public class RTPTreeTestCase {

	protected Random rand;
	protected int dim;
	protected int maxK;
	protected int initial;
	protected int size;
	protected double EPSILON;
	protected NowGen now;
	protected RandomInterval validTime;
	protected Set<NDRectangleKey> added;
	protected NDRandomRectangleGenerator spatialGen;

	protected final int REGION_TESTS = 1000;

	private RTPTree tree;

	public RTPTree getTree() {
		return this.tree;
	}

	public NDRectangleKey nextNDRectangleKey() {
		double[] vt = validTime.getNext();
		NDRectangle k = IndexFiller.buildSTRectangle(
				spatialGen.getNextRectangle(), now.getNow(),
				STFunctions.CURRENT, vt[0], vt[1]);
		return new NDRectangleKey(ObjectReference.getReference(new Object()), k);
	}

	@Before
	public void setUp() throws Exception {

		rand = new Random();
		dim = 5;
		maxK = 4;
		initial = 1000;
		size = 10000;
		EPSILON = 0.001;
		now = new NowGen(0.1, 0.9 / (size + 1));
		validTime = new STRandomInterval(new Uniform(0.005, 0.990,
				rand.nextLong()), new Uniform(0, 0.005, rand.nextLong()), 0.1,
				new Uniform(0, 1, rand.nextLong()), now);
		added = new HashSet<NDRectangleKey>();

		RandomInterval[] randIs = new RandomInterval[dim - 2];
		for (int i = 0; i < dim - 2; i++) {
			randIs[i] = new RandomInterval(new Uniform(0.005, 0.990,
					rand.nextLong()), new Uniform(0, 0.005, rand.nextLong()));
		}
		spatialGen = new NDRandomRectangleGenerator(randIs);
		RandomInterval validTime = new STRandomInterval(new Uniform(0.005,
				0.990, rand.nextLong()),
				new Uniform(0, 0.005, rand.nextLong()), 0.1, new Uniform(0, 1,
						rand.nextLong()), now);

		this.tree = new RTPTree(dim, 4096, DefaultStorage.getInstance(), now);

		double[] median = new double[dim];
		Arrays.fill(median, 0.75);
		this.tree.setMedian(median, median);

		ArrayList<NDRectangleKey> activeHistories = new ArrayList<NDRectangleKey>();

		/*
		 * initialSetup
		 */
		IndexFiller.fillIndex(tree, activeHistories,
				IndexFiller.createOperations(initial, 1, 0, 0, rand), now,
				spatialGen, validTime, rand);
		/*
		 * fill Index
		 */
		IndexFiller.fillIndex(tree, activeHistories,
				IndexFiller.createOperations(size - initial, 0, 1, 0, rand),
				now, spatialGen, validTime, rand);

		added = this.tree.getAll();
	}

	@Test
	public void testContains() {
		for (final NDRectangleKey k : this.added) {
			assertTrue(this.getTree().contains(k));
		}
		assertFalse(this.getTree().contains(this.nextNDRectangleKey()));
	}

	@Test
	public void testDelete() {
		assertFalse(this.getTree().delete(this.nextNDRectangleKey()));

		for (NDRectangleKey k : this.added) {
			assertTrue(this.getTree().delete(k));
		}
	}

	@Test
	public void testContainedQuery() {
		final NDRectangle testRegion = new NDRectangle(new NDPoint(
				new double[] { 0.050, 0.100, 0.200, 0.300, 0.400 }),
				new NDPoint(new double[] { 1.000, 1.000, 1.000,
						STFunctions.CURRENT, STFunctions.CURRENT }));

		final LinkedList<NDRectangleKey> contained = new LinkedList<>();

		for (NDRectangleKey k : added) {
			if (STFunctions.contains(testRegion, k.getNDKey(), now)) {
				contained.add(k);
			}
		}

		final HashSet<NDRectangleKey> result = new HashSet<>();

		RectangleQuery q = new RectangleQuery() {

			@Override
			public boolean query(NDRectangleKey k) {
				assertTrue(result.add(k));
				return true;
			}

		};

		this.getTree().getContained(testRegion, q);

		assertEquals(contained.size(), result.size());

		for (NDRectangleKey k : result) {
			assertTrue(STFunctions.contains(testRegion, k.getNDKey(), now));
		}
	}

	@Test
	public void testIntersectedQuery() {
		final NDRectangle testRegion = new NDRectangle(new NDPoint(
				new double[] { 0.050, 0.200, 0.300, 0.400, 0.500 }),
				new NDPoint(new double[] { 1.000, 1.000, 1.000,
						STFunctions.CURRENT, STFunctions.CURRENT }));
		final LinkedList<NDRectangleKey> intersected = new LinkedList<>();
		for (NDRectangleKey k : added) {
			if (STFunctions.intersects(testRegion, k.getNDKey(), now)) {
				intersected.add(k);
			}
		}

		final LinkedList<NDRectangleKey> l = new LinkedList<>();

		RectangleQuery q = new RectangleQuery() {

			@Override
			public boolean query(NDRectangleKey k) {
				l.add(k);
				return true;
			}
		};

		this.getTree().getIntersected(testRegion, q);

		assertEquals(intersected.size(), l.size());

		for (NDRectangleKey k : l) {

			assertTrue(STFunctions.intersects(testRegion, k.getNDKey(), now));
		}
	}

	@After
	public void tearDown() throws Exception {
		DefaultStorage.getInstance().cleanUp();
	}

	@Test
	public void testMultipleContained() {
		RandomInterval[] randIs = new RandomInterval[dim];
		for (int i = 0; i < dim - 2; i++) {
			randIs[i] = new RandomInterval(new Uniform(0.1, 0.8), new Uniform(
					0, 0.1));
		}
		randIs[dim - 2] = new STRandomInterval(new Uniform(0.1, 0.8),
				new Uniform(0, 0.1), 0.1, now);
		randIs[dim - 1] = new STRandomInterval(new Uniform(0.1, 0.8),
				new Uniform(0, 0.1), 0.1, now);

		final NDRandomRectangleGenerator queries = new NDRandomRectangleGenerator(
				randIs);

		for (int i = 0; i < REGION_TESTS; i++) {
			NDRectangle testRegion = queries.getNextRectangle();
			HashSet<NDRectangleKey> contained = new HashSet<>();

			for (NDRectangleKey k : this.added) {
				if (STFunctions.contains(testRegion, k.getNDKey(), now)) {
					contained.add(k);
				}
			}

			final HashSet<NDRectangleKey> s = new HashSet<>();

			RectangleQuery q = new RectangleQuery() {
				@Override
				public boolean query(NDRectangleKey v) {
					s.add(v);
					return true;
				}
			};

			this.getTree().getContained(testRegion, q);

			// if (contained.size() != s.size()) {
			// System.out.println(testRegion);
			// for (NDRectangleKey k : contained) {
			// if (!s.contains(k)) {
			// System.out.println(k.getNDKey());
			// }
			// }
			// }

			assertEquals(contained.size(), s.size());
		}
	}

	@Test
	public void testMultipleIntersected() {
		RandomInterval[] randIs = new RandomInterval[dim];
		for (int i = 0; i < dim - 2; i++) {
			randIs[i] = new RandomInterval(new Uniform(0.1, 0.8), new Uniform(
					0, 0.1));
		}
		randIs[dim - 2] = new STRandomInterval(new Uniform(0.1, 0.8),
				new Uniform(0, 0.1), 0.1, now);
		randIs[dim - 1] = new STRandomInterval(new Uniform(0.1, 0.8),
				new Uniform(0, 0.1), 0.1, now);

		final NDRandomRectangleGenerator queries = new NDRandomRectangleGenerator(
				randIs);

		for (int i = 0; i < REGION_TESTS; i++) {
			final HashSet<NDRectangleKey> s = new HashSet<>();

			final HashSet<NDRectangleKey> intersected = new HashSet<>();

			NDRectangle testRegion = queries.getNextRectangle();

			for (NDRectangleKey k : this.added) {
				if (STFunctions.intersects(testRegion, k.getNDKey(), now)) {
					intersected.add(k);
				}
			}

			RectangleQuery q = new RectangleQuery() {
				@Override
				public boolean query(NDRectangleKey v) {
					assertTrue(s.add(v));
					return true;
				}
			};

			this.getTree().getIntersected(testRegion, q);

			if (intersected.size() != s.size()) {
				System.out.println(s.size() + " / " + intersected.size());
				System.out.println(testRegion);
				System.out.println("NOW: " + now.getNow());
				System.out.println();
				for (NDRectangleKey k : intersected) {
					if (!s.contains(k)) {
						System.out.println(k.getNDKey());
					}
				}

			}

			assertEquals(intersected.size(), s.size());
		}
	}
}
