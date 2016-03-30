package de.mmenning.db.index.generate;

import de.mmenning.db.index.NDRectangle;

public abstract class NDRectangleGenerator {
	
	private String description;
	
	public String getDescription(){
		return this.description;
	}

	public abstract int getDim() ;

	public abstract void init();
	
	public abstract NDRectangle getNextRectangle() ;

	public void setDescription(final String description){
		this.description = description;
	}
}
