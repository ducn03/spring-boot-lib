package com.springboot.prj.features.test;

import com.springboot.lib.aop.LogActivity;
import com.springboot.lib.cache.CacheService;
import com.springboot.lib.helper.ControllerHelper;
import com.springboot.lib.service.log.HttpLogService;
import com.springboot.lib.service.redis.Redis;
import com.springboot.prj.features.test.cache.LogCache;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.Random;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicInteger;

@RestController
public class TestController {

    private final Redis redis;
    private final CacheService<String, String> cacheService;
    private final Random random = new Random();
    private final HttpLogService httpLogService;
    private final LogCache logCache;

    public TestController(Redis redis, CacheService<String, String> cacheService, HttpLogService httpLogService, LogCache logCache) {
        this.redis = redis;
        this.cacheService = cacheService;
        this.httpLogService = httpLogService;
        this.logCache = logCache;
    }

    /**
     * Lấy http log
     */
    @LogActivity
    @GetMapping("/log")
    public ResponseEntity<?> getLogs(@RequestParam(defaultValue = "false") boolean hasCache) {
        if (!hasCache) {
            return ControllerHelper.success(httpLogService.getAll());
        }
        return ControllerHelper.success(logCache.get());
    }

    /**
     * Benchmark 2 method check duplicate
     * @param key Redis key
     * @param ttl TTL in seconds
     * @param iterations number of times to run
     * @return result string with timing
     */
    @GetMapping("/benchmark")
    public String benchmark(@RequestParam(defaultValue = "key") String key,
                            @RequestParam(defaultValue = "10") long ttl,
                            @RequestParam(defaultValue = "100") int iterations) {

        // ===== Benchmark singleRequest (Lua script) =====
        long start1 = System.nanoTime();
        int hit1 = 0;
        for (int i = 0; i < iterations; i++) {
            if (redis.singleRequest(key + "_lua_" + i, ttl)) {
                hit1++;
            }
        }
        long time1 = System.nanoTime() - start1;

        // ===== Benchmark singleRequestHad (setIfAbsent) =====
        long start2 = System.nanoTime();
        int hit2 = 0;
        for (int i = 0; i < iterations; i++) {
            if (redis.singleRequestHad(key + "_set_" + i, ttl)) {
                hit2++;
            }
        }
        long time2 = System.nanoTime() - start2;

        return String.format("singleRequest (Lua): %d hits in %.2f ms\n" +
                        "singleRequestHad (setIfAbsent): %d hits in %.2f ms",
                hit1, time1 / 1_000_000.0,
                hit2, time2 / 1_000_000.0);
    }


    /**
     * Benchmark toàn diện cho CacheService
     * @param operations số lượng operations
     * @param threads số lượng threads đồng thời
     * @param ttl TTL trong giây
     * @return kết quả benchmark chi tiết
     */
    @GetMapping("/benchmark/cache")
    public String benchmarkCache(
            @RequestParam(defaultValue = "10000") int operations,
            @RequestParam(defaultValue = "10") int threads,
            @RequestParam(defaultValue = "60") long ttl) {

        StringBuilder result = new StringBuilder();
        result.append("╔═══════════════════════════════════════════════════════════════════╗\n");
        result.append("║          CACHE SERVICE - COMPREHENSIVE BENCHMARK                 ║\n");
        result.append("╚═══════════════════════════════════════════════════════════════════╝\n\n");

        // 1. Single-thread Performance Test
        result.append(singleThreadTest(operations, ttl));

        // 2. Multi-thread Concurrent Test
        result.append(multiThreadTest(operations, threads, ttl));

        // 3. Read/Write Ratio Test
        result.append(readWriteRatioTest(operations, ttl));

        // 4. High Contention Stress Test
        result.append(stressTest(operations, threads));

        // 5. TTL Expiration Test
        result.append(ttlExpirationTest());

        // 6. Memory Pressure Test (LRU Eviction)
        result.append(memoryPressureTest());

        // 7. LRU with Access Test (CORRECT)
        result.append(lruWithAccessTest());

        // 8. Cache Statistics
        result.append("\n");
        result.append("═".repeat(70)).append("\n");
        result.append("FINAL STATISTICS\n");
        result.append("═".repeat(70)).append("\n");
        result.append(cacheService.getStats().toString()).append("\n");

        return result.toString();
    }

