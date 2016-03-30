package de.mmenning.db.index.rtree;

public class NodeEntryPair {
	private final Node node;
	private final Entry parentEntry;

	public NodeEntryPair(Entry parentEntry, Node node) {
		this.node = node;
		this.parentEntry = parentEntry;
	}

	public Node getNode() {
		return this.node;
	}

	public Entry getParentEntry() {
		return this.parentEntry;
	}

}
