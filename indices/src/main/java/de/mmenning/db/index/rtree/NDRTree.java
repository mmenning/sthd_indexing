package de.mmenning.db.index.rtree;

import de.mmenning.db.index.NDRectangle;
import de.mmenning.db.index.NDRectangleKey;
import de.mmenning.db.index.NDRectangleKeyIndex;
import de.mmenning.db.index.RectangleQuery;
import de.mmenning.db.storage.*;
import de.mmenning.util.FixedArrayList;

import java.util.*;

/**
 * NDRTree. Basic implementation of an R-Tree. Uses Quadratic Split by
 * default. Splitting algorithm may be changed to Linear Split by
 * <code>setQuadraticSplit</code>.
 *
 * @author Mathias Menninghaus (mathias.menninghaus@uos.de)
 * @article R-Trees: A Dynamic Index Structure for Spatial Searching (Guttman,
 * 1984)
 */
public class NDRTree implements NDRectangleKeyIndex {

   protected int treeHeight = 1;
   /**
    * Minimum Number of maximum entries
    */
   public static final int MIN_MAX_ENTRIES = 4;
   /**
    * Number of dimensions in this NDRTree
    */
   private final int dimensions;
   /**
    * Maximum Capacity of the Nodes in this NDRTree
    */
   private final int maxCapacity;
   /**
    * Minimum Capacity of the Nodes in this NDRTree
    */
   private final double minFanout;
   /**
    * Root Node of this NDRTree
    */
   protected ObjectReference root;
   /**
    * Number of {@link NDRectangle NDRectangle} elements in this NDRTree
    */
   protected int size;
   /**
    * Determines how many times the Root has split (thus, how many times the
    * height of the tree has been increased) while one cycle of
    * {@link #condenseTree(ArrayList)}. Thus
    * <code>rootSplitsCondenseTree</code> is set to zero at the beginning of
    * {@link #condenseTree(ArrayList)} and increased within every call
    * {@link #split(ArrayList)}.
    */
   protected int rootSplitsWhileCondenseTree = 0;
   /**
    * determines, if the quadratic odr linear splitting algorithm is used.
    */
   private boolean quadraticSplit = true;

   public NDRTree(final int blockSize, final int dimensions, StorageManager s) {
      this(blockSize / (16 * dimensions + IOUtils.referenceByteSize()), 0.5,
            dimensions, s);

   }

   public NDRTree(final int blockSize, final int dimensions) {

      this(blockSize, dimensions, DefaultStorage.getInstance());
   }

   public NDRTree(final int maxCapacity, final double minFanout,
                  final int dimensions) {
      this(maxCapacity, minFanout, dimensions, DefaultStorage.getInstance());
   }

   /**
    * Constructs NDRTree
    *
    * @param maxCapacity maximum Capacity of a single Node in the tree.
    * @param minFanout   minimum Capacity of a single Node in the tree. Must be between
    *                    0 and maxCapacity/2
    * @param dimensions  number of dimensions in this tree
    * @throw IllegalArgumentException if maxCapacity is smaller than
    * MIN_MAX_ENTRIES, or if minCapacity is not between 0 and
    * maxCapacity/2, or if dimensions is <2
    */
   public NDRTree(final int maxCapacity, final double minFanout,
                  final int dimensions, StorageManager s) {

      if (maxCapacity < MIN_MAX_ENTRIES) {
         throw new IllegalArgumentException(
               "Maximum number of Entries too small (" + maxCapacity + "<"
                     + MIN_MAX_ENTRIES + ")");
      }
      if (minFanout < 0 || minFanout > 0.5) {
         throw new IllegalArgumentException(
               "Minimum fanout must be between 0 and 0.5");
      }
      if (dimensions < 2) {
         throw new IllegalArgumentException(
               "Must not have lesser than 2 dimensions");
      }

      this.dimensions = dimensions;
      this.maxCapacity = maxCapacity;
      this.minFanout = minFanout;

      Node rootNode = new Node(this.maxCapacity);

      this.ioacc = s;

      this.root = rootNode.getObjectReference();
      this.storeNode(rootNode);

   }

   /**
    * Determines whether this tree contains element <code>b</code> or not
    *
    * @param b element to be searched for
    * @return <code>true</code>, if this tree contains <code>b</code>
    */
   @Override
   public boolean contains(final NDRectangleKey b) {
      ArrayList<NodeEntryPair> path = this.createNewPath();
      return this.findLeaf(path, b) != -1;
   }

   /**
    * Deletes element <code>b</code> from this tree
    *
    * @param b element to be deleted
    * @return <code>true</code>, if the element has successfully been deleted.
    */
   @Override
   public boolean delete(final NDRectangleKey b) {

      ArrayList<NodeEntryPair> pathToLeaf = this.createNewPath();

      int posToDelete = this.findLeaf(pathToLeaf, b);

      if (posToDelete == -1) {
         return false;
      } else {

         Node leaf = pathToLeaf.get(pathToLeaf.size() - 1).getNode();

         if (!leaf.isLeaf()) {
            throw new RuntimeException("Should be a leaf!");
         }

         if (null == leaf.removeEntry(posToDelete)) {
            throw new RuntimeException("Entry not deleted");
         }

         this.storeNode(leaf);

         this.condenseTree(pathToLeaf);

         Node rootNode = getRoot();

         if (rootNode.size() == 1) {
            if (!rootNode.isLeaf()) {
               this.deleteNode(rootNode);
               this.root = rootNode.get(0).getChild();
               this.treeHeight--;
            }
         }
         this.size--;

         return true;
      }
   }

   /**
    * Get all Keys inserted in this tree.
    *
    * @return a set of all key elements
    */
   public Set<NDRectangleKey> getAll() {
      final HashSet<NDRectangleKey> s = new HashSet<NDRectangleKey>(this.size);
      this.traverseKeys(this.getRoot(), s);
      return s;
   }

