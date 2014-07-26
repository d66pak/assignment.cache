package assignment.cache;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

/**
 * <p>This class provides a skeletal implementation of the {@code CacheManager} interface to minimize the
 * effort required to implement this interface.
 *
 * <p>To implement {@code CacheManager} interface, extend this class and implement {@link #get(Object)} method
 * If required, default implementations can also be overridden
 *
 * @param <K> Key type to be stored in cache
 * @param <V> Value type to be stored in cache
 *
 * @author Deepak Telkar
 * Created by dtelkar on 7/25/14.
 */
public abstract class AbstractCacheManager<K, V> implements CacheManager<K,V> {

    protected Cache mCache;
    protected CacheBuilder<K, V> mCacheBuilder;
    // ScheduledExecutorService used for periodically purging the cache
    protected final ScheduledExecutorService mScheduler = Executors.newScheduledThreadPool(1);

    public AbstractCacheManager(CacheBuilder<K, V> cacheBuilder, Cache cacheImpl) {

        mCacheBuilder = cacheBuilder;
        mCache = cacheImpl;
        startPurging();
    }

    @Override
    public void clearCache() {

        mCache.clear();
    }

    @Override
    public boolean containsKey(Object key) {

        return mCache.containsKey(key);
    }

    @Override
    public V get(Object key) {

        CacheObject<V> co = (CacheObject<V>) mCache.get(key);
        if (co == null) {

            return null;
        }
        return co.getCache();
    }

    @Override
    public boolean isCacheEmpty() {

        return mCache.isEmpty();
    }

    @Override
    public V remove(Object key) {

        CacheObject<V> co = (CacheObject<V>) mCache.remove(key);
        if (co == null) {

            return null;
        }
        return co.getCache();
    }

    @Override
    public int cacheSize() {

        return mCache.size();
    }

    @Override
    public void shutdown() {

        mScheduler.shutdown();
    }

    /**
     * Cache clean up logic
     */
    private void startPurging() {

        mScheduler.scheduleAtFixedRate(new Runnable() {
            @Override
            public void run() {

                mCache.purge();
            }
        }, 0, mCacheBuilder.getPurgeFrequencyInMilliSec(), TimeUnit.MILLISECONDS);
    }
}
