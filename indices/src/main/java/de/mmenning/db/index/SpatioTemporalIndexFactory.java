package de.mmenning.db.index;

import de.mmenning.db.storage.StorageManager;

/**
 * @author Mathias Menninghaus (mathias.mennighaus@uos.de)
 */
public interface SpatioTemporalIndexFactory {

   public NDRectangleKeyIndex createIndex(int dims, int blockSize,
                                                   StorageManager s, NowGen
         now);

}