   public void getAll(final Collection<ObjectReference> result) {
      this.traverse(this.getRoot(), result);
   }

   /**
    * Return a set of all Node elements in this tree
    *
    * @return all Node elements in this tree
    */
   public Set<Node> getAllNodes() {
      final HashSet<Node> s = new HashSet<Node>();
      this.traverseNodes(this.getRoot(), s);
      return s;
   }

   @Override
   public void getContained(NDRectangle region, RectangleQuery q) {
      final ArrayList<NodeEntryPair> path = this.createNewPath();
      int current = path.size() - 1;

      while (path.size() != 0) {
         Node n = this.readNode(path.remove(current--).getNodeReference());

         if (n.isLeaf()) {
            /*
             * if leaf
				 */
            for (int i = 0; i < n.size(); i++) {

               if (region.contains(n.get(i).getMBBox())) {

                  if (!q.query(new NDRectangleKey(n.get(i).getChild(), n
                        .get(i).getMBBox()))) {
                     return;
                  }
               }
            }

         } else {
            for (int i = 0; i < n.size(); i++) {
               if (region.intersects(n.get(i).getMBBox())) {

                  path.add(new NodeEntryPair(i, n.get(i).getChild()));
                  current++;
               }
            }
         }
      }
   }

   @Override
   public int getDim() {
      return this.dimensions;
   }

   /**
    * Returns the height of this tree.
    *
    * @return the height of this tree
    */
   public int getHeight() {
      return this.treeHeight;
   }

   @Override
   public void getIntersected(NDRectangle region, RectangleQuery q) {
      final ArrayList<NodeEntryPair> path = this.createNewPath();
      int current = path.size() - 1;

      while (path.size() != 0) {
         Node n = this.readNode(path.remove(current--).getNodeReference());

         if (n.isLeaf()) {
            /*
             * if leaf
				 */
            for (int i = 0; i < n.size(); i++) {

               if (region.intersects(n.get(i).getMBBox())) {

                  if (!q.query(new NDRectangleKey(n.get(i).getChild(), n
                        .get(i).getMBBox()))) {
                     return;
                  }
               }
            }

         } else {
            for (int i = 0; i < n.size(); i++) {
               if (region.intersects(n.get(i).getMBBox())) {

                  path.add(new NodeEntryPair(i, n.get(i).getChild()));
                  current++;
               }
            }
         }
      }
   }

   /**
    * Maximum Capacity of this tree
    *
    * @return Maximum Capacity of this tree
    */
   public int getMaxCapacity() {
      return this.maxCapacity;
   }

   /**
    * Minimum Capacity of this tree
    *
    * @return Minimum Capacity of this tree
    */
   public double getMinFanout() {
      return this.minFanout;
   }

   /**
    * Returns the root of this tree.
    *
    * @return the root of this tree
    */
   public Node getRoot() {
      return this.readNode(this.root);
   }

   /**
    * Inserts <code>b</code> into this tree </br> *
    *
    * @param b element to be inserted
    * @return <code>true</code>, if <code>b</code> has succesfully been
    * inserted
    */
   @Override
   public boolean insert(final NDRectangleKey b) {

      ArrayList<NodeEntryPair> path = this.createNewPath();
      if (this.insert(path, new Entry(b), this.getLeafLevel())) {
         this.size++;
         return true;
      } else {
         return false;
      }
   }

   /**
    * Returns the size of this tree
    *
    * @return number of Elements in the tree
    */
   @Override
   public int size() {
      return this.size;
   }

   /**
    * Adjusts the bounding boxes of every <code>parentEntry</code> in the given
    * <code>ArrayList</code> of <code>NodeEntryPair</code>.
    *
    * @param path List of <code>NodeEntryPairs</code> which should be adjusted
    */
   protected void adjustNode(final ArrayList<NodeEntryPair> path) {
      /*
       * through the path from bottom to top, except the root which has no
		 * parent entry
		 */
      Node n = path.get(path.size() - 1).getNode();

      for (int i = (path.size() - 1); i > 0; i--) {

         int e = path.get(i).indexInParent();
         Node p = path.get(i - 1).getNode();

         if (e != -1) {
            p.get(e).setMBBox(this.unionEntries(n, 0, n.size()));
         }

         this.storeNode(p);

         n = p;
      }
   }

   /**
    * Adjust the bounding boxes of every <code>parentEntry</code> in the given
    * <code>ArrayList</code> of <code>NodeEntryPair</code> elements by unioning
    * it with the given <code>mbbox</code>.
    *
    * @param path  List of <code>NodeEntryPairs</code> which should be adjusted
    * @param mbbox Bounding Box by which every <code>Entry</code> in
    *              <code>path</code> should be enhanced with.
    */
   protected void adjustNode(final ArrayList<NodeEntryPair> path,
                             final NDRectangle mbbox) {

      for (int i = (path.size() - 1); i > 0; i--) {
         int e = path.get(i).indexInParent();
         Node p = path.get(i - 1).getNode();

         if (e != -1) {
            p.get(e).setMBBox(p.get(e).getMBBox().union(mbbox));
         }
			/*
			 * Update _parent_ node on disk
			 */
         this.storeNode(p);
      }
   }

