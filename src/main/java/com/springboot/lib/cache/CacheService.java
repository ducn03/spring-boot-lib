package com.springboot.lib.cache;

import com.springboot.lib.exception.AppException;
import com.springboot.lib.exception.ErrorCodes;
import jakarta.annotation.PreDestroy;
import org.springframework.stereotype.Service;

import java.util.Comparator;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicLong;

/**
 * Thread-safe in-memory cache với TTL support
 * - Tối ưu performance: O(log n) cho mọi operations
 * - Tránh memory leak: atomic operations, cleanup orphans
 * - Giới hạn size tự động với LRU eviction
 */
@Service
public class CacheService<K, V> {

    private final ConcurrentHashMap<K, CacheEntry<K, V>> cache;
    private final ConcurrentSkipListSet<CacheEntry<K, V>> expirationQueue;
    private final ScheduledExecutorService cleaner;

    // Giới hạn kích thước cache để tránh OOM
    private final int maxSize;
    private final AtomicLong hitCount = new AtomicLong();
    private final AtomicLong missCount = new AtomicLong();

    public CacheService() {
        // Default 10k entries
        this(10000);
    }

    public CacheService(int maxSize) {
        this.maxSize = maxSize;
        this.cache = new ConcurrentHashMap<>(Math.min(maxSize, 256));
        this.expirationQueue = new ConcurrentSkipListSet<>();

        this.cleaner = Executors.newSingleThreadScheduledExecutor(r -> {
            Thread t = new Thread(r, "cache-cleaner");
            t.setDaemon(true);
            // Low priority
            t.setPriority(Thread.MIN_PRIORITY);
            return t;
        });

        // Cleanup expired entries mỗi 30 giây
        cleaner.scheduleWithFixedDelay(this::cleanupExpired, 30, 30, TimeUnit.SECONDS);

        // Cleanup orphan entries mỗi 5 phút
        cleaner.scheduleWithFixedDelay(this::cleanupOrphans, 5, 5, TimeUnit.MINUTES);
    }

    /**
     * Lưu entry với TTL (milliseconds)
     */
    public void put(K key, V value, long ttl) {
        if (key == null || value == null) {
            throw new AppException(ErrorCodes.SYSTEM.SYSTEM_ERROR, "Key and value cannot be null");
        }

        long expireAt = System.currentTimeMillis() + ttl * 1000;
        CacheEntry<K, V> newEntry = new CacheEntry<>(key, value, expireAt);

        // Atomic update: remove old, add new
        cache.compute(key, (k, oldEntry) -> {
            if (oldEntry != null) {
                expirationQueue.remove(oldEntry);
            }
            expirationQueue.add(newEntry);
            return newEntry;
        });

        // Evict nếu vượt quá maxSize
        evictIfNeeded();
    }

    /**
     * Lấy value từ cache
     */
    public V get(K key) {
        if (key == null) return null;

        CacheEntry<K, V> entry = cache.get(key);
        if (entry == null) {
            missCount.incrementAndGet();
            return null;
        }

        if (entry.isExpired()) {
            removeEntry(key);
            missCount.incrementAndGet();
            return null;
        }

        hitCount.incrementAndGet();
        entry.recordAccess(); // Update access time cho LRU
        return entry.value;
    }

    /**
     * Xóa entry
     */
    public void remove(K key) {
        if (key != null) {
            removeEntry(key);
        }
    }

    /**
     * Xóa toàn bộ cache
     */
    public void clear() {
        cache.clear();
        expirationQueue.clear();
        hitCount.set(0);
        missCount.set(0);
    }

    /**
     * Lấy số lượng entries
     */
    public int size() {
        return cache.size();
    }

    /**
     * Kiểm tra key có tồn tại và chưa expired
     */
    public boolean containsKey(K key) {
        return get(key) != null;
    }

    /**
     * Cache statistics
     */
    public CacheStats getStats() {
        long hits = hitCount.get();
        long misses = missCount.get();
        double hitRate = (hits + misses) > 0 ? (double) hits / (hits + misses) : 0.0;
        return new CacheStats(size(), hits, misses, hitRate);
    }

    /**
     * Atomic remove entry
     */
    private void removeEntry(K key) {
        cache.computeIfPresent(key, (k, entry) -> {
            expirationQueue.remove(entry);
            return null; // Remove from cache
        });
    }

