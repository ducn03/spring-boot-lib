package com.springboot.prj.features.test.cache;

import com.springboot.jpa.repository.HttpLogRepository;
import com.springboot.lib.cache.LazyCache;
import com.springboot.lib.service.log.HttpLogResponse;
import com.springboot.lib.service.log.HttpLogService;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class LogCache extends LazyCache<List<HttpLogResponse>> {
    private final HttpLogService httpLogService;

    public LogCache(HttpLogService httpLogService) {
        // Cache 5 phút (5 * 60 * 1000 ms)
        super(5 * 60 * 1000);
        this.httpLogService = httpLogService;
    }

    @Override
    public List<HttpLogResponse> load() {
        return httpLogService.getAll();
    }
}