   /**
    * Choose a subtree of Node n at level level which is the best of all Node
    * elements in n to insert mbbox
    *
    * @param n     Node in which should be searched for the best Subtree
    * @param mbbox M to be inserted
    * @return the path to the best <code>Node</code> to insert
    * <code>mbbox</code> into.
    */
   protected NodeEntryPair chooseSubtree(final Node n, final NDRectangle mbbox) {

      double bestEnlargement = Double.POSITIVE_INFINITY;
      double bestArea = Double.POSITIVE_INFINITY;
      int bestIndex = -1;

      double enlargement;
      double area;

      for (int index = 0; index < n.size(); index++) {
         enlargement = this.computeAreaEnlargement(n, index, mbbox);
         if (enlargement < bestEnlargement) {
            bestEnlargement = enlargement;
            bestArea = n.get(index).getMBBox().volume();
            bestIndex = index;
         } else if (enlargement == bestEnlargement) {
            area = n.get(index).getMBBox().volume();
            if (area <= bestArea) {
               bestEnlargement = enlargement;
               bestArea = area;
               bestIndex = index;
            }
         }
      }
      if (bestIndex == -1) {
			/*
			 * if no index has been considered to be the best, choose it
			 * randomly.
			 */
         bestIndex = (int) (Math.random() * n.size());
      }

      return new NodeEntryPair(bestIndex, n.get(bestIndex).getChild());

   }

   /**
    * Computes the area enlargement of elements in Node n by inserting a new
    * bounding box into the Entry at position index.
    *
    * @param n     Node of which the area enlargement should be calculated
    * @param index position of the Entry which should be expanded by mbbox
    * @param mbbox minimum bounding box to expand the Entry at position index
    *              with
    * @return the area enlargement of the Entry at position index when
    * expanding with mbbox
    */
   protected double computeAreaEnlargement(final Node n, final int index,
                                           final NDRectangle mbbox) {
      return (n.get(index).getMBBox().union(mbbox)).volume()
            - n.get(index).getMBBox().volume();
   }

   /**
    * After an element has been deleted, condenseTree is called to adjust the
    * corresponding leaf node which is denoted by <code>path</code>.
    * Recursively goes upwards and reinserts the entries of underfull nodes.
    * The underfull nodes will be deleted.Uses
    * {@link #rootSplitsWhileCondenseTree} to determine the level for
    * reinserting entries from underfull nodes.
    *
    * @param path path to leaf-node l in which an element has been deleted
    * @see {@link #delete(NDRectangleKey)}
    * @see {@link #rootSplitsWhileCondenseTree}
    */
   protected void condenseTree(ArrayList<NodeEntryPair> path) {

      Node n = path.get(path.size() - 1).getNode();
      final LinkedList<Entry> deleted = new LinkedList<Entry>();
      int currentLevel = path.size() - 1;

      Node p;
      final LinkedList<Integer> levelOfDeleted = new LinkedList<Integer>();

      while (path.get(currentLevel).indexInParent() != -1) {

         p = path.get(currentLevel - 1).getNode();
         int minCapacity = (int) (n.getMaxCapacity() * this.getMinFanout());

         if (n.size() < minCapacity) {

            // remove entries for reinsert
            for (int i = 0; i < n.size(); i++) {
               deleted.add(n.get(i));
               levelOfDeleted.add(currentLevel);
            }

            int toRemove = path.get(currentLevel).indexInParent();

            // remove the node
            if (p.removeEntry(toRemove) == null) {
               throw new RuntimeException("An Entry does not exist: "
                     + toRemove);
            }
				/*
				 * Update parent node, delete removed node on disk
				 */
            this.deleteNode(n);
            this.storeNode(p);

         } else {
				/*
				 * parentEntry of n, only one step of adjustTree
				 */
            p.get(path.get(currentLevel).indexInParent()).setMBBox(
                  this.unionEntries(n, 0, n.size()));
				/*
				 * Update the parent node
				 */
            this.storeNode(p);
         }
         currentLevel--;
         n = p;
      }

      this.rootSplitsWhileCondenseTree = 0;

      Iterator<Integer> iter = levelOfDeleted.iterator();
      for (final Entry e : deleted) {

			/*
			 * update of the nodes in which the entries have been reinserted
			 * will be done by the insert methods.
			 */

         ArrayList<NodeEntryPair> newPath = this.createNewPath();

         if (!this.insert(newPath, e, iter.next()
               + this.rootSplitsWhileCondenseTree)) {
            throw new RuntimeException("ReInsert failed: " + e);
         }
      }
   }

   /**
    * Creates a distribution of the elements in Node n by using a linear cost
    * algorithm. First chooses seed Entry elements for the two resulting Node
    * elements, then fills them.
    *
    * @param n Node from which the Distribution should be generated. It will
    *          not be changed.
    * @return Resulting CandidateDistribution
    * @see {@link #linearPickSeeds(CandidateDistribution, FixedArrayList)}
    * @see {@link #linearDistribute(CandidateDistribution, FixedArrayList)}
    */
   protected CandidateDistribution createLinearCandidateDistribution(
         final Node n) {
      final CandidateDistribution c = new CandidateDistribution();

      c.firstNode = new ArrayList<Entry>();
      c.secondNode = new ArrayList<Entry>();

      final FixedArrayList<Entry> s = n.getAll();

		/*
		 * linear PickSeeds
		 */
      this.linearPickSeeds(c, s);

		/*
		 * linear split
		 */
      this.linearDistribute(c, s);

      return c;
   }

   /**
    * Creates a path to a <code>Node</code> as an <code>ArrayList</code>
    * <code>NodeEntryPair</code> elements starting with the root element of
    * this tree. The <code>ArrayList</code> has a initial capacity of the
    * height of this tree.
    *
    * @return a path starting with the root of this tree.
    */
   protected ArrayList<NodeEntryPair> createNewPath() {
      ArrayList<NodeEntryPair> path = new ArrayList<NodeEntryPair>(
            this.treeHeight);
      path.add(new NodeEntryPair(-1, this.root));
      return path;
   }