    /**
     * Test 1: Single-thread performance baseline
     */
    private String singleThreadTest(int operations, long ttl) {
        StringBuilder sb = new StringBuilder();
        sb.append("1. SINGLE-THREAD PERFORMANCE TEST\n");
        sb.append("-".repeat(70)).append("\n");

        cacheService.clear();

        // Write test
        long writeStart = System.nanoTime();
        for (int i = 0; i < operations; i++) {
            cacheService.put("single_" + i, "value_" + i, ttl);
        }
        long writeTime = System.nanoTime() - writeStart;
        double writeOpsPerSec = operations / (writeTime / 1_000_000_000.0);

        // Read test
        long readStart = System.nanoTime();
        int hits = 0;
        for (int i = 0; i < operations; i++) {
            if (cacheService.get("single_" + i) != null) hits++;
        }
        long readTime = System.nanoTime() - readStart;
        double readOpsPerSec = operations / (readTime / 1_000_000_000.0);

        sb.append(String.format("  Write: %d ops in %.2f ms (%,.0f ops/sec)\n",
                operations, writeTime / 1_000_000.0, writeOpsPerSec));
        sb.append(String.format("  Read:  %d ops in %.2f ms (%,.0f ops/sec)\n",
                operations, readTime / 1_000_000.0, readOpsPerSec));
        sb.append(String.format("  Hit rate: %.2f%%\n\n", (hits * 100.0 / operations)));

        return sb.toString();
    }

    /**
     * Test 2: Multi-thread concurrent access
     */
    private String multiThreadTest(int operations, int threads, long ttl) {
        StringBuilder sb = new StringBuilder();
        sb.append("2. MULTI-THREAD CONCURRENT TEST\n");
        sb.append("-".repeat(70)).append("\n");

        cacheService.clear();

        ExecutorService executor = Executors.newFixedThreadPool(threads);
        CountDownLatch latch = new CountDownLatch(threads);
        AtomicInteger errors = new AtomicInteger(0);
        AtomicInteger successOps = new AtomicInteger(0);
        int opsPerThread = operations / threads;

        long start = System.nanoTime();

        for (int t = 0; t < threads; t++) {
            final int threadId = t;
            executor.submit(() -> {
                try {
                    for (int i = 0; i < opsPerThread; i++) {
                        String key = "multi_" + threadId + "_" + i;
                        cacheService.put(key, "value_" + i, ttl);
                        if (cacheService.get(key) != null) {
                            successOps.addAndGet(2); // 1 put + 1 get
                        }
                    }
                } catch (Exception e) {
                    errors.incrementAndGet();
                } finally {
                    latch.countDown();
                }
            });
        }

        try {
            latch.await();
            long time = System.nanoTime() - start;
            int totalOps = successOps.get();
            double opsPerSec = totalOps / (time / 1_000_000_000.0);

            sb.append(String.format("  Threads: %d\n", threads));
            sb.append(String.format("  Total ops: %d in %.2f ms\n", totalOps, time / 1_000_000.0));
            sb.append(String.format("  Throughput: %,.0f ops/sec\n", opsPerSec));
            sb.append(String.format("  Errors: %d\n", errors.get()));
            sb.append(String.format("  Final size: %d\n\n", cacheService.size()));
        } catch (InterruptedException e) {
            sb.append("  Test interrupted\n\n");
            Thread.currentThread().interrupt();
        } finally {
            executor.shutdown();
        }

        return sb.toString();
    }

    /**
     * Test 3: Read/Write ratio (80/20 rule)
     */
    private String readWriteRatioTest(int operations, long ttl) {
        StringBuilder sb = new StringBuilder();
        sb.append("3. READ/WRITE RATIO TEST (80% read, 20% write)\n");
        sb.append("-".repeat(70)).append("\n");

        cacheService.clear();

        // Pre-populate cache
        for (int i = 0; i < 1000; i++) {
            cacheService.put("ratio_" + i, "value_" + i, ttl);
        }

        long start = System.nanoTime();
        int reads = 0, writes = 0;

        for (int i = 0; i < operations; i++) {
            if (random.nextInt(100) < 80) {
                // 80% reads
                cacheService.get("ratio_" + random.nextInt(1000));
                reads++;
            } else {
                // 20% writes
                cacheService.put("ratio_" + random.nextInt(1000), "new_value_" + i, ttl);
                writes++;
            }
        }

        long time = System.nanoTime() - start;
        double opsPerSec = operations / (time / 1_000_000_000.0);

        sb.append(String.format("  Total ops: %d in %.2f ms\n", operations, time / 1_000_000.0));
        sb.append(String.format("  Reads: %d, Writes: %d\n", reads, writes));
        sb.append(String.format("  Throughput: %,.0f ops/sec\n\n", opsPerSec));

        return sb.toString();
    }

