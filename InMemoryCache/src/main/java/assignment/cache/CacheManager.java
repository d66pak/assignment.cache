package assignment.cache;

/**
 * <p>Interface for the Cache Manager, which is responsible for maintaining the cache
 * Implementations of this interface are responsible for encapsulating the value to be stored into a cache object
 * based on the caching algorithm.
 *
 * NOTE: Classes must be extended from {@link assignment.cache.AbstractCacheManager} to provide specific implementations.
 *
 * @param <K> Key type to be stored in cache
 * @param <V> Value type to be stored in cache
 *
 * @author Deepak Telkar
 * Created by dtelkar on 7/25/14.
 */
public interface CacheManager<K, V> {

    V put(K key, V value);

    void clearCache();

    boolean containsKey(Object key);

    V get(Object key);

    boolean isCacheEmpty();

    V remove(Object key);

    int cacheSize();

    /**
     * Method must be called in order to stop all the
     * activities of CacheManager at the end
     */
    void shutdown();
}
