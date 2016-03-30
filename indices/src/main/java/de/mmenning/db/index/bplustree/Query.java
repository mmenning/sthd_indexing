package de.mmenning.db.index.bplustree;

import java.util.LinkedList;

/**
 * Queries a BPlusTree at the leaf level.
 *
 * @param <K> key type
 * @param <V> value type
 * @see BPlusTree#rangeQuery(K, K, Query<K,V>)
 */
public interface Query<K, V> {

   /**
    * Returns <code>true</code> if query should be continued, returns
    * <code>false</code> if query should not be continued. The last visited
    * node consists of <code>key</code> and <code>values</code>.
    *
    * @param key    key of the last visited node
    * @param values values of the last visited node
    * @return if the query should be continued or not
    */
   public boolean query(K key, LinkedList<V> values);

}
