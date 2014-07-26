package assignment.cache;

/**
 * <p>Timed cache implementation where each cache value is alive for user defined time after which
 * {@link #isExpired()} method returns true so that both key and value could be removed form the cache while purging
 *
 * @param <V> Value type to be stored in cache
 *
 * @author Deepak Telkar
 * Created by dtelkar on 7/25/14.
 */
public class TimedCacheObject<V> implements CacheObject<V> {

    private final long mTimeOfCreation = System.currentTimeMillis();

    private final long mMilliSecondsToLive;

    private V mValue;

    public TimedCacheObject(V value, long milliSecondsToLive) {

        mValue = value;
        mMilliSecondsToLive = milliSecondsToLive;
    }

    @Override
    public V getCache() {

        return mValue;
    }

    /**
     * Method to decide if the cache value has expired
     *
     * @return {@code true} if value has crossed TTL, else {@code false}
     */
    @Override
    public boolean isExpired() {

        if (System.currentTimeMillis() > (mTimeOfCreation + mMilliSecondsToLive)) {

            return true;
        }

        return false;
    }
}
