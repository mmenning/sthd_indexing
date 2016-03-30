package de.mmenning.db.index.rtree;

import java.util.Comparator;

class EntryComparator implements Comparator<Entry> {

	private final int dim;
	private final boolean greater;
	
	EntryComparator(final int dim, final boolean greater){
		this.dim = dim;
		this.greater = greater;
	}
	
	@Override
	public int compare(Entry o1, Entry o2) {
		return o1.getMBBox().compareTo(this.dim, this.greater, o2.getMBBox());

	}

}
