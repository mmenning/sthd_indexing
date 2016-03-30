package de.mmenning.db.index.rtree;

import java.util.ArrayList;

import de.mmenning.db.index.NDRectangle;

/**
 * A distribution of a overfull node into two nodes, i.e. two lists of nodes
 * and two lists of the corresponding rectangles.
 */
public class CandidateDistribution {

   public NDRectangle coveringFirst;
   public NDRectangle coveringSecond;

   public ArrayList<Entry> firstNode;
   public ArrayList<Entry> secondNode;

   @Override
   public String toString() {
      return "<CandidateDistribution first=" + this.firstNode + " second="
            + this.secondNode + "/>";
   }

}
