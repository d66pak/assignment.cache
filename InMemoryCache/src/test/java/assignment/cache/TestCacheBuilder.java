package assignment.cache;

import org.testng.Assert;
import org.testng.annotations.Test;

import java.util.ArrayList;
import java.util.Random;
import java.util.UUID;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

/**
 * Created by dtelkar on 7/26/14.
 */
public class TestCacheBuilder {

    @Test(enabled = true)
    public void basicDefaultParameterTest() {

        CacheBuilder<Long, String> builder = CacheBuilder.newBuilder();
        CacheManager<Long, String> cm = builder.build();

        Assert.assertEquals(builder.getCacheAlgorithm(), CacheBuilder.CacheAlgorithm.TIMED_CACHE);
        Assert.assertEquals(builder.getCacheType(), CacheBuilder.CacheType.IN_MEMORY_CACHE);
        Assert.assertEquals(builder.getInitialCapacity(), 100);
        Assert.assertEquals(builder.getMilliSecondsToLive(), 10000);
        Assert.assertEquals(builder.getPurgeFrequencyInMilliSec(), 5000);

        cm.shutdown();
    }

    @Test(enabled = true)
    public void basicParameterTest() {

        CacheBuilder<Long, String> builder = CacheBuilder.newBuilder()
                .cacheAlgorithm(CacheBuilder.CacheAlgorithm.TIMED_CACHE)
                .cacheType(CacheBuilder.CacheType.IN_MEMORY_CACHE)
                .initialCapacity(200)
                .purgeFrequency(10, TimeUnit.SECONDS)
                .timeToLive(11, TimeUnit.SECONDS);

        CacheManager<Long, String> cm = builder.build();

        Assert.assertEquals(builder.getCacheAlgorithm(), CacheBuilder.CacheAlgorithm.TIMED_CACHE);
        Assert.assertEquals(builder.getCacheType(), CacheBuilder.CacheType.IN_MEMORY_CACHE);
        Assert.assertEquals(builder.getInitialCapacity(), 200);
        Assert.assertEquals(builder.getPurgeFrequencyInMilliSec(), 10000);
        Assert.assertEquals(builder.getMilliSecondsToLive(), 11000);

        cm.shutdown();

    }

    @Test(enabled = true)
    public void basicGetPutTests() {

        CacheManager<Long, String> cm = CacheBuilder.newBuilder().build();

        Random random = new Random();

        long key = random.nextLong();
        String cache = UUID.randomUUID().toString();
        System.out.println("Cache : " + cache);

        // Key not present in cache
        String value = cm.get(key);
        Assert.assertNull(value);
        Assert.assertFalse(cm.containsKey(key));

        // Insert cache
        value = cm.put(key, cache);
        Assert.assertTrue(cm.containsKey(key));

        // Previous value should be null
        Assert.assertNull(value);

        // Get the value and check
        value = cm.get(key);
        Assert.assertEquals(value, cache);

        // Put a new key and check
        String newCache = UUID.randomUUID().toString();
        value = cm.put(key, newCache);
        Assert.assertEquals(value, cache);

        // Remove key and check
        value = cm.remove(key);
        Assert.assertEquals(value, newCache);

        cm.shutdown();

    }

    @Test(enabled = true)
    public void basicCacheTests() {

        CacheManager<Long, String> cm = CacheBuilder.newBuilder().build();

        // Cache should be empty
        Assert.assertTrue(cm.isCacheEmpty());

        // Cache size should be zero
        Assert.assertEquals(cm.cacheSize(), 0);

        // Insert elements
        Random random = new Random();

        for (int i = 0; i < 10; ++i) {

            String oldValue = cm.put(random.nextLong(), UUID.randomUUID().toString());
            Assert.assertNull(oldValue);
        }

        // Cache should not be empty
        Assert.assertFalse(cm.isCacheEmpty());

        // Cache should have 10 elements
        Assert.assertEquals(cm.cacheSize(), 10);

        // Clear the cache and check
        cm.clearCache();
        Assert.assertTrue(cm.isCacheEmpty());

        cm.shutdown();

    }