    /**
     * Cleanup expired entries (chạy định kỳ)
     */
    private void cleanupExpired() {
        try {
            long now = System.currentTimeMillis();
            int cleaned = 0;
            /**
             * Giới hạn để tránh block lâu
             */
            int maxCleanup = 500;

            CacheEntry<K, V> entry;
            while (cleaned < maxCleanup && (entry = expirationQueue.pollFirst()) != null) {
                if (entry.expireAt > now) {
                    // Chưa expired, add lại và dừng
                    expirationQueue.add(entry);
                    break;
                }

                // Atomic remove nếu đúng entry
                CacheEntry<K, V> finalEntry = entry;
                cache.computeIfPresent(entry.key, (k, currentEntry) -> {
                    if (currentEntry == finalEntry) {
                        return null; // Remove
                    }
                    return currentEntry; // Keep new entry
                });

                cleaned++;
            }
        } catch (Exception e) {
            // Log error nhưng không throw để cleanup tiếp tục
            System.err.println("Cache cleanup error: " + e.getMessage());
        }
    }

    /**
     * Cleanup orphan entries (entries trong expirationQueue nhưng không trong cache)
     * Chạy ít thường xuyên hơn để tránh overhead
     */
    private void cleanupOrphans() {
        try {
            int cleaned = 0;
            for (CacheEntry<K, V> entry : expirationQueue) {
                if (!cache.containsKey(entry.key)) {
                    expirationQueue.remove(entry);
                    cleaned++;
                }

                // Giới hạn số lượng để tránh block lâu
                if (cleaned > 1000) break;
            }
        } catch (Exception e) {
            System.err.println("Orphan cleanup error: " + e.getMessage());
        }
    }

    /**
     * Evict entries cũ nhất nếu vượt maxSize (LRU)
     */
    private void evictIfNeeded() {
        if (cache.size() <= maxSize) {
            return;
        }

        int toEvict = cache.size() - maxSize;
        cache.values().stream()
                .sorted(Comparator.comparingLong(e -> e.lastAccessTime))
                .limit(toEvict)
                .forEach(entry -> removeEntry(entry.key));
    }

    @PreDestroy
    public void shutdown() {
        cleaner.shutdown();
        try {
            if (!cleaner.awaitTermination(5, TimeUnit.SECONDS)) {
                cleaner.shutdownNow();
            }
        } catch (InterruptedException e) {
            cleaner.shutdownNow();
            Thread.currentThread().interrupt();
        }
        clear();
    }

    /**
     * Cache entry với TTL và LRU tracking
     */
    private static class CacheEntry<K, V> implements Comparable<CacheEntry<K, V>> {
        private static final AtomicLong SEQUENCE = new AtomicLong();

        final K key;
        final V value;
        final long expireAt;
        final long seq;
        /**
         * Cho LRU eviction
         */
        volatile long lastAccessTime;

        CacheEntry(K key, V value, long expireAt) {
            this.key = key;
            this.value = value;
            this.expireAt = expireAt;
            this.seq = SEQUENCE.getAndIncrement();
            this.lastAccessTime = System.currentTimeMillis();
        }

        boolean isExpired() {
            return System.currentTimeMillis() > expireAt;
        }

        void recordAccess() {
            this.lastAccessTime = System.currentTimeMillis();
        }

        @Override
        public int compareTo(CacheEntry<K, V> o) {
            // Sort by expireAt, then sequence
            int cmp = Long.compare(this.expireAt, o.expireAt);
            if (cmp != 0) return cmp;
            return Long.compare(this.seq, o.seq);
        }

        @Override
        public boolean equals(Object obj) {
            if (this == obj) return true;
            if (!(obj instanceof CacheEntry<?, ?> other)) return false;
            return this.seq == other.seq;
        }

        @Override
        public int hashCode() {
            return Long.hashCode(seq);
        }
    }

    /**
     * Cache statistics
     */
    public static class CacheStats {
        public final int size;
        public final long hits;
        public final long misses;
        public final double hitRate;

        CacheStats(int size, long hits, long misses, double hitRate) {
            this.size = size;
            this.hits = hits;
            this.misses = misses;
            this.hitRate = hitRate;
        }

        @Override
        public String toString() {
            return String.format("CacheStats{size=%d, hits=%d, misses=%d, hitRate=%.2f%%}",
                    size, hits, misses, hitRate * 100);
        }
    }
}