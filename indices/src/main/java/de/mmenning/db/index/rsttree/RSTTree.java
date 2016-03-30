package de.mmenning.db.index.rsttree;

import java.util.ArrayList;

import de.mmenning.db.index.NDRectangle;
import de.mmenning.db.index.NDRectangleKey;
import de.mmenning.db.index.rtree.CandidateDistribution;
import de.mmenning.db.index.rtree.Entry;
import de.mmenning.db.index.rtree.NDRStar;
import de.mmenning.db.index.rtree.Node;
import de.mmenning.db.index.rtree.NDRTree.NodeEntryPair;
import de.mmenning.db.storage.DefaultStorage;
import de.mmenning.db.storage.IOUtils;
import de.mmenning.db.storage.ObjectReference;
import de.mmenning.db.storage.StorageManager;
import de.mmenning.util.FixedArrayList;

/**
 * Implementation of the R^ST - Tree
 * 
 * Every inserted, queried, deleted etc. Every {@link NDRectangle} will be
 * converted into a {@link STRectangle}.
 * 
 * @see R-Tree Based Indexing of General Spatio-Temporal Data (Saltenis & Jensen
 *      1999)
 * 
 * @author Mathias Menninghaus (mathias.menninghaus@uos.de)
 *
 */
public class RSTTree extends NDRStar {

	private STConstants constants;

	public RSTTree(final int blockSize, final int spatialDimensions,
			StorageManager s) {

		this(blockSize
				/ (1 + 16 * (spatialDimensions + 2) + IOUtils
						.referenceByteSize()),0.5, spatialDimensions,
				BasicConstants.getInstance(), s);
	}

	/**
	 * Construct RSTTree with {@link BasicConstants}
	 * 
	 * @param maxCapacity
	 *            maximum capacity of the nodes of this tree
	 * @param minFanout
	 *            minimum capacity of the nodes of this tree
	 * @param spatialDimensions
	 *            amount of spatial dimenions. resulting tree will have a total
	 *            number of dimensions of <code>spatialDimensions + 2</code>
	 *            (the valid and the transaction time)
	 */
	public RSTTree(int maxCapacity, double minFanout, int spatialDimensions) {
		this(maxCapacity, minFanout, spatialDimensions, BasicConstants
				.getInstance(), DefaultStorage.getInstance());
	}

	public RSTTree(final int maxCapacity, final double minFanout,
			final int spatialDimensions, final STConstants stConstants) {
		this(maxCapacity, minFanout, spatialDimensions, stConstants,
				DefaultStorage.getInstance());
	}

	/**
	 * Constructs a RSTTree
	 * 
	 * @param maxCapacity
	 *            maximum capacity of the nodes of this tree
	 * @param minFanout
	 *            minimum capacity of the nodes of this tree
	 * @param spatialDimensions
	 *            amount of spatial dimenions. resulting tree will have a total
	 *            number of dimensions of <code>spatialDimensions + 2</code>
	 *            (the valid and the transaction time)
	 * @param stConstants
	 *            holds the "constants" for the p-Value, Now-Value and
	 *            alpha-Value
	 */
	public RSTTree(final int maxCapacity, final double minFanout,
			final int spatialDimensions, final STConstants stConstants,
			StorageManager s) {
		super(maxCapacity, minFanout, spatialDimensions + 2, s);
		this.constants = stConstants;
	}

	@Override
	public boolean contains(NDRectangleKey b) {
		return super.contains(formatKey(b));
	}

	@Override
	public boolean delete(NDRectangleKey b) {
		return super.delete(formatKey(b));

	}

	/**
	 * Returns the number of spatial dimensions
	 * 
	 * @return amount of spatial dimensions
	 */
	public int getSpatialDim() {
		return this.getDim() - 2;
	}

	public STConstants getSTConstants() {
		return this.constants;
	}

	/**
	 * Returns the dimension of the transaction time
	 * 
	 * @return dimension of the transaction time
	 */
	public int getTTDim() {
		return this.getSpatialDim();
	}

