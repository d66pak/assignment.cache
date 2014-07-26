package assignment.cache;

/**
 * <p>Interface for the cache to store the elements.
 * NOTE: Implementations of this interface are required to be thread-safe.
 *
 * @param <K> Key type to be stored in cache
 * @param <V> Value type to be stored in cache
 *
 * @author Deepak Telkar
 * Created by dtelkar on 7/25/14.
 */
public interface Cache<K, V> {

    void clear();

    boolean containsKey(Object key);

    V get(Object key);

    boolean isEmpty();

    V put(K key, V value);

    V remove(Object key);

    int size();

    void purge();
}
