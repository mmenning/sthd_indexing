package de.mmenning.db.index.view;

import java.io.File;

import de.mmenning.db.index.NDRectangle;

public interface RectangleImporter {

	public Iterable<NDRectangle> parseRectangles(File f);

}
