package de.mmenning.db.index.rtree;

import de.mmenning.db.index.NDRectangle;
import de.mmenning.db.storage.DefaultStorage;
import de.mmenning.db.storage.StorageManager;

import java.util.ArrayList;

/**
 * Like {@link NDRStar} but without reInsert and with the choose Subtree
 * algorithm of {@link NDRTree}.
 *
 * @author Mathias Menninghaus (mathias.mennighaus@uos.de)
 */
public class CrippledRStar extends NDRTree {


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
   public CrippledRStar(final int maxCapacity, final double minFanout,
                  final int dimensions) {

      this(maxCapacity, minFanout, dimensions, DefaultStorage.getInstance());
   }

   public CrippledRStar(int maxCapacity, double minFanout, int dimensions,
                  StorageManager s) {

      super(maxCapacity, minFanout, dimensions, s);
   }

   public CrippledRStar(int blockSize, int dimensions, StorageManager s) {
      super(blockSize, dimensions, s);
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
