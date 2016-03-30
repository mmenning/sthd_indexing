package de.mmenning.db.index.rtree;

import de.mmenning.db.index.NDRectangle;
import de.mmenning.db.index.NDRectangleKey;
import de.mmenning.db.storage.DefaultStorage;
import de.mmenning.db.storage.StorageManager;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.LinkedList;

/**
 * Implementation of a n-dimensional R-Star Tree.
 *
 * @author Mathias Menninghaus (mathias.menninghaus@uos.de)
 * @article The R*-tree: An Efficient and Robust Access Method for Points and
 * Rectangles (Beckmann, Kriegel 1990)
 * @article R-Trees: A Dynamic Index Structure for Spatial Searching (Guttman,
 * 1984)
 */
public class NDRStar extends NDRTree {

   /**
    * Amount of elements that are tried to be reinserted. Typically set to 1/3.
    *
    * @see {@link #reInsert(ArrayList<NodeEntryPair>) }
    */
   protected final double p;

   /**
    * Created once per insert. If true at the given level, reinsert of p
    * elements is tried.
    *
    * @see {@link #overflowTreatment(ArrayList<NodeEntryPair>)}
    * @see {@link #createOverflowTreatmentTable()}
    * @see {@link #increaseOverflowTreatmentTable()}
    */
   protected boolean[] overflowTreatment;

   /**
    * Constructs NDRStar
    *
    * @param maxCapacity maximum Capacity of a single Node in the tree.
    * @param minFanout   minimum Capacity of a single Node in the tree. Must be between
    *                    0 and maxCapacity/2
    * @param dimensions  number of dimensions in this tree
    * @throw IllegalArgumentException if maxCapacity is smaller than
    * MIN_MAX_ENTRIES, or if minCapacity is not between 0 and
    * maxCapacity/2, or if dimensions is <2
    */
   public NDRStar(final int maxCapacity, final double minFanout,
                  final int dimensions) {

      this(maxCapacity, minFanout, dimensions, DefaultStorage.getInstance());
   }

   public NDRStar(int maxCapacity, double minFanout, int dimensions,
                  StorageManager s) {

      super(maxCapacity, minFanout, dimensions, s);

      this.p = 1.0 / 3.0;

   }

   public NDRStar(int blockSize, int dimensions, StorageManager s) {
      super(blockSize, dimensions, s);
      this.p = 1.0 / 3.0;

   }

   /**
    * Inserts such like <code>NDRTree</code> but creates the overflow treatment
    * table first ({@link #createOverflowTreatmentTable()})
    */
   @Override
   public synchronized boolean insert(final NDRectangleKey b) {

		/*
       * NDRStar additional code line: createOverflowTreatmentTable for every
		 * single insertion also has to be done for condenseTree
		 */
      this.createOverflowTreatmentTable();

      return super.insert(b);
   }

   /**
    * Chooses the best axis to compute a split in <code>Node</code>
    * <code>n</code>. The first axis is 0.
    *
    * @param n <code>Node</code> in which to search the best split axis
    * @return the best split axis
    */
   protected int chooseSplitAxis(final Node n) {
      final double[] S = new double[this.getDim()];
      /*
       * for every axis: sort entries by lower and upper value calculate every
		 * distribution sum up the margin value of every distribution : S[i]
		 */
      int firstIndices;

      int minCapacity = (int) (n.getMaxCapacity() * this.getMinFanout());

      for (int i = 0; i < S.length; i++) {

         byte isGreater = 0;

         while (isGreater < 2) {
            this.sortByValue(n, i, isGreater == 0);

            for (int k = 1; k <= n.getMaxCapacity() - 2 * minCapacity + 2; k++) {

               firstIndices = minCapacity - 1 + k;

               S[i] += this.unionEntries(n, 0, firstIndices)
                     .simpleMargin()
                     + this.unionEntries(n, firstIndices,
                     n.size() - firstIndices).simpleMargin();

            }
            isGreater++;
         }
      }
      /*
		 * find the minimum S
		 */
      int min = 0;
      for (int i = 1; i < S.length; i++) {
         if (S[i] < S[min]) {
            min = i;
         }
      }
		/*
		 * return index of minimum S
		 */
      return min;
   }

