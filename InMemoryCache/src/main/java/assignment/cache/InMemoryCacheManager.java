package assignment.cache;

/**
 * <p>In memory cache manager implementation.
 *
 * @param <K> Key type to be stored in cache
 * @param <V> Value type to be stored in cache
 *
 * @author Deepak Telkar
 * Created by dtelkar on 7/25/14.
 */
public class InMemoryCacheManager<K, V> extends AbstractCacheManager<K, V> {

    public InMemoryCacheManager(CacheBuilder<K, V> cacheBuilder, Cache cacheImpl) {

        super(cacheBuilder, cacheImpl);
    }

    @Override
    public V put(K key, V value) {

        CacheObject<V> co = (CacheObject<V>) mCache.put(key, mCacheBuilder.buildCacheObject(value));

        if (co == null) {

            return null;
        }
        return co.getCache();
    }

}