    /**
     * Test 4: Concurrent stress test với race conditions
     */
    private String stressTest(int operations, int threads) {
        StringBuilder sb = new StringBuilder();
        sb.append("4. CONCURRENT STRESS TEST\n");
        sb.append("-".repeat(70)).append("\n");

        cacheService.clear();

        ExecutorService executor = Executors.newFixedThreadPool(threads);
        CountDownLatch latch = new CountDownLatch(threads);
        AtomicInteger successOps = new AtomicInteger(0);
        AtomicInteger failedOps = new AtomicInteger(0);

        // Shared keys cho high contention
        String[] sharedKeys = {"hot_1", "hot_2", "hot_3", "hot_4", "hot_5"};

        long start = System.nanoTime();

        for (int t = 0; t < threads; t++) {
            executor.submit(() -> {
                try {
                    for (int i = 0; i < operations / threads; i++) {
                        String key = sharedKeys[random.nextInt(sharedKeys.length)];

                        // Random operations
                        int op = random.nextInt(4);
                        switch (op) {
                            case 0 -> cacheService.put(key, "value_" + i, 10);
                            case 1 -> cacheService.get(key);
                            case 2 -> cacheService.remove(key);
                            case 3 -> cacheService.containsKey(key);
                        }
                        successOps.incrementAndGet();
                    }
                } catch (Exception e) {
                    failedOps.incrementAndGet();
                } finally {
                    latch.countDown();
                }
            });
        }

        try {
            latch.await();
            long time = System.nanoTime() - start;

            sb.append(String.format("  Concurrent threads: %d\n", threads));
            sb.append(String.format("  Shared keys: %d\n", sharedKeys.length));
            sb.append(String.format("  Time: %.2f ms\n", time / 1_000_000.0));
            sb.append(String.format("  Successful ops: %d\n", successOps.get()));
            sb.append(String.format("  Failed ops: %d\n", failedOps.get()));
            sb.append(String.format("  Success rate: %.2f%%\n\n",
                    (successOps.get() * 100.0 / operations)));
        } catch (InterruptedException e) {
            sb.append("  Stress test interrupted\n\n");
            Thread.currentThread().interrupt();
        } finally {
            executor.shutdown();
        }

        return sb.toString();
    }

    /**
     * Test 5: TTL expiration accuracy
     */
    private String ttlExpirationTest() {
        StringBuilder sb = new StringBuilder();
        sb.append("5. TTL EXPIRATION TEST\n");
        sb.append("-".repeat(70)).append("\n");

        // Test với TTL ngắn (1 giây)
        cacheService.put("ttl_test", "value", 1);

        boolean existsBefore = cacheService.containsKey("ttl_test");

        try {
            Thread.sleep(1100); // Wait for expiration
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }

        boolean existsAfter = cacheService.containsKey("ttl_test");
        boolean ttlWorks = existsBefore && !existsAfter;

        sb.append(String.format("  Entry exists before TTL: %b\n", existsBefore));
        sb.append(String.format("  Entry exists after TTL: %b\n", existsAfter));
        sb.append(String.format("  TTL works correctly: %b %s\n\n",
                ttlWorks, ttlWorks ? "✅" : "❌"));

        return sb.toString();
    }

    /**
     * Test 6: Memory pressure & LRU eviction (Original benchmark)
     */
    private String memoryPressureTest() {
        StringBuilder sb = new StringBuilder();
        sb.append("6. MEMORY PRESSURE TEST (LRU Eviction)\n");
        sb.append("-".repeat(70)).append("\n");

        // Tạo cache nhỏ để test eviction
        CacheService<String, String> smallCache = new CacheService<>(100);

        // Fill cache beyond capacity
        for (int i = 0; i < 150; i++) {
            smallCache.put("key_" + i, "value_" + i, 3600);
            sleep(1);
        }

        int finalSize = smallCache.size();
        boolean evictionWorks = finalSize <= 100;

        // Check if old entry preserved (won't be - not accessed)
        boolean hasKey0 = smallCache.containsKey("key_0");

        sb.append(String.format("  Max size: 100\n"));
        sb.append(String.format("  Inserted: 150 entries\n"));
        sb.append(String.format("  Final size: %d\n", finalSize));
        sb.append(String.format("  Eviction works: %b %s\n",
                evictionWorks, evictionWorks ? "✅" : "❌"));
        sb.append(String.format("  LRU preserved old entry: %b\n", hasKey0));
        sb.append(String.format("  (Note: Old entry NOT accessed → correctly evicted)\n\n"));

        smallCache.shutdown();
        return sb.toString();
    }

