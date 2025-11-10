package com.springboot.prj.features.test;

import com.springboot.lib.service.redis.Redis;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class TestController {

    private final Redis redis;

    public TestController(Redis redis) {
        this.redis = redis;
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
}