    @Test(enabled = true)
    public void purgeTest() {

        CacheManager<Long, String> cm = CacheBuilder.newBuilder()
                .timeToLive(2, TimeUnit.SECONDS)
                .purgeFrequency(1, TimeUnit.SECONDS)
                .build();

        // Insert elements
        Random random = new Random();

        for (int i = 0; i < 10; ++i) {

            long key = random.nextLong();
            cm.put(key, UUID.randomUUID().toString());
        }

        // Wait for some time and check if cache is purged
        try {
            Thread.sleep(4000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        // Cache should be empty by now
        Assert.assertTrue(cm.isCacheEmpty());

        cm.shutdown();
    }

    @Test(enabled = true)
    public void loadTest() throws InterruptedException {

        final int maxRecords = 1000;
        final int maxThreads = 100;
        final int ttl = 10;
        final int pFrequency = 2;

        // Generate list of keys for each thread to be operated upon
        final ArrayList<ArrayList<Long>> keyLists = new ArrayList<ArrayList<Long>>(maxThreads);
        final Random random = new Random();

        for (int i = 0 ; i < maxThreads; ++i) {

            ArrayList<Long> keylist = new ArrayList<Long>(maxRecords);
            for (int j = 0; j < maxRecords; ++j) {

                keylist.add(random.nextLong());
            }
            keyLists.add(keylist);
        }

        final CountDownLatch startGate = new CountDownLatch(1);
        final CountDownLatch endGate = new CountDownLatch(maxThreads);

        // Build cache for population
        final CacheManager<Long, String> cm = CacheBuilder.newBuilder()
                .timeToLive(ttl, TimeUnit.SECONDS)
                .purgeFrequency(pFrequency, TimeUnit.SECONDS)
                .build();

        for (int i = 0; i < maxThreads; ++i) {

            /**
             * Create n threads and stop them till all the threads are ready
             * using await() call
             */
            final int threadId = i;
            Thread t = new Thread() {

                @Override
                public void run() {

                    try {
                        startGate.await();
                    } catch (InterruptedException ignored) {}
                    try {
                        // insert keys to cache
                        ArrayList<Long> keylist = keyLists.get(threadId);
                        for (long key : keylist) {

                            cm.put(key, UUID.randomUUID().toString());
                        }
                    } finally {
                        endGate.countDown();
                    }
                }
            };
            t.start();
        }

        long startTime = System.nanoTime();
        System.out.println("Opening the start gate for put()...");

        // Open the startGate
        startGate.countDown();

        // While put is going on, try get
        System.out.println("Trying to get elements of cache... ");
        ExecutorService executorService = Executors.newFixedThreadPool(maxThreads);
        executorService.execute(new Runnable() {
            @Override
            public void run() {

                long startTime = System.currentTimeMillis();
                try {
                    for (;;) {
                        ArrayList<Long> keylist = keyLists.get(random.nextInt(maxThreads));
                        cm.get(keylist.get(random.nextInt(maxRecords)));
                        // run for ttl duration only
                        if (System.currentTimeMillis() > (startTime + ttl * 1000)) {

                            System.out.println("Exiting get thread...");
                            return;
                        }
                    }

                } finally {
                    System.out.println("get thread interrupted!");
                }
            }
        });

        // Wait till all the threads have completed
        endGate.await();
        executorService.shutdown();
        executorService.awaitTermination(ttl, TimeUnit.SECONDS);

        System.out.println("Number of elements in cache before ttl expiry: " + cm.cacheSize());
        System.out.println("Put operation took: " + (System.nanoTime() - startTime));

        // wait for some time for ttl to finish
        Thread.sleep(3000);
        System.out.println("Number of elements in cache after ttl expiry: " + cm.cacheSize());
        System.out.println("Overall test took: " + (System.nanoTime() - startTime));

        cm.shutdown();
    }

}
