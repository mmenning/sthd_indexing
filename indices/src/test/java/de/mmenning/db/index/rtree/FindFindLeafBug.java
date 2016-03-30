package de.mmenning.db.index.rtree;

import de.mmenning.db.index.NDRectangleKey;
import de.mmenning.db.index.generate.NDRandomRectangleGenerator;
import de.mmenning.db.index.generate.RectangleDistributions;
import de.mmenning.db.index.generate.StatsObject;
import de.mmenning.db.index.rtree.NDRTree;
import de.mmenning.db.index.rtree.Node;

public class FindFindLeafBug {

	static final int dim = 5;
	static final int maxK = 4;

	static NDRTree tree;
	static NDRectangleKey[] added;
	static final NDRandomRectangleGenerator recGen = RectangleDistributions.UNIFORM_5D;

	static NDRTree getTree() {
		return tree;
	}

	static NDRectangleKey nextNDRectangleKey() {
		return new NDRectangleKey(new StatsObject().getObjectReference(),
				recGen.getNextRectangle());
	}

	static void setUp(int initialSize) {
		added = new NDRectangleKey[initialSize];
		tree = new NDRTree(maxK, maxK / 2, dim);

		for (int i = 0; i < initialSize; i++) {
			final NDRectangleKey box = nextNDRectangleKey();

			// tree.insert(box);

			added[i] = box;
		}
	}

	public static void main(String[] args) {
		boolean fail = false;

		for (int size = 1; size < 10 && !fail; size++) {
			setUp(size);

			for (int i = 0; !fail && i < size; i++) {
				tree.insert(added[i]);

				for (int j = 0; j < i; j++) {

					if (!tree.contains(added[j])) {
						System.out.println("Not contained: "
								+ added[j].getObject());
						fail = true;
					}
				}
			}
			if (fail) {
				System.out.println("size " + (size));
				printTree();
			}
		}

	}

	static void printTree() {
		for (int i = 0; i < tree.getHeight(); i++) {
			printLevel(tree.getRoot(), i);
			System.out.println();
		}
	}

	static void printLevel(Node n, int level) {
		if (level == 0) {
			System.out.print("(" + n);

			for (int i = 0; i < n.size(); i++) {
				System.out.print("| " + n.get(i));
			}
			System.out.print(")");

		} else {
			for (int i = 0; i < n.size(); i++) {
				printLevel(tree.readNode(n.get(i).getChild()), level - 1);
			}
		}
	}

}
