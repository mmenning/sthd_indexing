package de.mmenning.db.index.generate;

import java.util.Collections;
import java.util.Iterator;
import java.util.List;

import de.mmenning.db.index.NDRectangle;

public class RealRectangleGenerator extends NDRectangleGenerator {

	private List<NDRectangle> rectangles;

	private Iterator<NDRectangle> iter;
	
	public RealRectangleGenerator(List<NDRectangle> rectangles) {

		this.rectangles = rectangles;
	}

	@Override
	public int getDim() {
		return 3;
	}

	@Override
	public NDRectangle getNextRectangle() {
		if(iter.hasNext()){
			return iter.next();
		}else{
			this.init();
			return iter.next();
		}
	}

	@Override
	public void init() {
		Collections.shuffle(rectangles);
		iter = rectangles.iterator();
	}

}