	/**
	 * Returns the dimension of the valid time
	 * 
	 * @return dimension of the valid time
	 */
	public int getVTDim() {
		return this.getSpatialDim() + 1;
	}

	@Override
	public synchronized boolean insert(NDRectangleKey b) {
		return super.insert(this.formatKey(b));
	}

	public void setSTConstants(STConstants stConstants) {
		this.constants = stConstants;
	}

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

				if (n.get(i).getMBBox().intersects(element.getNDKey())) {

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
	 * Creates the additional <code>CandidateDistribtution</code> for splitting
	 * <code>n</code> as described in the original paper. <code>n</code> will
	 * not be altered.
	 * 
	 * @param n
	 *            <code>Node</code> for which the splitting distribution should
	 *            be calculated
	 * @return the new splitting distribution for <code>n</code> or
	 *         <code>null</code> if it could not be generated.
	 */
	protected CandidateDistribution createRSTTreeCandidateDistribution(Node n) {

		FixedArrayList<Entry> s = n.getAll();

		final CandidateDistribution c = new CandidateDistribution();

		c.firstNode = new ArrayList<Entry>(n.getMaxCapacity());
		c.secondNode = new ArrayList<Entry>(n.getMaxCapacity());

		int gr = 0;
		int gs = 0;
		int st = 0;
		
		final int minCapacity = (int)(n.getMaxCapacity()*this.getMinFanout());
		int k = n.getMaxCapacity() + 1 - minCapacity;

		for (Entry e : s) {
			switch (((STRectangle) e.getMBBox()).getType()) {

			case STRectangle.GROWING_REC:
				gr++;
				break;
			case STRectangle.GROWING_STAIR:
				gs++;
				break;
			case STRectangle.STATIC:
				st++;
				break;
			default:
				throw new IllegalStateException();
			}
		}

		/*
		 * S2.1
		 */
		int priorityType = 0;
		if (gr + gs == 0) {
			priorityType = 1;
		} else if (gs == 0 && 0 < gr && gr <= k) {
			priorityType = 2;
		} else if (gs == 0 && gr > k) {
			priorityType = 3;
		} else if (gs > 0 && gr + gs <= k) {
			priorityType = 4;
		} else if (0 < gs && gs <= k && gr + gs > k) {
			priorityType = 5;
		} else if (gs > k) {
			priorityType = 6;
		}

		/*
		 * S2.2
		 */
		for (int i = 0; i < s.size(); i++) {
			switch (((STRectangle) s.get(i).getMBBox()).getType()) {
			case STRectangle.GROWING_REC:
				if (priorityType == 2 || priorityType == 4) {
					c.secondNode.add(s.get(i));
					s.remove(i);
					i--;
					gr--;
				}
				break;
			case STRectangle.GROWING_STAIR:
				if (priorityType == 4 || priorityType == 5) {
					c.secondNode.add(s.get(i));
					s.remove(i);
					i--;
					gs--;
				}
				break;
			case STRectangle.STATIC:
				break;
			default:
				throw new IllegalStateException();
			}
		}
		/*
		 * S2.3
		 */
		for (int i = 0; i < s.size(); i++) {
			switch (((STRectangle) s.get(i).getMBBox()).getType()) {
			case STRectangle.GROWING_REC:
				if (gr + c.firstNode.size() <= k) {
					c.firstNode.add(s.get(i));
					s.remove(i);
					i--;
					st--;
				}
				break;
			case STRectangle.GROWING_STAIR:
				break;
			case STRectangle.STATIC:
				if (st <= k) {
					c.firstNode.add(s.get(i));
					s.remove(i);
					i--;
					st--;
				}
				break;
			default:
				throw new IllegalStateException();
			}
		}

		/*
		 * S2.4
		 */
		if (s.size() == 0) {
			c.coveringFirst = this.unionEntries(c.firstNode, 0,
					c.firstNode.size());
			c.coveringSecond = this.unionEntries(c.secondNode, 0,
					c.secondNode.size());
			return c;
		}

		/*
		 * S2.5
		 */
		if (c.firstNode.size() == 0 && c.secondNode.size() == 0) {
			return null;
		}

		/*
		 * S2.6 + S2.7
		 */
		if (c.firstNode.size() == 0) {
			c.coveringSecond = this.unionEntries(c.secondNode, 0,
					c.secondNode.size());
			int seed = pickSeed(s, c.coveringSecond);

			c.firstNode.add(s.get(seed));
			s.remove(seed);
			c.coveringFirst = this.unionEntries(c.firstNode, 0,
					c.firstNode.size());
		} else if (c.secondNode.size() == 0) {
			c.coveringFirst = this.unionEntries(c.firstNode, 0,
					c.firstNode.size());
			int seed = pickSeed(s, c.coveringFirst);

			c.secondNode.add(s.get(seed));
			s.remove(seed);
			c.coveringSecond = this.unionEntries(c.secondNode, 0,
					c.secondNode.size());
		} else {
			c.coveringSecond = this.unionEntries(c.secondNode, 0,
					c.secondNode.size());
			c.coveringFirst = this.unionEntries(c.firstNode, 0,
					c.firstNode.size());
		}

		/*
		 * S2.8
		 */

		this.quadraticDistribute(c, s);

		return c;
	}

	/**
	 * Chooses the better <code>CandidateDistribution</code> between
	 * {@link #createRStarCandidateDistribution(Node)} and
	 * {@link #createRSTTreeCandidateDistribution(Node)}. The better
	 * <code>CandidateDistribution</code> is that, which
	 * <code>coveringFirst</code> and <code>coveringSecond</code> bounding boxes
	 * have the least overlap volume with one another. Ties are resolved by
	 * choosing the <code>CandidateDistribution</code> with the least area.
	 * 
	 * @return either the <code>CandidateDistribution</code> from
	 *         {@link #createRStarCandidateDistribution(Node)} or
	 *         {@link #createRSTTreeCandidateDistribution(Node)}-
	 */
	@Override
	protected CandidateDistribution createSplitDistribution(Node n) {
		CandidateDistribution rstarCandidate = this
				.createRStarCandidateDistribution(n);
		CandidateDistribution rstCandidate = this
				.createRSTTreeCandidateDistribution(n);

		if (rstCandidate == null) {
			return rstarCandidate;
		}

		double overlapRStar = rstarCandidate.coveringFirst
				.intersect(rstarCandidate.coveringSecond);
		double overlapRST = rstCandidate.coveringFirst
				.intersect(rstCandidate.coveringSecond);

		if (overlapRST < overlapRStar) {
			return rstCandidate;
		} else if (overlapRST == overlapRStar) {
			double areaRStar = rstarCandidate.coveringFirst.volume()
					+ rstarCandidate.coveringSecond.volume();
			double areaRST = rstCandidate.coveringFirst.volume()
					+ rstCandidate.coveringSecond.volume();
			if (areaRST <= areaRStar) {
				return rstCandidate;
			} else {
				return rstarCandidate;
			}
		} else {
			return rstarCandidate;
		}
	}

	/**
	 * Formats the given <code>key</code> by converting its
	 * <code>NDRectangle</code> into an <code>STRectangle</code>
	 * 
	 * @param key
	 *            <code>NDRectangleKey</code> which should be formated
	 * @return formated <code>NDRectangleKey</code>
	 */
	protected NDRectangleKey formatKey(NDRectangleKey key) {
		return new NDRectangleKey(key.getObject(), this.formatRectangle(key
				.getNDKey()));
	}

	/**
	 * Converts the given <code>NDRectangle</code> <code>r</code> to a new
	 * <code>STRectangle</code>. If <code>r</code> is already a
	 * <code>STRecangle</code>, it will not be converted again.
	 * 
	 * @param r
	 *            <code>NDRectangle</code> to be converted
	 * @return the converted Rectangle
	 */
	protected NDRectangle formatRectangle(NDRectangle r) {
		if (r instanceof STRectangle) {
			return r;
		} else {
			return new STRectangle(r, this.constants);
		}
	}

	/**
	 * Picks the index a seed entry out of <code>entries</code> such that it
	 * bounding box would enlarge the volume of <code>coverOfTheOther</code> the
	 * most.
	 * 
	 * @param entries
	 *            list, from which the seed entry should be picked.
	 * @param coverOfTheOther
	 *            the <code>NDRectangle</code> which should be enlarged the most
	 *            by the picked seed.
	 * @return the index of the seed <code>Entry</code> in <code>entries</code>
	 */
	protected int pickSeed(FixedArrayList<Entry> entries,
			NDRectangle coverOfTheOther) {

		double bestEnlargement = Double.NEGATIVE_INFINITY;
		int bestEnlargementIndex = -1;
		double currentEnlargement;
		double otherVolume = coverOfTheOther.volume();
		for (int i = 0; i < entries.size(); i++) {
			currentEnlargement = entries.get(i).getMBBox()
					.union(coverOfTheOther).volume()
					- otherVolume;
			if (currentEnlargement > bestEnlargement) {
				bestEnlargement = currentEnlargement;
				bestEnlargementIndex = i;
			}
		}
		return bestEnlargementIndex;
	}

	/**
	 * Reinserts {@link NDRStar#p} entries from the <code>Node</code> which is
	 * denoted by <code>path</code>. Reinserts always that <code>Entry</code>
	 * which decreases the volume of the bounding box of the <code>Node</code>
	 * from which it is extracted the most. Uses {@link NDRStar
	 * nodeSplitsWhileReInsert} to compute the node level to which the extracted
	 * entries should be reinserted.
	 * 
	 * @param path
	 *            path to the <code>Node</code> from which entries should be
	 *            reinserted
	 */
	@Override
	protected void reInsert(ArrayList<NodeEntryPair> path) {
		final Node n = path.get(path.size() - 1).getNode();
		final int nodeLevel = path.size() - 1;

		final int P = (int) (n.getMaxCapacity() * this.p);
		ArrayList<Entry> removed = new ArrayList<Entry>(P);

		for (int i = 0; i < P; i++) {
			int best = 0;
			double bestValue = volumeWithout(n, 0);
			double currentValue = 0;
			for (int j = 1; j < n.size(); j++) {
				currentValue = volumeWithout(n, j);
				if (currentValue < bestValue) {
					best = j;
					bestValue = currentValue;
				}
			}
			removed.add(n.get(best));
			n.removeEntry(best);
		}

		this.storeNode(n);

		this.rootSplitsWhileReInsert = 0;

		this.adjustNode(path);

		for (int i = 0; i < removed.size(); i++) {
			ArrayList<NodeEntryPair> newPath = this.createNewPath();

			this.insert(newPath, removed.get(i), nodeLevel
					+ this.rootSplitsWhileReInsert);
		}
	}

	/**
	 * Calculates the volume of the bounding box of <code>n</code> without the
	 * <code>Entry</code> at index <code>entryPos</code> in <code>n</code>.
	 * <code>n</code> will not be altered.
	 * 
	 * @param n
	 *            <code>Node</code> from which the volume without the
	 *            <code>Entry</code> at <code>entryPos</code> shall be
	 *            calculated.
	 * @param entryPos
	 *            position of the <code>Entry</code> which will not be
	 *            considered calculating the volume of <code>n</code>
	 * @return the volume of all entries in <code>n</code> without the
	 *         <code>Entry</code> at <code>entryPos</code>
	 */
	protected double volumeWithout(Node n, int entryPos) {
		if (entryPos == 0) {
			return this.unionEntries(n, 1, n.size() - 1).volume();
		}
		if (entryPos == n.size() - 1) {
			return this.unionEntries(n, 0, n.size() - 1).volume();
		}
		NDRectangle left = this.unionEntries(n, 0, entryPos);

		NDRectangle right = this.unionEntries(n, entryPos + 1, n.size()
				- entryPos - 1);

		return left.union(right).volume();
	}

	@Override
	protected int getNodeByteSize(Node n) {
		int nodeByteSize = (this.getDim() * 16 + IOUtils.referenceByteSize() + 1)
				* n.getMaxCapacity();
		return nodeByteSize;
	}

	@Override
	protected Node readNode(ObjectReference or) {
		return super.readNode(or);
	}

}