   @Override
   protected NodeEntryPair chooseSubtree(final Node n, final NDRectangle mbbox) {

		/*
		 * Test son for leaf
		 */
      if (!n.isEmpty()) {
         if (this.readNodeChild(n.get(0)).isLeaf()) {

				/*
				 * If sons reference to leafs: Choose the entry in n whose
				 * rectangle needs least overlap enlargement to include
				 * getMBBox().Resolve ties by choosing the entry whose rectangle
				 * needs least area enlargement
				 */

            double overlapEnlargement;
            double areaEnlargement;

            int bestEntry = 0;
            double bestOverlapEnlargement = this.computeOverlapEnlargement(
                  n, bestEntry, mbbox);
            double bestAreaEnlargement = this.computeAreaEnlargement(n,
                  bestEntry, mbbox);

            for (int i = 1; i < n.size(); i++) {

               overlapEnlargement = this.computeOverlapEnlargement(n, i,
                     mbbox);

               if (overlapEnlargement < bestOverlapEnlargement) {

                  bestEntry = i;
                  bestOverlapEnlargement = overlapEnlargement;
                  bestAreaEnlargement = this.computeAreaEnlargement(n, i,
                        mbbox);
               } else if (overlapEnlargement == bestOverlapEnlargement) {
                  areaEnlargement = this.computeAreaEnlargement(n, i,
                        mbbox);
                  if (areaEnlargement < bestAreaEnlargement) {
                     bestEntry = i;
                     bestOverlapEnlargement = overlapEnlargement;
                     bestAreaEnlargement = areaEnlargement;
                  }
               }
            }

            return new NodeEntryPair(bestEntry, n.get(bestEntry).getChild());

         } else {

				/*
				 * If sons not reference to leafs: Choose the Entry in n whose
				 * rectangle needs least area enlargement to include getMBBox().
				 * Resolve ties by choosing the Entry with rectangle of smallest
				 * area.
				 */

            int bestEntry = 0;
            double bestArea = n.get(bestEntry).getMBBox().volume();
            double area;
            double bestAreaEnlargement = this.computeAreaEnlargement(n,
                  bestEntry, mbbox);
            double areaEnlargement;

            for (int i = 1; i < n.size(); i++) {
               areaEnlargement = this.computeAreaEnlargement(n, i, mbbox);

               if (areaEnlargement < bestAreaEnlargement) {

                  bestEntry = i;
                  bestAreaEnlargement = areaEnlargement;
                  bestArea = n.get(i).getMBBox().volume();

               } else if (areaEnlargement == bestAreaEnlargement) {
                  area = n.get(i).getMBBox().volume();

                  if (area < bestArea) {
                     bestEntry = i;
                     bestAreaEnlargement = areaEnlargement;
                     bestArea = area;
                  }
               }
            }

            return new NodeEntryPair(bestEntry, n.get(bestEntry).getChild());

         }
      }
      return null;
   }

   /**
    * Computes the overlap enlargement of elements in <code>Node</code> by
    * <code>n</code> inserting a new bounding box into the <code>Entry</code>
    * at position index and summing up all overlap enlargements with the other
    * <code>Entry</code> elements in this <code>Node</code>.
    *
    * @param n     Node to compute the overlap of
    * @param index position of the Entry which should be expanded by getMBBox()
    * @param mbbox minimum bounding box to expand the Entry at position index
    *              with
    * @return the overlap enlargement of the Entry at index with all other
    * Entry elements in this Node when adding getMBBox() to it.
    */
   protected double computeOverlapEnlargement(final Node n, final int index,
                                              final NDRectangle mbbox) {
      double overlapEnlarge = 0.0;

      final NDRectangle union = n.get(index).getMBBox().union(mbbox);

      for (int i = 0; i < n.size(); i++) {

         if (i != index) {
            overlapEnlarge += n.get(i).getMBBox().intersect(union)
                  - n.get(i).getMBBox()
                  .intersect(n.get(index).getMBBox());
         }
      }
      return overlapEnlarge;
   }

