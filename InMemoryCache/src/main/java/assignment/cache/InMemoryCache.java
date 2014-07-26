package assignment.cache;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

/**
 * <p>In memory cache implementation
 * Internally uses ConcurrentHashMap to store the cache
 *
 * @param <K> Key type to be stored in cache
 * @param <V> Value type to be stored in cache
 *
 * @author Deepak Telkar
 * Created by dtelkar on 7/25/14.
 */
class InMemoryCache<K, V> implements Cache<K,V> {

    private ConcurrentMap<K, V> mInMemoryCache;

    public InMemoryCache(int initialCapacity) {

        mInMemoryCache = new ConcurrentHashMap<K, V>(initialCapacity);
    }

    @Override
    public void clear() {

        mInMemoryCache.clear();
    }

    @Override
    public boolean containsKey(Object key) {

        return mInMemoryCache.containsKey(key);
    }

    @Override
    public V get(Object key) {

        return mInMemoryCache.get(key);
    }

    @Override
    public boolean isEmpty() {

        return mInMemoryCache.isEmpty();
    }

    @Override
    public V put(K key, V value) {

        return mInMemoryCache.put(key, value);
    }

    @Override
    public V remove(Object key) {

        return mInMemoryCache.remove(key);
    }

    @Override
    public int size() {

        return mInMemoryCache.size();
    }

    @Override
    public void purge() {

        for (ConcurrentMap.Entry<K, V> entry : mInMemoryCache.entrySet()) {

            if (((CacheObject)entry.getValue()).isExpired()) {

                // Cache entry has expired, remove it
                mInMemoryCache.remove(entry.getKey());
            }
        }
    }
}
