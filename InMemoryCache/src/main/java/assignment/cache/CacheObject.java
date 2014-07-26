package assignment.cache;

/**
 * <p>Cache object interface responsible to encapsulating the value stored in cache.
 * NOTE: This interface should be used to implement various caching algorithms like timed cache, LRU cache, etc
 *
 * @param <V> Value type to be stored in cache object
 *
 * @author Deepak Telkar
 * Created by dtelkar on 7/25/14.
 */
public interface CacheObject<V> {

    /**
     * Getter method for actual value stored in cache
     *
     * @return Value encapsulated within cache object
     */
    V getCache();

    /**
     * Implementation for various caching algorithms like timed cache, LRU cache, etc
     *
     * @return {@code true} if cache value has expired, else {@code false}
     */
    boolean isExpired();
}