   /**
    * After an element has been deleted, condenseTree is called to adjust the
    * corresponding leaf node which is denoted by <code>path</code>.
    * Recursively goes upwards and reinserts the entries of underfull nodes.
    * The underfull nodes will be deleted.Uses
    * {@link #rootSplitsWhileCondenseTree} to determine the level for
    * reinserting entries from underfull nodes. Creates a new overflow
    * treatment table for every
    *
    * @param path path to leaf-node l in which an element has been deleted
    * @see {@link #delete(NDRectangleKey)}
    * @see {@link #rootSplitsWhileCondenseTree}
    */
   @Override
   protected void condenseTree(ArrayList<NodeEntryPair> path) {
      final LinkedList<Entry> deleted = new LinkedList<Entry>();
      Node p;

      final LinkedList<Integer> levelOfDeleted = new LinkedList<Integer>();
      Node leaf = path.get(path.size() - 1).getNode();
      int currentLevel = path.size() - 1;

      while (path.get(currentLevel).indexInParent() != -1) {
         p = path.get(currentLevel - 1).getNode();
         final int minCapacity = (int) (leaf.getMaxCapacity() * this
               .getMinFanout());

         if (leaf.size() < minCapacity) {

            // remove brothers for reinsert
            for (int i = 0; i < leaf.size(); i++) {
               deleted.add(leaf.get(i));
               levelOfDeleted.add(currentLevel);
            }

            // remove the node
            if (p.removeEntry(path.get(currentLevel).indexInParent()) == null) {
               throw new RuntimeException("An Entry does not exist: "
                     + path.get(currentLevel).indexInParent());
            }
				/*
				 * Update parent node, delete removed node on disk
				 */
            this.deleteNode(leaf);
            this.storeNode(p);

         } else {
				/*
				 * parentEntry of n, only one step of adjustTree
				 */
            p.get(path.get(currentLevel).indexInParent()).setMBBox(
                  this.unionEntries(leaf, 0, leaf.size()));
				/*
				 * Update the parent node
				 */
            this.storeNode(leaf);
         }
         currentLevel--;
         leaf = p;
      }

      this.rootSplitsWhileCondenseTree = 0;

      Iterator<Integer> iter = levelOfDeleted.iterator();
      for (final Entry e : deleted) {
			/*
			 * NDRStar addition createOverflowTreatmentTable once for every
			 * insert
			 */
         this.createOverflowTreatmentTable();

         ArrayList<NodeEntryPair> newPath = this.createNewPath();

         if (!this.insert(newPath, e, iter.next()
               + this.rootSplitsWhileCondenseTree)) {
            throw new RuntimeException("ReInsert failed: " + e);
         }
      }
   }

   /**
    * Creates a boolean for every level in this tree to compute
    * overflowTreatment for the whole tree.
    *
    * @see {@link NDRStar#overflowTreatment(ArrayList<NodeEntryPair>)}
    */
   protected void createOverflowTreatmentTable() {
      this.overflowTreatment = new boolean[this.getHeight()];
      Arrays.fill(this.overflowTreatment, false);
   }

   /**
    * Determines the best distribution of entries given a specific splitAxis.
    *
    * @param n Node to generate the Distribution for. Node will not be
    *          changed.
    * @return Number of Entry elements in the first Group. The Entry array will
    * be sorted in the right way when this method ends.
    * @see {@link #chooseSplitAxis(Node)}
    */
   protected CandidateDistribution createRStarCandidateDistribution(
         final Node n) {

      final int splitAxis = this.chooseSplitAxis(n);

      return computeRStarSplit(n, splitAxis);
   }

   /**
    * Fills the given <code>CandidateDistribution</code>s
    * <code>firstNode</code> and <code>secondNode</code> <code>ArrayList</code>
    * with the <code>Entry</code> elements from <code>n</code>. Entries from 0
    * to <code>firstIndices</code>, excluding, are put in
    * <code>firstNode</code> and the others in <code>SecondNode</code>. The
    * covering Rectangles of <code>c</code> are not adjusted!
    *
    * @param n                <code>Node</code> which entries should be grouped into
    *                         <code>c</code>
    * @param bestFirstIndices Entries from 0 to <code>bestFirstIndices</code> are put into
    *                         <code>firstNode</code>, the others into
    *                         <code>secondNode</code>
    * @param c                <code>CandidateDistribution</code> which
    *                         <code>ArrayList</code>s should be filled by Entries form
    *                         <code>n</code>
    */
   protected void groupEntries(Node n, final int bestFirstIndices,
                               final CandidateDistribution c) {
      c.firstNode = new ArrayList<Entry>();
      for (int i = 0; i < bestFirstIndices; i++) {
         c.firstNode.add(n.get(i));
      }

      c.secondNode = new ArrayList<Entry>();
      for (int i = bestFirstIndices; i < n.size(); i++) {
         c.secondNode.add(n.get(i));
      }

   }