   /**
    * Creates a distribution of the elements in Node n by using the a
    * qudratic-cost algorithm. First chooses seed Entry elements for the two
    * resulting Node elements, then fills them.
    *
    * @param n Node from which the Distribution should be generated. It will
    *          not be changed.
    * @return Resulting CandidateDistribution
    * @see {@link #quadraticPickSeeds(CandidateDistribution, FixedArrayList)}
    * @see {@link #quadraticDistribute(CandidateDistribution, FixedArrayList)}
    */
   protected CandidateDistribution createQuadraticCandidateDistribution(
         final Node n) {

      final CandidateDistribution c = new CandidateDistribution();

      c.firstNode = new ArrayList<Entry>();
      c.secondNode = new ArrayList<Entry>();

      final FixedArrayList<Entry> s = n.getAll();

		/*
		 * quadratic PickSeeds
		 */
      this.quadraticPickSeeds(c, s);

		/*
		 * quadratic split
		 */
      this.quadraticDistribute(c, s);

      return c;
   }

   /**
    * Set the splitting algorithm to Quadratic Split (<code>true</code>) or
    * Linear Split (<code>false</code>). By default, the Quadratic Split is
    * used.
    *
    * @param quadraticSplit set the splitting algorithm to Quadratic Split
    */
   public void setQuadraticSplit(boolean quadraticSplit) {

      this.quadraticSplit = quadraticSplit;

   }

   /**
    * Determine the best Candidate Distribution to split Node n. If using
    * another split distribution, overriding classes should override this
    * method. The method {@link #split(ArrayList)} calls this method in order
    * to determine which <code>CandidateDistribution</code> should be used.
    *
    * @param n <code>Node</code> to be splitted. It will and should not be
    *          changed during the split
    * @return <code>CandidateDistribution</code> of the splitted
    * <code>Node</code>
    * @see {@link #split(ArrayList<NodeEntryPair>)}
    */
   protected CandidateDistribution createSplitDistribution(final Node n) {

      final CandidateDistribution c =
            quadraticSplit ? this
                  .createQuadraticCandidateDistribution(n) : this
                  .createLinearCandidateDistribution(n);

      return c;
   }

   /**
    * Finds the position of the leaf <code>Entry</code> which contains
    * <code>element</code>. The parameter <code>path</code> will contain the
    * path to that <code>Entry</code>. Starting with the node-element where the
    * search started and ending with the leaf-node which contains
    * <code>element</code> at the returned position.
    *
    * @param path    Path in which should be searched. Will start the search with
    *                the last entry in <code>path</code> as root-node. At the end
    *                of the operation <code>path</code> will contain the path to
    *                the node which entry at the returned postion contains
    *                <code>element</code> or the path with which it has started.
    * @param element the element to search for
    * @return the position of the entry element which holds
    * <code>element</code> or -1 if it has not been found.
    */
   protected int findLeaf(final ArrayList<NodeEntryPair> path,
                          final NDRectangleKey element) {

      Node n = path.get(path.size() - 1).getNode();

      if (n.isLeaf()) {
         for (int i = 0; i < n.size(); i++) {
            Entry e = n.get(i);
            if (new NDRectangleKey(e.getChild(), e.getMBBox())
                  .equals(element)) {
               return i;
            }
         }
         return -1;
      } else {
         for (int i = 0; i < n.size(); i++) {

            if (n.get(i).getMBBox().contains(element.getNDKey())) {

               path.add(new NodeEntryPair(i, n.get(i).getChild()));

               int newPath = this.findLeaf(path, element);

               if (newPath != -1) {
                  return newPath;
               } else {
                  path.remove(path.size() - 1);
               }
            }
         }
         return -1;
      }
   }

   /**
    * Add all <code>NDRectangle</code> elements in the leaf <code>Node</code>
    * elements of <code>Node</code> <code>n</code> which are not intersected by
    * <code>bbox</code> to <code>Collection</code> <code>c</code>.
    *
    * @param n    <code>Node</code> in which the elements are searched
    * @param bbox Minimum Bounding Box to search disjoining <code>Node</code>
    *             elements
    * @param c    <code>Collection</code> where the not intersecting
    *             <code>NDRectangle</code> elements will be added
    */
   protected void getDisjoining(final Node n, final NDRectangle bbox,
                                final Collection<ObjectReference> c) {
      if (n.isLeaf()) {
         for (int i = 0; i < n.size(); i++) {
            if (n.get(i).getMBBox().disjoins(bbox)) {
               c.add(n.get(i).getChild());
            }
         }
      } else {
         for (int i = 0; i < n.size(); i++) {
            if (n.get(i).getMBBox().disjoins(bbox)) {
               this.traverse(this.readNodeChild(n.get(i)), c);
            } else {
               this.getDisjoining(this.readNodeChild(n.get(i)), bbox, c);
            }
         }
      }
   }

   /**
    * Get the last or the leaf level in this tree.
    *
    * @return number of the leaf level
    */
   protected int getLeafLevel() {
      return this.treeHeight - 1;
   }

   /**
    * Add all NDRectangle elements in the leaf Node elements of Node n which
    * overlap bbox to Collection c.
    *
    * @param n    <code>Node</code> in which the elements are searched
    * @param bbox Minimum Bounding Box to search overlapping <code>Node</code>
    *             elements
    * @param c    Collection where the bbox overlapping <code>NDRectangle</code>
    *             elements will be added.
    */
   protected void getOverlapping(final Node n, final NDRectangle bbox,
                                 final Collection<ObjectReference> c) {
      if (n.isLeaf()) {
         for (int i = 0; i < n.size(); i++) {
            if (n.get(i).getMBBox().intersects(bbox)) {
               c.add(n.get(i).getChild());
            }
         }
      } else {
         for (int i = 0; i < n.size(); i++) {
            if (n.get(i).getMBBox().intersects(bbox)) {
               this.getOverlapping(this.readNodeChild(n.get(i)), bbox, c);
            }
         }
      }
   }

