package de.mmenning.db.index.pyramid;

import de.mmenning.db.index.NDPointKey;

public interface PyramidQuery {

	public boolean query(PyramidValue k, NDPointKey v);
	
}
