package de.mmenning.db.index.rsttree;

import java.util.ArrayList;
import java.util.Random;
import java.util.Set;

import de.mmenning.db.index.NDRectangleKey;
import de.mmenning.db.index.NowGen;
import de.mmenning.db.index.generate.IndexFiller;
import de.mmenning.db.index.generate.NDRandomRectangleGenerator;
import de.mmenning.db.index.rsttree.RSTTree;
import de.mmenning.db.index.rsttree.RSTTreeFactory;
import de.mmenning.db.index.rtree.NDRTree;
import de.mmenning.db.index.rtree.Node;
import de.mmenning.db.storage.DefaultStorage;
import de.mmenning.util.math.RandomInterval;
import de.mmenning.util.math.STRandomInterval;
import de.mmenning.util.math.Uniform;

public class STBugTracker {

	private static RSTTree fill() {
		final int dim = 2;
		final int initial = 1;
		final int size = 9;
		final Random rand = new Random();
		NowGen now = new NowGen(1.0, 0.001);
		RandomInterval[] randIs = new RandomInterval[dim - 2];
		for (int i = 0; i < dim - 2; i++) {
			randIs[i] = new RandomInterval(new Uniform(5, 990), new Uniform(0,
					5));
		}
		NDRandomRectangleGenerator recGen = new NDRandomRectangleGenerator(
				randIs);
		RandomInterval validTime = new STRandomInterval(new Uniform(5, 990),
				new Uniform(0, 5), 0.1, now);

		RSTTree tree = RSTTreeFactory.createRSTTree(4, dim - 2, 0.75, 1., now);
		ArrayList<NDRectangleKey> activeHistories = new ArrayList<NDRectangleKey>();
		/*
		 * initialSetup
		 */
		IndexFiller.fillIndex(tree, activeHistories,
				IndexFiller.createOperations(initial, 1, 0, 0, rand), now,
				recGen, validTime, rand);
		/*
		 * fill Index
		 */
		IndexFiller.fillIndex(tree, activeHistories,
				IndexFiller.createOperations(size - initial, 0, 1, 0, rand),
				now, recGen, validTime, rand);
		return tree;

	}

	public static void main(String[] args) {

		// System.out.println("Before");
		// printTree(tree);
		boolean failed = false;

		for (int test = 0; test < 10000 && !failed; test++) {

			RSTTree tree = fill();
			Set<NDRectangleKey> added = tree.getAll();

			for (NDRectangleKey key : added) {
				if (!tree.delete(key)) {
					failed = true;
					System.out.println(key.getNDKey() + " could not be deleted");
					if(!tree.contains(key)){
						System.out.println("Also not Contained");
					}
					printTree(tree);
					break;
				}
				if (tree.contains(key)) {
					failed = true;
					System.out.println("Still contained: " + key.getObject()
							+ " " + key.getNDKey());
					printTree(tree);
					break;

				}
			}
		}

	}

	static void printTree(RSTTree rsttree) {
		System.out.println("SIZE = " + rsttree.size());
		System.out.println("NOW = " + rsttree.getSTConstants().getNowValue());
		System.out.println("P = " + rsttree.getSTConstants().getPValue());

		Node root = rsttree.getRoot();
		printNode(root);


		System.out.println("left child");
		printNode((Node) DefaultStorage.getInstance()
				.load(root.get(0).getChild()).getObject());
		System.out.println("right child");
		printNode((Node) DefaultStorage.getInstance()
				.load(root.get(1).getChild()).getObject());
		System.out.println();
	}

	static void printNode(Node n) {
		for (int i = 0; i < n.size(); i++) {
			System.out.println(n.get(i).getMBBox());
		}
	}

	static void printAll(NDRTree rst) {
		Set<NDRectangleKey> s = rst.getAll();
		for (NDRectangleKey key : s) {
			System.out.println(key.getObject() + " " + key.getNDKey());
		}

	}
}