   @Override
   protected CandidateDistribution createSplitDistribution(final Node n) {
      final CandidateDistribution c = this
            .createRStarCandidateDistribution(n);
      return c;
   }

   protected CandidateDistribution computeRStarSplit(final Node n,
                                                     final int splitAxis) {

      byte sortGreaterValue = 0;

      double overlap;
      double area;

      double minOverlap = Double.MAX_VALUE;
      double minArea = Double.MAX_VALUE;

      int bestFirstIndices = -1;

      int firstIndices = -1;

      final CandidateDistribution c = new CandidateDistribution();

		/*
		 * first sort by greater value, then by lesser value
		 */
      while (sortGreaterValue < 2) {

         this.sortByValue(n, splitAxis, sortGreaterValue == 0);

			/*
			 * go through all possible distributions
			 */
         int minCapacity = (int) (n.getMaxCapacity() * this.getMinFanout());

         for (int k = 1; k <= n.getMaxCapacity() - 2 * minCapacity + 2; k++) {

            firstIndices = minCapacity - 1 + k;

				/*
				 * compare the distributions by their overlap
				 */
            final NDRectangle firstGroup = this.unionEntries(n, 0,
                  firstIndices);
            final NDRectangle secondGroup = this.unionEntries(n,
                  firstIndices, n.size() - firstIndices);

            overlap = firstGroup.intersect(secondGroup);

            if (overlap < minOverlap) {
               minOverlap = overlap;
               minArea = firstGroup.volume() + secondGroup.volume();
               bestFirstIndices = firstIndices;

               c.coveringFirst = firstGroup;
               c.coveringSecond = secondGroup;

               this.groupEntries(n, bestFirstIndices, c);

					/*
					 * resolve ties by using the the distribution which groups
					 * have the smaller area
					 */
            } else if (overlap == minOverlap) {
               area = firstGroup.volume() + secondGroup.volume();
               if (area < minArea) {
                  minOverlap = overlap;
                  minArea = area;
                  bestFirstIndices = firstIndices;

                  c.coveringFirst = firstGroup;
                  c.coveringSecond = secondGroup;

                  this.groupEntries(n, bestFirstIndices, c);
               }
            }
         }
         sortGreaterValue++;
      }

      if (bestFirstIndices == -1) {
         // System.err
         // .println("NDRStar.createRStarCandidateDistribution could not find a split, trivial splitting");
         bestFirstIndices = n.size() / 2;
         c.coveringFirst = this.unionEntries(n, 0, bestFirstIndices);
         c.coveringSecond = this.unionEntries(n, bestFirstIndices, n.size()
               - bestFirstIndices);
         this.groupEntries(n, bestFirstIndices, c);
      }

      return c;
   }

   /**
    * If a split of the root Node occurred, and the insertion is not already
    * finished, increase the Size of the overflow treatment-table by one. Set
    * the first entry to false and copy the values of the old overflow
    * treatment table to the following entries.
    */
   protected void increaseOverflowTreatmentTable() {
      if (this.overflowTreatment.length == this.getHeight()) {
         throw new RuntimeException(
               "Height equals length of overflow treatment table - why increase it?");
      }
      final boolean[] old = this.overflowTreatment;
      this.overflowTreatment = new boolean[this.getHeight()];
      this.overflowTreatment[0] = false;
      for (int i = 1; i < this.overflowTreatment.length; i++) {
         this.overflowTreatment[i] = old[i - 1];
      }
   }

   @Override
   protected boolean insert(ArrayList<NodeEntryPair> path, final Entry toAdd,
                            final int level) {

      Node n = path.get(path.size() - 1).getNode();

      if (n.isLeaf() || path.size() - 1 == level) {

         n.addEntry(toAdd);
         this.storeNode(n);

         this.adjustNode(path, toAdd.getMBBox());

         boolean split = true;

         while (path.size() > 0 && split
               && path.get(path.size() - 1).getNode().isFull()) {
            if (path.get(path.size() - 1).indexInParent() == -1) {
               split = this.overflowTreatment(path);
               if (split) {
                  this.increaseOverflowTreatmentTable();
               }
            } else {
               split = this.overflowTreatment(path);
            }
         }

         return true;
      } else {
         final NodeEntryPair chosen = this
               .chooseSubtree(n, toAdd.getMBBox());
         path.add(chosen);
         return this.insert(path, toAdd, level);
      }
   }

