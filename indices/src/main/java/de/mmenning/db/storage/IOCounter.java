package de.mmenning.db.storage;

public class IOCounter {

	private long reads;
	private long writes;
	
	public long getReads(){
		return this.reads;
	}
	
	public long getWrites(){
		return this.writes;
	}
	
	public void incReads(int b){
		if(b<=0){
			throw new IllegalArgumentException();
		}
		this.reads+=b;
	}
	
	public void incWrites(int b){
		if(b<=0){
			throw new IllegalArgumentException();
		}
		this.writes+=b;
	}
	
}
