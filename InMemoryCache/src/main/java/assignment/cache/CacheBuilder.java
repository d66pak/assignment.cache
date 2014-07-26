package assignment.cache;

import java.util.concurrent.TimeUnit;

/**
 *
 * <p>This is a builder class to create the instance of CacheManager by providing
 * configuration parameters, such as:
 *
 * <ul>
 *     <li>Cache Algorithm : Algorithm used for caching values @see CacheBuilder.CacheAlgorithm
 *     <li>Cache Type: Type of cache to use. Like in memory cache etc. @see CacheBuilder.CacheType
 *     <li>Initial capacity : Initial capacity of cache
 *     <li>Time to live : Time to live for each cache element
 *     <li>Purge frequency : Frequency of cleaning up the cache
 * </ul>
 *
 * <p>These features are optional; CacheManager can be created with default parameters as seen in the
 * below usage example.
 *
 * <p>Usage example 1 : <pre> {@code
 *
 * CacheManager<Long,String> cm = CacheBuilder.newBuilder().build();
 * }</pre>
 *
 * <p>Above example creates CacheManager with following defaults:
 *
 * <ul>
 *     <li>Cache Algorithm : Timed cache
 *     <li>Cache Type : In memory cache
 *     <li>Initial capacity : 100
 *     <li>Time to live : 10 seconds
 *     <li>Purge frequency : 5 seconds
 * </ul>
 *
 * <p>Usage example 2 : <pre> {@code
 *
 * CacheBuilder<Long,String> builder = CacheBuilder.newBuilder()
 *         .cacheAlgorithm(CacheBuilder.CacheAlgorithm.TIMED_CACHE)
 *         .cacheType(CacheBuilder.CacheType.IN_MEMORY_CACHE)
 *         .initialCapacity(200)
 *         .purgeFrequency(10, TimeUnit.SECONDS)
 *         .timeToLive(11, TimeUnit.SECONDS);
 * }</pre>
 *
 * @param <K> Key type for all the CacheManagers created by this builder
 * @param <V> Value type for all the CacheManagers created by this builder
 *
 * @author Deepak Telkar
 *
 * Created by dtelkar on 7/25/14.
 */
public class CacheBuilder<K, V> {

    /**
     * Cache type
     */
    public enum CacheType {

        IN_MEMORY_CACHE, // Cache stored in memory
    }

    /**
     * Cache Algorithm
     */
    public enum CacheAlgorithm {

        TIMED_CACHE, // Timed cache elements, removed when expired
    }

    private static final int DEFAULT_INITIAL_CAPACITY = 100;
    private static final long DEFAULT_TIME_TO_LIVE = 10000;
    private static final long DEFAULT_PURGE_FREQUENCY = 5000;

    private int mInitialCapacity;

    private long mMilliSecondsToLive;
    private long mPurgeFrequency;
    private CacheType mCacheType;
    private CacheAlgorithm mCacheAlgo;

    public int getInitialCapacity() {
        return mInitialCapacity;
    }

    public long getMilliSecondsToLive() {
        return mMilliSecondsToLive;
    }

    public long getPurgeFrequencyInMilliSec() {
        return mPurgeFrequency;
    }

    public CacheType getCacheType() {
        return mCacheType;
    }

    public CacheAlgorithm getCacheAlgorithm() {
        return mCacheAlgo;
    }

    /**
     * Static method to create an instance of CacheBuilder
     *
     * @return CacheBuilder<K,V> instance
     */
    public static CacheBuilder newBuilder() {

        return new CacheBuilder();
    }

    /**
     * Builder method to set time to live
     *
     * @param ttl Expiration time of cache element
     * @param unit Time unit
     * @return CacheBuilder<K,V> instance
     */
    public CacheBuilder<K, V> timeToLive(long ttl, TimeUnit unit) {

        if (ttl > 0) {

            mMilliSecondsToLive = unit.toMillis(ttl);
        }
        return this;
    }

    /**
     * Builder method to set cache cleanup frequency
     *
     * @param purgeFrequency Cache clean frequency
     * @param unit Time unit
     * @return CacheBuilder<K,V> instance
     */
    public CacheBuilder<K, V> purgeFrequency(long purgeFrequency, TimeUnit unit) {

        if (purgeFrequency > 0) {

            mPurgeFrequency = unit.toMillis(purgeFrequency);
        }
        return this;
    }

    /**
     * Builder method to set cache type
     *
     * @param cType @see CacheBuilder.CacheType
     * @return CacheBuilder<K,V> instance
     */
    public CacheBuilder<K, V> cacheType(CacheType cType) {

        mCacheType = cType;
        return this;
    }

    /**
     * Builder method to set cache algorithm
     *
     * @param cAlgo @see CacheBuilder.CacheAlgorithm
     * @return CacheBuilder<K,V> instance
     */
    public CacheBuilder<K, V> cacheAlgorithm(CacheAlgorithm cAlgo) {

        mCacheAlgo = cAlgo;
        return  this;
    }

    /**
     * Builder method to set the initial capacity of the cache
     *
     * @param initialCapacity Initial capacity
     * @return CacheBuilder<K,V> instance
     */
    public CacheBuilder<K, V> initialCapacity(int initialCapacity) {

        if (initialCapacity > 0) {

            mInitialCapacity = initialCapacity;
        }
        return this;
    }

    /**
     * Final build method to create CacheManager
     *
     * @return Specific CacheManager<K,V> implementation instance
     * @throws IllegalStateException If unsupported cache type is provided
     */
    public CacheManager<K, V> build() {

        CacheManager<K, V> cacheManagerImpl;

        switch (mCacheType) {

            case IN_MEMORY_CACHE: {

                cacheManagerImpl = new InMemoryCacheManager<K, V>(this,
                        new InMemoryCache<K, CacheObject<V>>(mInitialCapacity));
                break;
            }
            // TODO: Add other cache implementations here
            default: {

                throw new IllegalStateException("Unsupported cache type!");
            }

        }

        return cacheManagerImpl;
    }

    /**
     * Factory method to construct CacheObject
     *
     * @param value Value to be stored in cache
     * @return Specific CacheObject<V> implementation instance
     * @throws IllegalStateException If unsupported algorithm type is provided
     */
    public CacheObject<V> buildCacheObject(V value) {

        CacheObject<V> cacheObj;

        switch (mCacheAlgo) {

            case TIMED_CACHE: {

                cacheObj = new TimedCacheObject<V>(value, mMilliSecondsToLive);
                break;
            }
            // TODO: Add other algorithm implementations here
            default: {

                throw new IllegalStateException("Unsupported algorithm type!");
            }
        }

        return cacheObj;
    }

    /**
     * Private constructor
     */
    private CacheBuilder() {

        mInitialCapacity = DEFAULT_INITIAL_CAPACITY;
        mMilliSecondsToLive = DEFAULT_TIME_TO_LIVE;
        mPurgeFrequency = DEFAULT_PURGE_FREQUENCY;
        mCacheType = CacheType.IN_MEMORY_CACHE;
        mCacheAlgo = CacheAlgorithm.TIMED_CACHE;
    }

}
