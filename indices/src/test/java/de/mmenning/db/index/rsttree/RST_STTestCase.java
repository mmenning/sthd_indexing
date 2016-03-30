package de.mmenning.db.index.rsttree;

import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.Random;
import java.util.Set;

import de.mmenning.db.index.rsttree.RSTTree;
import de.mmenning.db.index.rsttree.RSTTreeFactory;
import de.mmenning.db.index.rsttree.STRectangle;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import de.mmenning.db.index.NDPoint;
import de.mmenning.db.index.NDRectangle;
import de.mmenning.db.index.NDRectangleKey;
import de.mmenning.db.index.NowGen;
import de.mmenning.db.index.RectangleQuery;
import de.mmenning.db.index.STFunctions;
import de.mmenning.db.index.generate.IndexFiller;
import de.mmenning.db.index.generate.NDRandomRectangleGenerator;
import de.mmenning.db.index.rtree.NDRTree;
import de.mmenning.db.index.rtree.Node;
import de.mmenning.db.index.rtree.NodeEntryPair;
import de.mmenning.db.storage.DefaultStorage;
import de.mmenning.db.storage.ObjectReference;
import de.mmenning.util.math.RandomInterval;
import de.mmenning.util.math.STRandomInterval;
import de.mmenning.util.math.Uniform;

public class RST_STTestCase {

	private RSTTree tree;

	// public final long seed = new Random().nextLong();
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

	public NDRTree getTree() {
		return this.tree;
	}

