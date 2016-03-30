package de.mmenning.db.index.bplustree;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.ConcurrentModificationException;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.NoSuchElementException;

import de.mmenning.db.storage.DefaultStorage;
import de.mmenning.db.storage.ObjectReference;
import de.mmenning.db.storage.Storable;
import de.mmenning.db.storage.StorableIndex;
import de.mmenning.db.storage.StorageManager;

public abstract class BPlusTree<K, V> implements StorableIndex,
		Iterable<KeyValuePair<K, LinkedList<V>>> {

	private class BPlusTreeRangeIterator implements
			Iterator<KeyValuePair<K, LinkedList<V>>> {

		private int currentIndex;
		private LeafNode<K, V> currentLeaf;
		private final int currentMod;
		private final K end;

		private BPlusTreeRangeIterator(K begin, K end) {
			ArrayList<IndexNodePair> path = findLeaf(begin);
			this.currentLeaf = readLeafNode(path.get(path.size() - 1)
					.getNodeReference());
			this.currentMod = mod;
			this.currentIndex = this.currentLeaf.getIndex(begin);
			if (this.currentIndex < 0) {
				this.currentIndex = -(this.currentIndex + 1);
			}
			if (this.currentIndex >= currentLeaf.size()) {
				currentLeaf = null;
			}
			this.end = end;

		}

		@Override
		public boolean hasNext() {
			if (this.currentLeaf != null) {
				KeyValuePair<K, LinkedList<V>> next = new KeyValuePair<K, LinkedList<V>>(
						this.currentLeaf.get(this.currentIndex).getKey(),
						currentLeaf.getChild(currentIndex));
				if (compare(next.getKey(), end) > 0) {
					return false;
				} else {
					return true;
				}

			} else {
				return false;

			}
		}

		@Override
		public KeyValuePair<K, LinkedList<V>> next() {
			if (!hasNext()) {
				throw new NoSuchElementException();
			}
			if (currentMod != mod) {
				throw new ConcurrentModificationException();
			}

			KeyValuePair<K, LinkedList<V>> next = new KeyValuePair<K, LinkedList<V>>(
					this.currentLeaf.get(this.currentIndex).getKey(),
					currentLeaf.getChild(currentIndex));

			this.currentIndex++;

			if (currentIndex == this.currentLeaf.size()) {
				this.currentLeaf = getRightNode(this.currentLeaf);
				this.currentIndex = 0;
			}

			return next;
		}

		@Override
		public void remove() {
			throw new UnsupportedOperationException();

		}
	}

	private class BPlusTreeIterator implements
			Iterator<KeyValuePair<K, LinkedList<V>>> {

		private int currentIndex;
		private LeafNode<K, V> currentLeaf;
		private final int currentMod;

		private BPlusTreeIterator() {
			this.currentLeaf = getLeftMostLeaf();
			this.currentMod = mod;
			this.currentIndex = 0;
		}

		@Override
		public boolean hasNext() {
			return this.currentLeaf != null;
		}

		@Override
		public KeyValuePair<K, LinkedList<V>> next() {
			if (!hasNext()) {
				throw new NoSuchElementException();
			}
			if (currentMod != mod) {
				throw new ConcurrentModificationException();
			}

			KeyValuePair<K, LinkedList<V>> next = new KeyValuePair<K, LinkedList<V>>(
					this.currentLeaf.get(this.currentIndex).getKey(),
					currentLeaf.getChild(currentIndex));

			this.currentIndex++;

			if (currentIndex == this.currentLeaf.size()) {
				this.currentLeaf = getRightNode(this.currentLeaf);
				this.currentIndex = 0;
			}

			return next;
		}

		@Override
		public void remove() {
			throw new UnsupportedOperationException();

		}
	}

	protected class IndexNodePair {

		private int index;
		private ObjectReference n;

		protected IndexNodePair(int index, BNode<K, ?> node) {
			this.n = node.getObjectReference();
			this.index = index;
		}

		protected IndexNodePair(int index, ObjectReference or) {
			this.n = or;
			this.index = index;
		}

		protected int getIndexInParent() {
			return index;
		}

		protected BNode<K, ?> getNode() {
			return readNode(n);
		}

		protected ObjectReference getNodeReference() {
			return this.n;
		}
	}

	private final Comparator<? super K> comp;

	private int height;

	private StorageManager ioacc = DefaultStorage.getInstance();

	protected final int leafCapacity;

	private int mod = 0;

	protected final int nodeCapacity;

	private ObjectReference root;

	private int size;

	public BPlusTree(BPlusTree<K, V> original) {
		this.nodeCapacity = original.nodeCapacity;
		this.leafCapacity = original.leafCapacity;
		this.comp = original.comp;
		this.ioacc = original.ioacc;

		this.bulkLoad(original.iterator());
	}

	public BPlusTree(final int dirNode_D, final int leafNode_D) {
		this(dirNode_D, leafNode_D, null, DefaultStorage.getInstance());
	}

	public BPlusTree(final int dirNode_D, int leafNode_D,
			Comparator<? super K> comp) {
		this(dirNode_D, leafNode_D, comp, DefaultStorage.getInstance());
	}

	public BPlusTree(final int dirNode_D, final int leafNode_D,
			Comparator<? super K> comp, StorageManager s) {
		this.nodeCapacity = 2 * dirNode_D;
		this.leafCapacity = 2 * leafNode_D;
		this.comp = comp;

		this.height = 1;
		this.ioacc = s;

	}

	public BPlusTree(final int dirNode_D, final int leafNode_D, StorageManager s) {
		this(dirNode_D, leafNode_D, null, s);
	}

	public BPlusTree(Iterator<KeyValuePair<K, LinkedList<V>>> elems,
			int dirNode_D, int leafNode_D, Comparator<K> comp, StorageManager s) {
		this(dirNode_D, leafNode_D, comp, s);

		bulkLoad(elems);

	}

	public void clear() {
		ArrayList<IndexNodePair> path = this.createPath();

		while (path.size() != 1) {
			clear(path);
		}

		this.ioacc.delete(this.root);
		this.root = null;

	}

	public boolean contains(K key) {
		ArrayList<IndexNodePair> path = this.findLeaf(key);

		LeafNode<K, V> leaf = this.readLeafNode(path.get(path.size() - 1)
				.getNodeReference());

		return leaf.contains(key);
	}

	public boolean contains(K key, V value) {
		ArrayList<IndexNodePair> path = this.findLeaf(key);

		LeafNode<K, V> leaf = this.readLeafNode(path.get(path.size() - 1)
				.getNodeReference());

		KeyValuePair<K, LinkedList<V>> kv = leaf.get(key);

		if (kv == null) {
			return false;
		}

		LinkedList<V> l = kv.getValue();

		return l.contains(value);

	}

	public int getHeight() {
		return this.height;
	}

	@Override
	public StorageManager getStorageManager() {
		return this.ioacc;
	}

	public boolean insert(K key, V value) {
		if (root == null) {
			LeafNode<K, V> rootNode = new LeafNode<K, V>(this.comp,
					this.leafCapacity + 1);
			this.root = rootNode.getObjectReference();
			this.storeLeafNode(rootNode);
		}

		ArrayList<IndexNodePair> path = this.findLeaf(key);

		IndexNodePair inpLeaf = path.get(path.size() - 1);

		LeafNode<K, V> leaf = this.readLeafNode(inpLeaf.getNodeReference());

		KeyValuePair<K, LinkedList<V>> kv = leaf.get(key);
		if (kv == null) {
			LinkedList<V> l = new LinkedList<>();
			l.add(value);

			kv = new KeyValuePair<>(key, l);
			leaf.insert(kv);
		
			this.storeNode(leaf);

			this.readjust(path);

			split(path);

			this.mod++;
			this.size++;
			return true;
		} else {
			LinkedList<V> l = kv.getValue();
			if (l.contains(value)) {
				return false;
			} else {
				l.add(value);
				this.mod++;
				this.size++;
				this.storeNode(leaf);
				return true;
			}
		}

	}

	@Override
	public Iterator<KeyValuePair<K, LinkedList<V>>> iterator() {
		return new BPlusTreeIterator();
	}

	public Iterator<KeyValuePair<K, LinkedList<V>>> rangeQuery(K begin, K end) {
		return new BPlusTreeRangeIterator(begin, end);
	}

	public boolean rangeQuery(K begin, K end, Query<K, V> q) {

		ArrayList<IndexNodePair> path = this.findLeaf(begin);

		LeafNode<K, V> leaf = this.readLeafNode(path.get(path.size() - 1)
				.getNodeReference());

		int index = leaf.getIndex(begin);

		if (index < 0) {
			index = -(index + 1);
		}

		boolean query = true;

		while (query) {

			for (int i = index; i < leaf.size() && query; i++) {

				if (compare(end, leaf.get(i).getKey()) < 0) {
					return true;
				} else {
					query = q.query(leaf.getKey(i), leaf.getChild(i));
				}
			}

			if (query) {
				leaf = this.getRightNode(leaf);
				index = 0;

				if (leaf == null) {
					return true;
				}
			} else {
				return false;
			}
		}

		throw new IllegalStateException();
	}

	public boolean remove(K key) {
		ArrayList<IndexNodePair> path = this.findLeaf(key);
		return remove(key, path);

	}

	public boolean remove(K key, V value) {
		ArrayList<IndexNodePair> path = this.findLeaf(key);

		LeafNode<K, V> leaf = this.readLeafNode(path.get(path.size() - 1)
				.getNodeReference());

		KeyValuePair<K, LinkedList<V>> kv = leaf.get(key);

		if (kv == null) {
			return false;
		} else {
			LinkedList<V> l = kv.getValue();
			if (l.remove(value)) {
				this.size--;
				this.mod++;
				/*
				 * remove the value from the specific entry
				 */
				/*
				 * if there are no more values with the given key, remove the
				 * key.
				 */
				if (l.size() == 0) {

					leaf.remove(kv.getKey());
					this.storeNode(leaf);
					merge(path);
				} else {
					this.storeNode(leaf);
				}

				return true;
			} else {
				return false;
			}
		}

	}

	public int size() {
		return this.size;
	}

	private DirNode<K, V> addDirBulkLoad(DirNode<K, V> father, BNode<K, ?> toAdd) {

		if (father.size() == this.nodeCapacity) {

			DirNode<K, V> newDirNode = new DirNode<K, V>(this.comp,
					this.nodeCapacity);
			newDirNode.addFirst(new KeyValuePair<>(toAdd.getKey(0), toAdd
					.getObjectReference()));
			this.storeDirNode(newDirNode);

			return newDirNode;

		} else {
			father.addLast(new KeyValuePair<>(toAdd.getKey(0), toAdd
					.getObjectReference()));
			this.storeDirNode(father);

			return father;
		}

	}

	protected void bulkLoad(Iterator<KeyValuePair<K, LinkedList<V>>> elems) {

		ArrayList<IndexNodePair> path = bulkLoadRoot(elems);
		/*
		 * bulk load the remaining elements
		 */
		LeafNode<K, V> leftLeaf = this.readLeafNode(path
				.remove(path.size() - 1).getNodeReference());

		while (elems.hasNext()) {
			/*
			 * bulk load the next leaf
			 */
			LeafNode<K, V> rightLeaf = new LeafNode<>(comp, this.leafCapacity);
			bulkLoadLeaf(rightLeaf, elems);

			leftLeaf.setRightSibling(rightLeaf.getObjectReference());

			this.storeLeafNode(leftLeaf);
			this.storeLeafNode(rightLeaf);

			/*
			 * put it in the tree
			 */
			splitBulkLoad(path, rightLeaf);
			leftLeaf = rightLeaf;

		}

		this.root = path.get(0).getNodeReference();
	}

	private void bulkLoadLeaf(LeafNode<K, V> leaf,
			Iterator<KeyValuePair<K, LinkedList<V>>> elems) {

		while (elems.hasNext() && leaf.size() < this.leafCapacity) {
			leaf.addLast(elems.next());
			this.size++;
		}
	}

	private ArrayList<IndexNodePair> bulkLoadRoot(
			Iterator<KeyValuePair<K, LinkedList<V>>> elems) {

		ArrayList<IndexNodePair> path = new ArrayList<>();

		/*
		 * create initial leaf
		 */
		LeafNode<K, V> rootLeaf = new LeafNode<>(comp, this.leafCapacity + 1);
		bulkLoadLeaf(rootLeaf, elems);
		this.storeLeafNode(rootLeaf);

		/*
		 * create initial root
		 */
		if (elems.hasNext()) {
			DirNode<K, V> dirRoot = new DirNode<>(comp, this.nodeCapacity + 1);
			dirRoot.addFirst(new KeyValuePair<>(rootLeaf.getKey(0), rootLeaf
					.getObjectReference()));
			this.storeDirNode(dirRoot);

			path.add(new IndexNodePair(-1, dirRoot));
			path.add(new IndexNodePair(0, rootLeaf));
			this.height = 2;

		} else {
			path.add(new IndexNodePair(-1, rootLeaf));
			this.height = 1;
		}

		return path;
	}

	private void clear(ArrayList<IndexNodePair> path) {
		if (path.size() == this.getHeight()) {
			IndexNodePair p = path.get(path.size() - 1);
			ioacc.delete(p.getNodeReference());
		} else {
			ObjectReference nr = path.get(path.size() - 1).getNodeReference();
			int size = this.readDirNode(nr).size();
			for (int i = 0; i < size; i++) {

				ObjectReference ref = this.readDirNode(nr).getChild(i);
				path.add(new IndexNodePair(i, ref));
				clear(path);
				path.remove(path.size() - 1);
				ioacc.delete(ref);
				;

			}
		}
	}

	private ArrayList<IndexNodePair> createPath() {

		ArrayList<IndexNodePair> path = new ArrayList<IndexNodePair>(
				this.getHeight());

		path.add(new IndexNodePair(-1, this.root));

		return path;
	}

	private void deleteNode(BNode<K, ?> node) {
		this.ioacc.delete(node.getObjectReference());
	}

	private void doMerge(ArrayList<IndexNodePair> path) {

		IndexNodePair child = path.get(path.size() - 1);
		BNode<K, ?> childNode = child.getNode();
		DirNode<K, V> fatherNode = (DirNode<K, V>) path.get(path.size() - 2)
				.getNode();

		final int d = childNode.isLeaf() ? (this.leafCapacity / 2)
				: (this.nodeCapacity / 2);

		if (childNode.size() < d) {

			BNode<K, ?> sibling;
			boolean isLeftSibling;

			final int index = child.getIndexInParent();

			/*
			 * get left or right sibling for merge
			 */
			if (index == 0) {
				sibling = this.readNode(fatherNode.get(index + 1).getValue());
				isLeftSibling = false;
			} else if (index == fatherNode.size() - 1) {
				sibling = this.readNode(fatherNode.get(index - 1).getValue());
				isLeftSibling = true;
			} else {
				BNode<K, ?> leftSibling = this.readNode(fatherNode.get(
						index - 1).getValue());
				BNode<K, ?> rightSibling = this.readNode(fatherNode.get(
						index + 1).getValue());
				/*
				 * choose sibling with most entries
				 */
				if (rightSibling.size() > leftSibling.size()) {
					sibling = rightSibling;
					isLeftSibling = false;
				} else {
					sibling = leftSibling;
					isLeftSibling = true;
				}
			}

			/*
			 * if sibling has more than d entries redistribute, else merge
			 */
			if (sibling.size() > d) {
				/*
				 * with redistribution the right of the two nodes may have
				 * changed its separator.
				 */
				if (isLeftSibling) {
					this.redistribute(sibling, childNode);
				} else {
					this.redistribute(childNode, sibling);
					path.set(path.size() - 1, new IndexNodePair(index + 1,
							sibling));
				}
				this.readjust(path);
			} else {
				if (isLeftSibling) {
					this.merge(sibling, childNode);
					fatherNode.remove(index);
				} else {
					this.merge(childNode, sibling);
					fatherNode.remove(index + 1);
				}
				this.storeNode(fatherNode);
			}
		} else {
			this.readjust(path);
		}
	}

	private IndexNodePair findLeaf(DirNode<K, V> father, K search) {

		int index = father.getIndex(search);

		if (index >= 0) {
			return new IndexNodePair(index, this.readNode(father.get(index)
					.getValue()));
		} else {

			if (index == -1) {
				/*
				 * leftmost entry is greater than search
				 */
				return new IndexNodePair(0, this.readNode(father.get(0)
						.getValue()));
			} else if (index == -(father.size() + 1)) {
				/*
				 * rightmost entry is smaller than search
				 */
				index = father.size() - 1;
				return new IndexNodePair(index, this.readNode(father.get(index)
						.getValue()));
			} else {
				/*
				 * search does not equal an entry in father. return entry which
				 * is the last one which entry is smaller than father
				 */
				index = -(index) - 2;
				return new IndexNodePair(index, this.readNode(father.get(index)
						.getValue()));
			}
		}
	}

	@SuppressWarnings("unchecked")
	private ArrayList<IndexNodePair> findLeaf(final K search) {

		ArrayList<IndexNodePair> path = createPath();

		IndexNodePair n = path.get(path.size() - 1);

		while (path.size() != this.getHeight()) {
			n = findLeaf(
					this.readDirNode(n.getNodeReference()),
					search);
			path.add(n);
		}

		return path;
	}

	private LeafNode<K, V> getLeftMostLeaf() {
		return this.getLeftMostLeaf(this.getRoot());
	}

	private LeafNode<K, V> getLeftMostLeaf(BNode<K, ?> n) {

		if (n.isLeaf()) {
			return (LeafNode<K, V>) n;
		} else {
			return getLeftMostLeaf(readNode((ObjectReference) n.getFirst()
					.getValue()));
		}

	}

	private void merge(ArrayList<IndexNodePair> path) {

		if (path.size() > 1) {
			/*
			 * only merge non-root nodes.
			 */
			while (path.size() > 1) {
				/*
				 * merge until root node
				 */
				this.doMerge(path);

				path.remove(path.get(path.size() - 1));

			}

			BNode<K, ?> rootNode = this.readNode(this.root);
			if (rootNode.size() == 1) {
				this.deleteNode(rootNode);
				this.root = (ObjectReference) rootNode.getFirst().getValue();
				this.height--;
			}
		}
	}

	private void merge(BNode leftSibling, BNode rightSibling) {
		for (int i = 0; i < rightSibling.size(); i++) {
			leftSibling.addLast(rightSibling.get(i));
		}
		if (leftSibling.isLeaf()) {

			((LeafNode) leftSibling).setRightSibling(((LeafNode) rightSibling)
					.getRightSibling());
		}
		this.storeNode(leftSibling);
		this.deleteNode(rightSibling);
	}

	private void readjust(ArrayList<IndexNodePair> path) {

		if (path.size() > 1) {
			BNode<K, ?> childNode;
			int childIndex;

			BNode<K, ?> fatherNode = path.get(path.size() - 1).getNode();
			int fatherIndex = path.get(path.size() - 1).getIndexInParent();

			for (int i = path.size() - 2; i >= 0; i--) {

				childNode = fatherNode;
				childIndex = fatherIndex;
				fatherNode = path.get(i).getNode();
				fatherIndex = path.get(i).getIndexInParent();

				if (!readjust(childNode, childIndex, (DirNode<K, V>) fatherNode)) {
					break;
				}
			}
		}
	}

	private boolean readjust(BNode<K, ?> childNode, int childIndex,
			DirNode<K, V> fatherNode) {

		final int cmp;

		K childSeparator = childNode.get(0).getKey();
		K fatherSeparator = fatherNode.get(childIndex).getKey();

		if (this.comp == null) {
			cmp = ((Comparable<? super K>) childSeparator)
					.compareTo(fatherSeparator);
		} else {
			cmp = this.comp.compare(childSeparator, fatherSeparator);
		}

		if (cmp != 0) {
			fatherNode
					.replace(new KeyValuePair<K, ObjectReference>(
							childSeparator, childNode.getObjectReference()),
							childIndex);
			this.storeNode(fatherNode);
			return true;
		} else {
			return false;
		}
	}

	private void redistribute(BNode leftSibling, BNode rightSibling) {

		while (Math.abs(leftSibling.size() - rightSibling.size()) > 1) {
			if (leftSibling.size() < rightSibling.size()) {
				leftSibling.addLast(rightSibling.remove(0));
			} else {
				rightSibling.addFirst(leftSibling.removeLast());
			}
		}

		this.storeNode(leftSibling);
		this.storeNode(rightSibling);
	}

	private boolean remove(K key, ArrayList<IndexNodePair> path) {

		LeafNode<K, V> leaf = this.readLeafNode(path.get(path.size() - 1)
				.getNodeReference());

		KeyValuePair<K, LinkedList<V>> kv = leaf.get(key);

		if (!leaf.remove(key)) {
			return false;
		} else {
			/*
			 * the key has been removed. store the change
			 */
			storeNode(leaf);
			merge(path);
			this.mod++;
			this.size -= kv.getValue().size();
			return true;
		}

	}

	private void split(ArrayList<IndexNodePair> path) {
		IndexNodePair child = path.remove(path.size() - 1);
		IndexNodePair father;

		if (path.size() == 0) {
			this.splitLeaf(null, child);
		} else {
			father = path.remove(path.size() - 1);
			this.splitLeaf(this.readDirNode(father.getNodeReference()), child);

			while (path.size() > 0) {
				child = father;
				father = path.remove(path.size() - 1);
				splitDir(this.readDirNode(father.getNodeReference()), child);
			}
			splitDir(null, father);
		}
	}

	private void splitBulkLoad(final ArrayList<IndexNodePair> path,
			final LeafNode<K, V> leaf) {

		DirNode<K, V> dirNode = null, newDirNode = null;
		int fatherLevel = path.size() - 1;
		BNode<K, ?> toAdd = leaf;

		while (fatherLevel >= 0) {

			dirNode = this
					.readDirNode(path.get(fatherLevel).getNodeReference());

			/*
			 * a new dir node will be created if the adding of to add to dirNode
			 * would overload the dirNode -> a split occurs
			 */
			newDirNode = addDirBulkLoad(dirNode, toAdd);

			if (dirNode != newDirNode) {

				/*
				 * toAdd has been added to newDirNode and therefore, if it has
				 * been a DirNode, the path has to be updated.
				 */

				if (fatherLevel < path.size() - 1) {
					path.set(fatherLevel + 1,
							new IndexNodePair(0, toAdd.getObjectReference()));
				}

				/*
				 * newDirNode has to become the new toAdd, because it needs to
				 * be added into the tree; one level above the father level is
				 * the father of newDirNode and dirNode
				 */
				toAdd = newDirNode;

				/*
				 * if root node has splitted.
				 */
				if (fatherLevel == 0) {
					DirNode<K, V> newRoot = new DirNode<K, V>(comp,
							this.nodeCapacity + 1);
					newRoot.addLast(new KeyValuePair<>(dirNode.getKey(0),
							dirNode.getObjectReference()));
					newRoot.addLast(new KeyValuePair<>(newDirNode.getKey(0),
							newDirNode.getObjectReference()));

					this.storeDirNode(newRoot);

					path.add(0,
							new IndexNodePair(-1, newRoot.getObjectReference()));
					path.set(
							1,
							new IndexNodePair(1, newDirNode
									.getObjectReference()));
					this.height++;

				}

				fatherLevel--;

			} else {

				/*
				 * else only the path must be updated
				 */
				if (fatherLevel < path.size() - 1) {
					path.set(
							fatherLevel + 1,
							new IndexNodePair(path.get(fatherLevel + 1)
									.getIndexInParent() + 1, toAdd
									.getObjectReference()));
				}

				return;
			}
		}

	}

	private void splitDir(DirNode<K, V> father, IndexNodePair child) {

		DirNode<K, V> n = (DirNode<K, V>) child.getNode();

		if (n.size() > this.nodeCapacity) {
			LinkedList<KeyValuePair<K, ObjectReference>> tmp = new LinkedList<>();

			int d = this.nodeCapacity / 2;

			for (int i = 0; i < d; i++) {
				tmp.addFirst(n.getLast());
				n.removeLast();
			}

			DirNode<K, V> newOne = new DirNode<K, V>(this.comp,
					this.nodeCapacity + 1);

			for (KeyValuePair<K, ObjectReference> k : tmp) {
				newOne.addLast(k);
			}

			KeyValuePair<K, ObjectReference> intoFather = new KeyValuePair<K, ObjectReference>(
					newOne.get(0).getKey(), newOne.getObjectReference());

			if (father == null) {
				/*
				 * splitting the root
				 */
				father = new DirNode<K, V>(this.comp, this.nodeCapacity + 1);
				father.addLast(new KeyValuePair<K, ObjectReference>(n.get(0)
						.getKey(), n.getObjectReference()));
				father.addLast(intoFather);
				this.root = father.getObjectReference();
				this.height++;
			} else {
				/*
				 * or else
				 */
				if (!father.insert(intoFather, child.getIndexInParent() + 1)) {
					throw new IllegalStateException();
				}
			}
			this.storeNode(newOne);
			this.storeNode(n);
			this.storeNode(father);
		}
	}

	private void splitLeaf(DirNode<K, V> father, IndexNodePair child) {

		LeafNode<K, V> childNode = (LeafNode<K, V>) child.getNode();

		if (childNode.size() > this.leafCapacity) {

			LinkedList<KeyValuePair<K, LinkedList<V>>> tmp = new LinkedList<>();

			int d = this.leafCapacity / 2;

			for (int i = 0; i < d; i++) {
				tmp.addFirst(childNode.removeLast());
			}

			LeafNode<K, V> newOne = new LeafNode<K, V>(this.comp,
					this.leafCapacity + 1);

			for (KeyValuePair<K, LinkedList<V>> k : tmp) {
				newOne.addLast(k);
			}

			/*
			 * only needs to update the reference of the right sibling of n, no
			 * need to load the right sibling of n into memory.
			 */
			newOne.setRightSibling(childNode.getRightSibling());
			childNode.setRightSibling(newOne.getObjectReference());

			KeyValuePair<K, ObjectReference> intoFather = new KeyValuePair<K, ObjectReference>(
					newOne.get(0).getKey(), newOne.getObjectReference());

			if (father == null) {
				/*
				 * splitting the root
				 */
				father = new DirNode<K, V>(this.comp, this.nodeCapacity + 1);
				father.addLast(new KeyValuePair<K, ObjectReference>(childNode
						.get(0).getKey(), childNode.getObjectReference()));
				father.addLast(intoFather);
				this.root = father.getObjectReference();
				this.height++;
			} else {
				/*
				 * or else
				 */
				if (!father.insert(intoFather, child.getIndexInParent() + 1)) {
					throw new IllegalStateException("father: "
							+ father.get(0).getKey() + " intoFather "
							+ intoFather.getKey());
				}
			}

			this.storeNode(newOne);
			this.storeNode(childNode);
			this.storeNode(father);
		} else {
			this.storeNode(childNode);
		}
	}

	private void storeNode(BNode<K, ?> n) {
		if (n.isLeaf()) {
			storeLeafNode((LeafNode<K, V>) n);
		} else {
			storeDirNode((DirNode<K, V>) n);
		}
	}

	protected int compare(K one, K another) {

		if (comp == null) {
			return ((Comparable<K>) one).compareTo(another);
		} else {
			return comp.compare(one, another);
		}

	}

	protected void deleteDirNode(DirNode<K, V> n) {
		this.ioacc.delete(n.getObjectReference());
	}

	protected void deleteLeafNode(LeafNode<K, V> n) {
		this.ioacc.delete(n.getObjectReference());
	}

	protected abstract Storable dirNodeToStorable(DirNode<K, V> n);

	protected abstract DirNode<K, V> getDirNodeFromStorable(Storable st);

	protected abstract LeafNode<K, V> getLeafNodeFromStorable(Storable st);

	protected LeafNode<K, V> getRightNode(LeafNode<K, V> leaf) {

		ObjectReference rightLeaf = leaf.getRightSibling();

		if (rightLeaf == null) {
			return null;
		} else {
			return this.readLeafNode(rightLeaf);

		}
	}

	protected abstract Storable leafNodeToStorable(LeafNode<K, V> n);

	protected DirNode<K, V> readDirNode(ObjectReference or) {
		Storable st = this.ioacc.load(or);

		return this.getDirNodeFromStorable(st);
	}

	protected LeafNode<K, V> readLeafNode(ObjectReference or) {
		Storable st = this.ioacc.load(or);

		return this.getLeafNodeFromStorable(st);
	}

	protected BNode<K, ?> readNode(ObjectReference or) {

		if (or.getMeta() == (DirNode.class)) {
			return this.readDirNode(or);

		} else if (or.getMeta() == (LeafNode.class)) {

			return this.readLeafNode(or);
		} else {
			throw new IllegalArgumentException(or.getMeta()
					+ " isn`t a node reference");
		}

	}

	protected void storeDirNode(DirNode<K, V> n) {
		this.ioacc.store(this.dirNodeToStorable(n));
	}

	protected void storeLeafNode(LeafNode<K, V> n) {
		this.ioacc.store(this.leafNodeToStorable(n));
	}

	BNode<K, ?> getRoot() {
		return this.readNode(this.root);
	}

}
