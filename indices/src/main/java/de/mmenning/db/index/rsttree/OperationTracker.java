package de.mmenning.db.index.rsttree;

import java.io.Serializable;
import java.util.Observable;

public class OperationTracker extends Observable implements Serializable{

	private static final long serialVersionUID = -5267300492328210203L;

	public void inserted(){
		this.setChanged();
		this.notifyObservers();
	}

}