	/*
	 * set up with a tree with 50 boxables
	 */
	@Before
	public void setUp() throws Exception {

		rand = new Random();
		dim = 5;
		maxK = 4;
		initial = 100;
		size = 1000;
		EPSILON = 0.001;
		now = new NowGen(1.0, 0.0001);
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

		this.tree = RSTTreeFactory.createRSTTree(maxK, dim - 2, 0.75, 0, now);
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

	@After
	public void tearDown() throws Exception {
		DefaultStorage.getInstance().cleanUp();
	}

	public NDRectangleKey nextNDRectangleKey() {
		double[] vt = validTime.getNext();
		NDRectangle k = IndexFiller.buildSTRectangle(
				spatialGen.getNextRectangle(), now.getNow(),
				STFunctions.CURRENT, vt[0], vt[1]);
		return new NDRectangleKey(ObjectReference.getReference(new Object()), k);
	}

	/*
	 * Compute Bounding Boxes and check from leaf to top
	 */
	@Test
	public void testBoundingBoxes() {
		ArrayList<NodeEntryPair> l = new ArrayList<NodeEntryPair>();
		l.add(new NodeEntryPair(null, this.getTree().getRoot()));
		this.rekDownTestBoundingBoxes(l);
	}

	@Test
	public void testContains() {
		for (final NDRectangleKey b : this.added) {
			try {
				assertTrue(this.getTree().contains(b));
			} catch (AssertionError ae) {
				throw new AssertionError("not contained?! "
						+ b.getNDKey().toString());
			}
		}
		assertFalse(this.getTree().contains(this.nextNDRectangleKey()));
	}

	@Test
	public void testDelete() {
		// System.out.println(seed);

		for (final NDRectangleKey b : this.added) {
			// assertTrue(this.getTree().delete(b));
			if (!this.getTree().delete(b)) {
				throw new AssertionError("not deleted "
						+ b.getNDKey().toString());
			}

			if (this.getTree().contains(b)) {
				throw new AssertionError("deleted, but still contained "
						+ b.getNDKey().toString());
			}
			// assertFalse(this.getTree().contains(b));

		}
		assertFalse(this.getTree().delete(this.nextNDRectangleKey()));
		assertEquals(0, this.getTree().size());
		assertEquals(0, this.getTree().getAll().size());
	}

	@Test(expected = NullPointerException.class)
	public void testDeleteNull() {
		this.getTree().delete(null);
	}

	@Test
	public void testInsert() {
		assertTrue(this.getTree().insert(this.nextNDRectangleKey()));
	}

	@Test(expected = NullPointerException.class)
	public void testInsertNull() throws NullPointerException {
		this.getTree().insert(null);
	}

	@Test
	public void testIsLeaf() {
		final Set<Node> nodes = this.getTree().getAllNodes();
		for (final Node n : nodes) {
			if (n.isLeaf()) {
				for (int i = 0; i < n.size(); i++) {
					assertTrue(!(n.get(i).isNodeEntry()));
				}
			}
		}
	}

	@Test
	public void testMaxCapacity() {
		assertEquals(this.getTree().getMaxCapacity(), maxK);
		final Set<Node> nodes = this.getTree().getAllNodes();
		for (final Node n : nodes) {
			assertTrue(n.size() <= this.getTree().getMaxCapacity());
		}
	}

	@Test
	public void testMinCapacity() {
		final Set<Node> nodes = this.getTree().getAllNodes();

		for (final Node n : nodes) {
			if (!n.getObjectReference().equals(
					this.getTree().getRoot().getObjectReference())) {
				assertTrue(n.size() >= (int) (n.getMaxCapacity() * this
						.getTree().getMinFanout()));
			}
		}
	}

	@Test
	public void testSize() {
		assertEquals(size, this.getTree().size());

		assertTrue(this.getTree().insert(this.nextNDRectangleKey()));
		assertEquals(this.getTree().size(), size + 1);

		assertTrue(this.getTree().delete(this.added.iterator().next()));
		assertEquals(this.getTree().size(), size);

		ArrayList l = new ArrayList();
		this.getTree().getAll(l);
		assertEquals(this.getTree().size(), l.size());
	}

	private void rekDownTestBoundingBoxes(final ArrayList<NodeEntryPair> path) {

		final Node n = path.get(path.size() - 1).getNode();
		if (!n.isLeaf()) {
			for (int i = 0; i < n.size(); i++) {
				ArrayList<NodeEntryPair> newPath = (ArrayList<NodeEntryPair>) path
						.clone();
				newPath.add(new NodeEntryPair(n.get(i), (Node) DefaultStorage
						.getInstance().load(n.get(i).getChild()).getObject()));
				rekDownTestBoundingBoxes(newPath);
			}
		} else {
			this.rekUpTestBoundingBoxes(path);
		}
	}

	private void rekUpTestBoundingBoxes(final ArrayList<NodeEntryPair> path) {
		for (int i = path.size() - 1; i >= 0; i--) {
			testBoundingBoxes(path.get(i));
		}
	}

	private void testBoundingBoxes(final NodeEntryPair n) {
		if (n.getParentEntry() != null) {
			final NDRectangle a = n.getParentEntry().getMBBox();

			Node c = n.getNode();
			NDRectangle b = c.get(0).getMBBox();
			for (int i = 1; i < c.size(); i++) {
				b = b.union(c.get(i).getMBBox());
			}

			assertArrayEquals(a.getBegin().toArray(), b.getBegin().toArray(),
					EPSILON);
			assertArrayEquals(a.getEnd().toArray(), b.getEnd().toArray(),
					EPSILON);
		}
	}

	public void testContainingAll() {
		final LinkedList l = new LinkedList();

		this.getTree().getAll(l);
		NDRectangle testRegion = new NDRectangle(new NDPoint(new double[] {
				-1000, -1000, -1000, -1000, -1000 }), new NDPoint(new double[] {
				10000, 10000, 10000, 10000, 10000 }));
		int contained = 0;
		for (NDRectangleKey k : added) {
			if (testRegion.contains(k.getNDKey())) {
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

		this.getTree().getContained(testRegion, q);

		assertEquals(contained, l.size());
	}

	@Test
	public void testContained() {
		final LinkedList l = new LinkedList();

		this.getTree().getAll(l);
		NDRectangle testRegion = new NDRectangle(new NDPoint(new double[] { 5,
				-1000, -1000, -1000, -1000 }), new NDPoint(new double[] {
				10000, 10000, 10000, 10000, 10000 }));
		int contained = 0;
		for (NDRectangleKey k : added) {
			if (testRegion.contains(k.getNDKey())) {
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

		this.getTree().getContained(testRegion, q);

		assertEquals(contained, l.size());
	}

	@Test
	public void testContainedST() {
		final LinkedList l = new LinkedList();

		this.getTree().getAll(l);
		NDRectangle testRegion = new NDRectangle(new NDPoint(new double[] { 5,
				5, 20, 10, 20 }), new NDPoint(new double[] { 10, 40, 50,
				STFunctions.CURRENT, STFunctions.CURRENT }));
		STRectangle stTestRegion = new STRectangle(testRegion,
				this.tree.getSTConstants());

		int contained = 0;
		for (NDRectangleKey k : added) {
			if (STFunctions.contains(stTestRegion, k.getNDKey(), this.now)) {
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

		this.getTree().getContained(testRegion, q);

		assertEquals(contained, l.size());
	}

	@Test
	public void testContainingDisjoining() {
		final LinkedList l = new LinkedList();

		this.getTree().getAll(l);
		NDRectangle testRegion = new NDRectangle(new NDPoint(new double[] {
				1000, 1000, 1000, 1000, 11000 }), new NDPoint(new double[] {
				10000, 10000, 10000, 10000, 10000 }));
		int contained = 0;
		for (NDRectangleKey k : added) {
			if (testRegion.contains(k.getNDKey())) {
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

		this.getTree().getContained(testRegion, q);

		assertEquals(contained, l.size());
	}

	@Test
	public void testIntersected() {
		final LinkedList l = new LinkedList();

		this.getTree().getAll(l);
		NDRectangle testRegion = new NDRectangle(new NDPoint(new double[] { 5,
				5, 5, 5, 5 }), new NDPoint(new double[] { 50, 50, 50, 50, 50 }));
		int intersected = 0;
		for (NDRectangleKey k : added) {
			if (testRegion.intersects(k.getNDKey())) {
				intersected++;
			}
		}

		RectangleQuery q = new RectangleQuery() {

			@Override
			public boolean query(NDRectangleKey k) {
				l.add(k);
				return true;
			}

		};

		l.clear();
		this.getTree().getIntersected(testRegion, q);

		assertEquals(intersected, l.size());
	}

	@Test
	public void testUpdate() {

		NDRectangleKey newOne = nextNDRectangleKey();

		NDRectangleKey oldOne = added.iterator().next();

		assertTrue(this.getTree().update(oldOne, newOne));

		assertTrue(this.getTree().contains(newOne));
		assertFalse(this.getTree().contains(oldOne));

		assertFalse(this.getTree().update(oldOne, added.iterator().next()));

	}
}