   protected int rootSplitsWhileReInsert = 0;

   /**
    * When after an insert the Node is full, overflow treatment is called to
    * determine, whether the Entry elements in this Node should be splitted or
    * p of them re-inserted. Returns the father of the splitted Node in order
    * to propagate overflowTreatment upwards.
    *
    * @param path the path to the full node
    * @see {@link #reInsert(ArrayList<NodeEntryPair>)}
    * @see {@link #createSplitDistribution(Node)}
    * @see {@link #split(ArrayList<NodeEntryPair>)}
    * @see {@link #createOverflowTreatmentTable()}
    * @see {@link #rootSplitsWhileReInsert}
    */
	/*
	 * true: split; false: reInsert
	 */
   protected boolean overflowTreatment(final ArrayList<NodeEntryPair> path) {

      final int nodeLevel = path.size() - 1;
      if (path.get(nodeLevel).indexInParent() != -1
            && !this.overflowTreatment[nodeLevel]) {
         this.overflowTreatment[nodeLevel] = true;
         this.reInsert(path);
         return false;
      } else {
         this.overflowTreatment[nodeLevel] = true;
         if (path.get(nodeLevel).indexInParent() == -1) {
            this.rootSplitsWhileReInsert++;
         }
         this.split(path);

         return true;
      }
   }

   /**
    * Sorts the <code>Entry</code> elements of the node denoted by
    * <code>path</code> in increasing to the center of the nodes parent entry
    * and re-insert the last {@link #p} Entry elements
    *
    * @param path Path to the node which entries shall be sorted an reinserted
    * @see {@link NDRectangle#quadraticCenterDistance(NDRectangle)}
    */
   protected void reInsert(final ArrayList<NodeEntryPair> path) {

		/*
		 * path to the node is path
		 */
      final int nodeLevel = path.size() - 1;
      final NodeEntryPair np = path.get(nodeLevel);
      final Node n = np.getNode();

      this.sortByDistance(
            path.get(nodeLevel - 1).getNode().get(np.indexInParent()), n);

      final int P = (int) (n.getMaxCapacity() * this.p);
      final ArrayList<Entry> removed = new ArrayList<Entry>(P);

      for (int i = 0; i < P; i++) {
         removed.add(n.removeLast());
      }

      this.storeNode(n);

      this.adjustNode(path);

      this.rootSplitsWhileReInsert = 0;
		/*
		 * close reinsert
		 */
      for (int i = P - 1; i >= 0; i--) {

         final ArrayList<NodeEntryPair> insertPath = this.createNewPath();

         this.insert(insertPath, removed.get(i), nodeLevel
               + this.rootSplitsWhileReInsert);
      }
   }

   /**
    * Sort the Entry elements in the Node given by <code>node</code> by the
    * distance of their center to the center of the parent Entry given by
    * <code>parentEntry</code>
    *
    * @param node        contains the <code>Node</code> which entries should be sorted
    *                    by the center distance to the corresponding <code>Entry</code>
    *                    in <code>parentEntry</code>
    * @param parentEntry parent <code>Entry</code> of Node
    * @see {@link NDRectangle#quadraticCenterDistance(NDRectangle)}
    */
   protected void sortByDistance(final Entry parentEntry, final Node node) {

      final double[] distances = new double[node.size()];

      for (int i = 0; i < node.size(); i++) {
         distances[i] = parentEntry.getMBBox().quadraticCenterDistance(
               node.get(i).getMBBox());

      }
      node.sort(distances);
   }

   /**
    * Sort the <code>Entry</code> elements in this node by a specific value
    * from its bounding box.
    *
    * @param n       <code>Node</code> to be sorted
    * @param dim     dimension to use for sorting
    * @param greater if <code>true</code>, the ending value of the interval in
    *                dimension <code>dim</code> will be used for sorting
    * @see {@link NDRectangle#compareTo(int, boolean, NDRectangle)}
    */
   protected void sortByValue(final Node n, final int dim,
                              final boolean greater) {

      n.sort(new EntryComparator(dim, greater));

   }
}