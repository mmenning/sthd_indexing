package de.mmenning.db.index.rtree;

import de.mmenning.db.index.NDPoint;
import de.mmenning.db.index.NDRectangle;
import de.mmenning.db.index.NDRectangleKey;
import de.mmenning.db.index.RectangleQuery;
import de.mmenning.db.index.generate.NDRandomRectangleGenerator;
import de.mmenning.db.index.generate.RectangleDistributions;
import de.mmenning.db.index.generate.StatsObject;
import de.mmenning.db.storage.DefaultStorage;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.Set;

import static org.junit.Assert.*;

public class NDRTreeTestCase {

	protected int dim = 5;
	protected int maxK = 8;
	protected int initialSize = 1000;
	public static final double EPSILON = 0.001;

	private NDRTree tree;
	protected Set<NDRectangleKey> added = new HashSet<NDRectangleKey>();
	protected NDRandomRectangleGenerator recGen = RectangleDistributions.UNIFORM_5D;

	public NDRTree getTree() {
		return this.tree;
	}

	public NDRectangleKey nextNDRectangleKey() {
		return new NDRectangleKey(new StatsObject().getObjectReference(),
				this.recGen.getNextRectangle());
	}

	/*
	 * set up with a tree with 50 boxables
	 */
	@Before
	public void setUp() throws Exception {
		this.tree = new NDRTree(maxK, 0.5, dim);

		for (int i = 0; i < initialSize; i++) {
			final NDRectangleKey box = this.nextNDRectangleKey();

			assertTrue(this.tree.insert(box));

			if (!this.added.add(box)) {
				fail("Boxable already added - not unique");
			}
		}
	}

	@After
	public void tearDown() throws Exception {

		DefaultStorage.getInstance().cleanUp();
	}

	/*
	 * Compute Bounding Boxes and check from leaf to top
	 */
	@Test
	public void testBoundingBoxes() {
		ArrayList<NodeEntryPair> path = new ArrayList<NodeEntryPair>();
		path.add(new NodeEntryPair(null, this.getTree().getRoot()));
		this.rekDownTestBoundingBoxes(path);
	}

	@Test
	public void testContains() {
		for (final NDRectangleKey b : this.added) {
			assertTrue(this.getTree().contains(b));
		}
		assertFalse(this.getTree().contains(this.nextNDRectangleKey()));
	}

	@Test
	public void testDelete() {
		for (final NDRectangleKey b : this.added) {
			assertTrue(this.getTree().delete(b));
			assertFalse(this.getTree().contains(b));
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
			assertTrue(n.size() <= n.getMaxCapacity());
		}
	}

	@Test
	public void testMinCapacity() {
		final Set<Node> nodes = this.getTree().getAllNodes();

		for (final Node n : nodes) {
			if (!n.getObjectReference().equals(
					this.getTree().getRoot().getObjectReference())) {
				int minCapacity = (int) (n.getMaxCapacity() * this.getTree()
						.getMinFanout());

				assertTrue(n.size() >= minCapacity);
			}
		}
	}

	@Test
	public void testSize() {
		assertEquals(initialSize, this.getTree().size());

		assertTrue(this.getTree().insert(this.nextNDRectangleKey()));
		assertEquals(this.getTree().size(), initialSize + 1);

		assertTrue(this.getTree().delete(this.added.iterator().next()));
		assertEquals(this.getTree().size(), initialSize);

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
			final NDRectangle b = this.getTree().unionEntries(n.getNode(), 0,
					n.getNode().size());

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
		RectangleQuery q = new RectangleQuery() {

			@Override
			public boolean query(NDRectangleKey k) {
				l.add(k);
				return true;
			}

		};

		l.clear();
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

		l.clear();

		RectangleQuery q = new RectangleQuery() {

			@Override
			public boolean query(NDRectangleKey k) {
				l.add(k);
				return true;
			}

		};

		this.getTree().getIntersected(testRegion, q);

		assertEquals(intersected, l.size());
	}