    /**
     * Test 7: LRU with access (CORRECT TEST)
     */
    private String lruWithAccessTest() {
        StringBuilder sb = new StringBuilder();
        sb.append("7. LRU WITH ACCESS TEST (Correct verification)\n");
        sb.append("-".repeat(70)).append("\n");

        CacheService<String, String> testCache = new CacheService<>(100);

        // Add hot entry
        testCache.put("hot_key", "hot_value", 3600);
        sleep(50);

        // Add 50 cold entries
        for (int i = 0; i < 50; i++) {
            testCache.put("cold_" + i, "value_" + i, 3600);
            sleep(1);
        }

        // Access hot entry 10 times
        for (int i = 0; i < 10; i++) {
            testCache.get("hot_key");
            sleep(5);
        }

        // Add 100 more entries (total 151)
        for (int i = 50; i < 150; i++) {
            testCache.put("cold_" + i, "value_" + i, 3600);
            sleep(1);
        }

        boolean hotPreserved = testCache.containsKey("hot_key");
        boolean cold0Evicted = !testCache.containsKey("cold_0");
        boolean cold149Exists = testCache.containsKey("cold_149");
        int finalSize = testCache.size();
        boolean lruWorks = hotPreserved && cold0Evicted && finalSize <= 100;

        sb.append(String.format("  Hot key (accessed 10x): %s %s\n",
                hotPreserved ? "PRESERVED" : "EVICTED",
                hotPreserved ? "✅" : "❌"));
        sb.append(String.format("  Cold key (not accessed): %s %s\n",
                cold0Evicted ? "EVICTED" : "PRESERVED",
                cold0Evicted ? "✅" : "❌"));
        sb.append(String.format("  Recent key: %s %s\n",
                cold149Exists ? "EXISTS" : "EVICTED",
                cold149Exists ? "✅" : "❌"));
        sb.append(String.format("  Final size: %d (max: 100)\n", finalSize));
        sb.append(String.format("  LRU works correctly: %b %s\n\n",
                lruWorks, lruWorks ? "✅" : "❌"));

        testCache.shutdown();
        return sb.toString();
    }

    /**
     * Benchmark nhanh - chỉ test cơ bản
     */
    @GetMapping("/benchmark/cache/quick")
    public String quickBenchmark() {
        cacheService.clear();

        int ops = 1000;
        long start = System.nanoTime();

        for (int i = 0; i < ops; i++) {
            cacheService.put("quick_" + i, "value_" + i, 60);
        }

        for (int i = 0; i < ops; i++) {
            cacheService.get("quick_" + i);
        }

        long time = System.nanoTime() - start;
        double opsPerSec = (ops * 2) / (time / 1_000_000_000.0);

        return String.format("""
            ╔═══════════════════════════════════════════════════════════╗
            ║              QUICK CACHE BENCHMARK                        ║
            ╚═══════════════════════════════════════════════════════════╝
            
            Operations: %,d
            Time: %.2f ms
            Throughput: %,.0f ops/sec
            
            %s
            """,
                ops * 2, time / 1_000_000.0, opsPerSec, cacheService.getStats());
    }

    /**
     * Test các operations cơ bản
     */
    @GetMapping("/benchmark/cache/basic")
    public String basicOperations() {
        cacheService.clear();

        StringBuilder sb = new StringBuilder();
        sb.append("╔═══════════════════════════════════════════════════════════╗\n");
        sb.append("║           BASIC CACHE OPERATIONS TEST                     ║\n");
        sb.append("╚═══════════════════════════════════════════════════════════╝\n\n");

        // PUT
        long start = System.nanoTime();
        cacheService.put("test_key", "test_value", 60);
        long putTime = System.nanoTime() - start;
        sb.append(String.format("PUT: %.3f μs\n", putTime / 1000.0));

        // GET (hit)
        start = System.nanoTime();
        String value = cacheService.get("test_key");
        long getTime = System.nanoTime() - start;
        sb.append(String.format("GET (hit): %.3f μs\n", getTime / 1000.0));

        // GET (miss)
        start = System.nanoTime();
        cacheService.get("non_existent");
        long missTime = System.nanoTime() - start;
        sb.append(String.format("GET (miss): %.3f μs\n", missTime / 1000.0));

        // REMOVE
        start = System.nanoTime();
        cacheService.remove("test_key");
        long removeTime = System.nanoTime() - start;
        sb.append(String.format("REMOVE: %.3f μs\n", removeTime / 1000.0));

        // CONTAINS KEY
        cacheService.put("test_key", "test_value", 60);
        start = System.nanoTime();
        boolean exists = cacheService.containsKey("test_key");
        long containsTime = System.nanoTime() - start;
        sb.append(String.format("CONTAINS KEY: %.3f μs\n\n", containsTime / 1000.0));

        sb.append(cacheService.getStats().toString());

        return sb.toString();
    }

    /**
     * Helper method: sleep
     */
    private void sleep(long millis) {
        try {
            Thread.sleep(millis);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }

}