   /**
    * Inserts a <code>Entry</code> to <code>Node</code> which is given by
    * <code>path</code> at a given <code>level</code>. If the <code>Node</code>
    * , in which the <code>Entry</code> has been added, is full, a split on the
    * <code>Node</code> will be performed and propagated upwards. At the end
    * <code>path</code> will contain the path to the <code>Node</code> in which
    * <code>toAdd</code> has been added. NOTE: WILL NOT TEST IF THE ENTRIES
    * CHILD IS ALREADY CONTAINED IN THIS NODE.
    *
    * @param path  the path where the insertion begins
    * @param toAdd <code>Entry</code> which should be added.
    * @param level level at which the <code>Entry</code> should be added.
    * @return <code>true</code> if the <code>Entry</code> has been inserted
    * succesfully.
    * @see {@link #adjustNode(ArrayList<NodeEntryPair>, NDRectangle)}
    * @see {@link #split(ArrayList<NodeEntryPair>)}
    */
   protected boolean insert(ArrayList<NodeEntryPair> path, final Entry toAdd,
                            final int level) {

      Node n = path.get(path.size() - 1).getNode();

      if (n.isLeaf() || path.size() - 1 == level) {

         n.addEntry(toAdd);
         this.storeNode(n);

         this.adjustNode(path, toAdd.getMBBox());

         while (path.size() > 0 && n.isFull()) {
            this.split(path);
            if (path.size() > 0) {
               n = path.get(path.size() - 1).getNode();
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

   /**
    * Distributes the elements in <code>s</code> into the two Node elements in
    * <code>c</code>. NOTE: <code>c</code> must contain seed-elements. At the
    * end, <code>c</code>is filled with the elements in <code>s</code> and
    * <code>s</code> is empty. Uses {@link #linearPickNext
    * (CandidateDistribution, FixedArrayList<Entry>)} to
    * put the next Entry from <code>s</code> into the CandidateDistribution.
    *
    * @param c start and result of the linear distribution algorithm
    * @param s Collection of Entry elements to be distributed
    * @see {@link #linearPickNext(CandidateDistribution, FixedArrayList<Entry>)}
    */
   protected void linearDistribute(final CandidateDistribution c,
                                   final FixedArrayList<Entry> s) {
      if (c.firstNode.size() == 0 || c.secondNode.size() == 0) {
         throw new IllegalArgumentException("No seed-Elements detected");
      }
      while (!s.isEmpty()) {

         int minCapacity = (int) (s.capacity() * this.getMinFanout());

         if (s.size() == (minCapacity - c.firstNode.size())) {
            while (!s.isEmpty()) {
               final Entry add = s.removeLast();
               c.firstNode.add(add);
               c.coveringFirst = c.coveringFirst.union(add.getMBBox());
            }
         } else if (s.size() == (minCapacity - c.secondNode.size())) {
            while (!s.isEmpty()) {
               final Entry add = s.removeLast();
               c.secondNode.add(add);
               c.coveringSecond = c.coveringSecond.union(add.getMBBox());
            }
         } else {
				/*
				 * linear pick next (choose any Node as Next)
				 */
            this.linearPickNext(c, s);
         }
      }
   }

   /**
    * Picks the next element form <code>s</code> and puts it into one of the
    * Node elements in <code>c</code>. Removes the chosen elements from
    * <code>s</code>. Updates covering boxes in <code>c</code>.
    *
    * @param c start and result of the linear pick next algorithm
    * @param s Collection of Entry elements from which the next one should be
    *          picked.
    */
   protected void linearPickNext(final CandidateDistribution c,
                                 final FixedArrayList<Entry> s) {

      double bestD1 = Double.NaN;
      double bestD2 = Double.NaN;

      final double firstVolume = c.coveringFirst.volume();
      final double secondVolume = c.coveringSecond.volume();

      bestD1 = c.coveringFirst.union(s.getLast().getMBBox()).volume()
            - firstVolume;
      bestD2 = c.coveringSecond.union(s.getLast().getMBBox()).volume()
            - secondVolume;

		/*
		 * put next Entry into Node, which rectangle has to be enlarged least,
		 * resolve ties by adding to the Node with smaller area, then to the one
		 * with fewer entries, then to either
		 */
      final ArrayList<Entry> prev;

      if (bestD1 < bestD2) {
         prev = c.firstNode;
      } else if (bestD2 < bestD2) {
         prev = c.secondNode;
      } else {
         if (firstVolume < secondVolume) {
            prev = c.firstNode;
         } else if (secondVolume < firstVolume) {
            prev = c.secondNode;
         } else {
            if (c.firstNode.size() <= c.secondNode.size()) {
               prev = c.firstNode;
            } else {
               prev = c.secondNode;
            }
         }
      }
		/*
		 * add to next element to preferred Node
		 */
      final Entry add = s.removeLast();
      prev.add(add);

      if (prev == c.firstNode) {
         c.coveringFirst = c.coveringFirst.union(add.getMBBox());
      } else {
         c.coveringSecond = c.coveringSecond.union(add.getMBBox());
      }
   }

   /**
    * Pick the seed entries from s and put them into c. Remove seed elements
    * from s. Update covering boxes in c. Node elements in c should be empty.
    *
    * @param c CandidateDistribution which should be filled with the seed
    *          elements
    * @param s Collection of Entry elements to chose the seed elements from
    */
   protected void linearPickSeeds(final CandidateDistribution c,
                                  final FixedArrayList<Entry> s) {

      if (!c.firstNode.isEmpty() || !c.secondNode.isEmpty()) {
         throw new IllegalArgumentException(
               "Seed Entry elements already in CandidateDistribution");
      }

      int maxLowIndex = -1;
      int minHighIndex = -1;

      int bestMaxLowIndex = -1;
      int bestMinHighIndex = -1;

      double maxLow = Double.NEGATIVE_INFINITY;
      double minHigh = Double.POSITIVE_INFINITY;

      double minLow = Double.POSITIVE_INFINITY;
      double maxHigh = Double.NEGATIVE_INFINITY;

      double normalizedSep = Double.NaN;
      double bestNormalizedSep = Double.NEGATIVE_INFINITY;

      double min, max;

		/*
		 * Along each dimension find the entry whose rectangle has the highest
		 * low side, and then the one with the lowest high side. Record the
		 * separation. Normalize the separations by dividing by the width of the
		 * entire set along the corresponding dimension. Choose the pair with
		 * greatest normalized separation along any dimension as the seed
		 * Entries
		 */
      for (int dim = 0; dim < this.dimensions; dim++) {

         maxLowIndex = -1;
         minHighIndex = -1;

         maxLow = Double.NEGATIVE_INFINITY;
         minHigh = Double.POSITIVE_INFINITY;
         minLow = Double.POSITIVE_INFINITY;
         maxHigh = Double.NEGATIVE_INFINITY;

         for (int index = 0; index < s.size(); index++) {
            min = s.get(index).getMBBox().getBegin().getValue(dim);

            max = s.get(index).getMBBox().getEnd().getValue(dim);

            if (min < minLow) {
               minLow = min;
            }

            if (min > maxLow) {
               maxLow = min;
               maxLowIndex = index;
            }

            if (max > maxHigh) {
               maxHigh = max;
            }

            if (max < minHigh) {
               minHigh = max;
               minHighIndex = index;
            }
         }
         if (maxLowIndex == -1) {
            maxLowIndex = 0;
         }
         if (minHighIndex == -1) {
            minHighIndex = 1;
         }

         normalizedSep = s.get(maxLowIndex).getMBBox()
               .separation(s.get(minHighIndex).getMBBox(), dim)
               / Math.abs(maxHigh - minLow);

         // System.out.println("maxHigh "+maxHigh);
         // System.out.println("minLow "+minLow);
         // System.out.println("normalizedSep "+normalizedSep);

         if (Double.compare(normalizedSep, bestNormalizedSep) > 0) {
            bestMaxLowIndex = maxLowIndex;
            bestMinHighIndex = minHighIndex;
            bestNormalizedSep = normalizedSep;
         }
      }

      if (bestMaxLowIndex == bestMinHighIndex) {
			/*
			 * in the very unlikely case that every normalized separation is the
			 * same, thus maxLowIndex and minHighIndex are the same in every
			 * dimension, choose the last two elements as new seeds.
			 */

         final Entry bestFirstEntry = s.removeLast();
         final Entry bestSecondEntry = s.removeLast();

         c.firstNode.add(bestFirstEntry);
         c.secondNode.add(bestSecondEntry);

         c.coveringFirst = bestFirstEntry.getMBBox();
         c.coveringSecond = bestSecondEntry.getMBBox();

      } else {

			/*
			 * add seeds to the Node elements in the CandidateDistribution
			 */
         final Entry bestMaxLowEntry = s.get(bestMaxLowIndex);
         final Entry bestMinHighEntry = s.get(bestMinHighIndex);

         c.firstNode.add(bestMaxLowEntry);
         c.secondNode.add(bestMinHighEntry);

         if (!s.remove(bestMaxLowEntry) || !s.remove(bestMinHighEntry)) {
            throw new RuntimeException(
                  "Could not remove inserted Entry from s");
         }

         c.coveringFirst = bestMaxLowEntry.getMBBox();
         c.coveringSecond = bestMinHighEntry.getMBBox();
      }
   }

   /**
    * Distributes the elements in <code>s</code> into the two Node elements in
    * <code>c</code>. NOTE: <code>c</code> must contain seed-elements. At the
    * end, <code>c</code> is filled with the elements in <code>s</code>s and
    * <code>s</code> is empty. Updates the covering boxes in <code>c</code>.
    * Uses {@link #linearPickNext(CandidateDistribution, FixedArrayList)
    * quadraticPickNext} to put the next Entry from s into the
    * CandidateDistribution.
    *
    * @param c start and result of the linear distribution algorithm
    * @param s Collection of Entry elements to be distributed
    * @see {@link #quadraticPickNext(CandidateDistribution, FixedArrayList)}
    */
   protected void quadraticDistribute(final CandidateDistribution c,
                                      final FixedArrayList<Entry> s) {

      if (c.firstNode.isEmpty() || c.secondNode.isEmpty()) {
         throw new IllegalArgumentException("No seed-Elements detected");
      }

      while (!s.isEmpty()) {

         int minCapacity = (int) (s.capacity() * this.getMinFanout());

         if (s.size() == (minCapacity - c.firstNode.size())) {
            while (!s.isEmpty()) {
               final Entry add = s.removeLast();
               c.firstNode.add(add);
               c.coveringFirst = c.coveringFirst.union(add.getMBBox());
            }
         } else if (s.size() == (minCapacity - c.secondNode.size())) {
            while (!s.isEmpty()) {
               final Entry add = s.removeLast();
               c.secondNode.add(add);
               c.coveringSecond = c.coveringSecond.union(add.getMBBox());
            }
         } else {
				/*
				 * quadratic pick next
				 */
            this.quadraticPickNext(c, s);
         }
      }
   }

   /**
    * Picks the next element form <code>s</code> and puts it into one of the
    * Node elements in <code>c</code>. Remove the chosen elements from
    * <code>s</code>. Update covering boxes in <code>c</code>.
    *
    * @param c start and result of the linear pick next algorithm
    * @param s Collection of Entry elements from which the next one should be
    *          picked.
    */
   protected void quadraticPickNext(final CandidateDistribution c,
                                    final FixedArrayList<Entry> s) {

      double bestD1 = Double.NEGATIVE_INFINITY;
      double bestD2 = Double.NEGATIVE_INFINITY;
      double bestDiff = Double.NEGATIVE_INFINITY;
      int bestIndex = -1;

      double d1, d2, diff;

      final double firstVolume = c.coveringFirst.volume();
      final double secondVolume = c.coveringSecond.volume();

		/*
		 * For each entry E not yet m a group, calculate d1= the area increase
		 * required in the covering rectangle of Group 1 to include EI Calculate
		 * d2 similarly for Group 2 Choose any entry with the maximum difference
		 * between d1 and d2
		 */
      for (int i = 0; i < s.size(); i++) {
         d1 = c.coveringFirst.union(s.get(i).getMBBox()).volume()
               - firstVolume;
         d2 = c.coveringSecond.union(s.get(i).getMBBox()).volume()
               - secondVolume;
         diff = Math.abs(d1 - d2);

         if (diff >= bestDiff) {
            bestD1 = d1;
            bestD2 = d2;
            bestDiff = diff;
            bestIndex = i;
         }
      }

		/*
		 * put next Entry into Node, which rectangle has to be enlarged least,
		 * resolve ties by adding to the Node with smaller area, then to the one
		 * with fewer entries, then to either
		 */
      final ArrayList<Entry> prev;

      if (bestD1 < bestD2) {
         prev = c.firstNode;
      } else if (bestD2 < bestD2) {
         prev = c.secondNode;
      } else {
         if (firstVolume < secondVolume) {
            prev = c.firstNode;
         } else if (secondVolume < firstVolume) {
            prev = c.secondNode;
         } else {
            if (c.firstNode.size() <= c.secondNode.size()) {
               prev = c.firstNode;
            } else {
               prev = c.secondNode;
            }
         }
      }

      final Entry add = s.get(bestIndex);

      prev.add(add);

      s.remove(bestIndex);

		/*
		 * add to next element to preferred Node
		 */

      if (prev == c.firstNode) {
         c.coveringFirst = c.coveringFirst.union(add.getMBBox());
      } else {
         c.coveringSecond = c.coveringSecond.union(add.getMBBox());
      }
   }

   /**
    * Pick the seed entries from <code>s</code> and put them into
    * <code>c</code>. Remove seed elements from <code>s</code>. Update covering
    * boxes in <code>c</code>. Node elements in <code>c</code> should be empty.
    *
    * @param c CandidateDistribution which should be filled with the seed
    *          elements
    * @param s Collection of Entry elements to chose the seed elements from
    */
   protected void quadraticPickSeeds(final CandidateDistribution c,
                                     final FixedArrayList<Entry> s) {

      if (!c.firstNode.isEmpty() || !c.secondNode.isEmpty()) {
         throw new IllegalArgumentException(
               "Seed Entry elements already in CandidateDistribution");
      }

      NDRectangle J = null;

      int bestA = -1;
      int bestB = -1;
      double bestD = Double.NEGATIVE_INFINITY;
      double d;

		/*
		 * For each pair of entries A and B, compose a rectangle J including A
		 * and B. Calculate d = area(J)-area(A)-area(B). Choose the pair with
		 * the largest d.
		 */
      for (int i = 0; i < s.size(); i++) {
         for (int j = i + 1; j < s.size(); j++) {
            J = s.get(i).getMBBox().union(s.get(j).getMBBox());

            d = J.volume() - s.get(i).getMBBox().volume()
                  - s.get(j).getMBBox().volume();

            if (d > bestD) {
               bestD = d;
               bestA = i;
               bestB = j;
            }
         }
      }
      if (bestA == bestB) {
         throw new RuntimeException("bestA equals bestB: " + bestA);
      }

      final Entry bestAEntry = s.get(bestA);
      final Entry bestBEntry = s.get(bestB);

      c.firstNode.add(bestAEntry);
      c.secondNode.add(bestBEntry);

      if (!s.remove(bestAEntry)) {
         throw new RuntimeException(
               "Could not remove in first Node inserted Entry from s");
      }
      if (!s.remove(bestBEntry)) {
         throw new RuntimeException(
               "Could not remove in second Node inserted Entry from s");
      }

      c.coveringFirst = bestAEntry.getMBBox();
      c.coveringSecond = bestBEntry.getMBBox();
   }

   /**
    * Splits the Node which is denoted by <code>path</code> into two new Nodes.
    * Removes the Node from its father and puts the new Nodes into the former
    * father. If it has been the root, a new Root will be generated with the
    * new Nodes as its children. Increases {@link #rootSplitsWhileCondenseTree}
    * when the root has been splitted.
    *
    * @param path Path to the Node which has to be split
    * @return the father Node in which the new Nodes have been put in
    * @see {@link #createSplitDistribution(Node)}
    * @see {@link #rootSplitsWhileCondenseTree}
    */
   protected final void split(final ArrayList<NodeEntryPair> path) {

      final int nodeLevel = path.size() - 1;
      final Node n = path.get(nodeLevel).getNode();

      final CandidateDistribution c = this.createSplitDistribution(n);

      Node father;

      if (path.get(nodeLevel).indexInParent() == -1) {
         final Node newRoot = new Node(this.getMaxCapacity());
         this.deleteNode(this.root);
         this.root = newRoot.getObjectReference();
         father = newRoot;
         this.treeHeight++;
         this.rootSplitsWhileCondenseTree++;
      } else {
         father = path.get(nodeLevel - 1).getNode();
         if (father.removeEntry(path.get(nodeLevel).indexInParent()) == null) {
            throw new RuntimeException("Cannot remove splitted Node");
         }
      }

		/*
		 * original node will be first one, second node will be a new one
		 */
      final Node firstNode = n;
      while (n.size() > 0) {
         n.removeLast();
      }
      final Node secondNode = new Node(this.getMaxCapacity());

      for (final Entry e : c.firstNode) {
         firstNode.addEntry(e);
      }
      for (final Entry e : c.secondNode) {
         secondNode.addEntry(e);
      }

      this.storeNode(firstNode);
      this.storeNode(secondNode);

      father.addEntry(new Entry(c.coveringFirst, firstNode));
      father.addEntry(new Entry(c.coveringSecond, secondNode));

      this.storeNode(father);

      path.remove(path.size() - 1);
   }

   /**
    * Go through <code>Node</code> <code>n</code> and add all
    * <code>NDRectangle</code> elements contained in <code>n</code> or its
    * child <code>Node</code> elements
    *
    * @param n <code>Node</code> which should be traversed
    * @param s Resulting set of <code>NDRectangle</code> elements
    */
   protected void traverse(final Node n, final Collection<ObjectReference> s) {
      if (n.isLeaf()) {
         for (int i = 0; i < n.size(); i++) {
            if (!s.add(n.get(i).getChild())) {
               throw new RuntimeException(
                     "NDRectangle has already been inserted");
            }
         }
      } else {
         for (int i = 0; i < n.size(); i++) {
            this.traverse(this.readNodeChild(n.get(i)), s);
         }
      }
   }

   /**
    * Go through <code>Node</code> <code>n</code> and add all
    * <code>NDRectangleKey</code> elements contained in <code>n</code> or its
    * child <code>Node</code> elements
    *
    * @param n <code>Node</code> which should be traversed
    * @param s Resulting set of <code>NDRectangleKey</code> elements
    */
   protected void traverseKeys(final Node n, final Collection<NDRectangleKey> s) {
      if (n.isLeaf()) {
         for (int i = 0; i < n.size(); i++) {
            if (!s.add(new NDRectangleKey(n.get(i).getChild(), n.get(i)
                  .getMBBox()))) {
               throw new RuntimeException(
                     "Rectangle has already been inserted");
            }
         }
      } else {
         for (int i = 0; i < n.size(); i++) {
            this.traverseKeys(this.readNodeChild(n.get(i)), s);
         }
      }
   }

   /**
    * Go through <code>Node</code> <code>n</code> and add it and all child
    * <code>Node</code> elements to <code>s</code>
    *
    * @param n Node which should be traversed
    * @param s Resulting set of Node elements
    */
   protected void traverseNodes(final Node n, final Collection<Node> s) {
      if (n.isLeaf()) {
         if (!s.add(n)) {
            throw new RuntimeException("Node " + n + " already inserted");
         }
      } else {
         for (int i = 0; i < n.size(); i++) {
            this.traverseNodes(this.readNodeChild(n.get(i)), s);
         }
         if (!s.add(n)) {
            throw new RuntimeException("Node " + n + " already inserted");
         }
      }
   }

   /**
    * Unions <code>length</code> <code>Entry</code> elements in
    * <code>ArrayList</code> <code>s</code>, beginning with the
    * <code>Entry</code> at index <code>begin</code>
    *
    * @param s      <code>ArrayList</code> of <code>Entry</code> elements which
    *               should be unionend.
    * @param begin  index, at which the union should be started.
    * @param length number of Entry elements to be unionend
    * @return the union of the entries.
    */
   protected NDRectangle unionEntries(final ArrayList<Entry> s,
                                      final int begin, final int length) {
      NDRectangle covering = s.get(begin).getMBBox();
      for (int i = begin + 1; i < begin + length; i++) {
         covering = covering.union(s.get(i).getMBBox());
      }
      return covering;
   }

   /**
    * Unions <code>length</code> Entry elements in Node n, beginning with the
    * Entry at index begin
    *
    * @param n      Node which entries should be unionend.
    * @param begin  index, at which the union should be started.
    * @param length number of Entry elements to be unionend
    * @return the union of the entries.
    */
   protected NDRectangle unionEntries(final Node n, final int begin,
                                      final int length) {

      // System.out.println("union entries from "+begin+" to "+(+begin+length));
      NDRectangle result = n.get(begin).getMBBox();

      for (int i = begin + 1; i < begin + length; i++) {
			/*
			 * use union method of NDRectangle
			 */
         result = result.union(n.get(i).getMBBox());
      }
      return result;
   }

   @Override
   public boolean update(NDRectangleKey oldOne, NDRectangleKey newOne) {

      if (!this.delete(oldOne)) {
         return false;
      } else {
         return this.insert(newOne);
      }
   }

   /*-----------------------------------------------------------------------*/
   protected Node readNodeChild(Entry e) {
      return readNode(e.getChild());

   }

   protected Node readNode(ObjectReference or) {
      return (Node) this.ioacc.load(or).getObject();
   }

   protected void deleteNode(ObjectReference or) {
      ioacc.delete(or);
   }

   protected void deleteNode(Node n) {
      ioacc.delete(n.getObjectReference());
   }

   protected void storeNode(Node n) {
      ioacc.store(new SimpleStorable(n.getObjectReference(), n, this
            .getNodeByteSize(n)));
   }

   private StorageManager ioacc = DefaultStorage.getInstance();

   protected int getNodeByteSize(Node n) {
      return n.getMaxCapacity()
            * (IOUtils.referenceByteSize() + (this.getDim() * 8 * 2));
   }

   @Override
   public StorageManager getStorageManager() {
      return this.ioacc;
   }

	/*-----------------------------------------------------------------------*/

   public class NodeEntryPair {

      private final ObjectReference node;
      private final int indexInParent;

      public NodeEntryPair(int indexInParent, ObjectReference node) {
         if (node == null) {
            throw new NullPointerException("node must not be null");
         }
         this.node = node;
         this.indexInParent = indexInParent;

      }

      public Node getNode() {
         return readNode(this.node);
      }

      public ObjectReference getNodeReference() {
         return this.node;
      }

      public int indexInParent() {
         return this.indexInParent;
      }

   }
}