	@Test
	public void testLeafPathLength() {
		ArrayList<NDRTree.NodeEntryPair> path = new ArrayList<NDRTree.NodeEntryPair>();

		path.add(this.getTree().new NodeEntryPair(-1, this.getTree().getRoot()
				.getObjectReference()));

		this.getTree().findLeaf(path, added.iterator().next());

		assertEquals(path.size(), this.getTree().getHeight());

	}

	@Test
	public void testFindLeaf() {
		ArrayList<NDRTree.NodeEntryPair> path = new ArrayList<NDRTree.NodeEntryPair>();

		path.add(this.getTree().new NodeEntryPair(-1, this.getTree().getRoot()
				.getObjectReference()));

		this.getTree().findLeaf(path, added.iterator().next());

		for (int i = 0; i < path.size() - 1; i++) {

			Node p = path.get(i).getNode();
			Node n = path.get(i + 1).getNode();
			int index = path.get(i + 1).indexInParent();

			assertEquals(p.get(index).getChild(), n.getObjectReference());

		}
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


//	final int REGION_TESTS = 10000;
//
//	@Test
//	public void testMultipleContained() {
//		RandomInterval[] randIs = new RandomInterval[dim];
//		for (int i = 0; i < dim; i++) {
//			randIs[i] = new RandomInterval(new Uniform(0.1, 0.8), new Uniform(
//					0, 0.1));
//		}
//
//		final NDRandomRectangleGenerator queries = new NDRandomRectangleGenerator(
//				randIs);
//
//		NowGen now = new NowGen(0,0);
//
//		for (int i = 0; i < REGION_TESTS; i++) {
//			NDRectangle testRegion = queries.getNextRectangle();
//			HashSet<NDRectangleKey> contained = new HashSet<>();
//
//			for (NDRectangleKey k : this.added) {
//				if (STFunctions.contains(testRegion, k.getNDKey(),now )) {
//					contained.add(k);
//				}
//			}
//
//			final HashSet<NDRectangleKey> s = new HashSet<>();
//
//			RectangleQuery q = new RectangleQuery() {
//				@Override
//				public boolean query(NDRectangleKey v) {
//					s.add(v);
//					return true;
//				}
//			};
//
//			this.getTree().getContained(testRegion, q);
//
//			// if (contained.size() != s.size()) {
//			// System.out.println(testRegion);
//			// for (NDRectangleKey k : contained) {
//			// if (!s.contains(k)) {
//			// System.out.println(k.getNDKey());
//			// }
//			// }
//			// }
//
//			assertEquals(contained.size(), s.size());
//		}
//	}
//
//
//	@Test
//	public void testMultipleIntersected() {
//		RandomInterval[] randIs = new RandomInterval[dim];
//		for (int i = 0; i < dim; i++) {
//			randIs[i] = new RandomInterval(new Uniform(0.1, 0.8), new Uniform(
//					0, 0.1));
//		}
//
//		NowGen now = new NowGen(0,0);
//
//		final NDRandomRectangleGenerator queries = new NDRandomRectangleGenerator(
//				randIs);
//
//		for (int i = 0; i < REGION_TESTS; i++) {
//			final HashSet<NDRectangleKey> s = new HashSet<>();
//
//			final HashSet<NDRectangleKey> intersected = new HashSet<>();
//
//			NDRectangle testRegion = queries.getNextRectangle();
//
//			for (NDRectangleKey k : this.added) {
//				if (STFunctions.intersects(testRegion, k.getNDKey(), now)) {
//					intersected.add(k);
//				}
//			}
//
//			RectangleQuery q = new RectangleQuery() {
//				@Override
//				public boolean query(NDRectangleKey v) {
//					assertTrue(s.add(v));
//					return true;
//				}
//			};
//
//			this.getTree().getIntersected(testRegion, q);
//
//			if (intersected.size() != s.size()) {
//				System.out.println(s.size() + " / " + intersected.size());
//				System.out.println(testRegion);
//				System.out.println("NOW: " + now.getNow());
//				System.out.println();
//				for (NDRectangleKey k : intersected) {
//					if (!s.contains(k)) {
//						System.out.println(k.getNDKey());
//					}
//				}
//
//			}
//
//			assertEquals(intersected.size(), s.size());
//		}
//	}

}
