package com.springboot.lib.service.log;

import com.springboot.jpa.domain.HttpLog;
import com.springboot.jpa.repository.HttpLogRepository;
import lombok.CustomLog;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@CustomLog
public class HttpLogService {
    private final HttpLogRepository httpLogRepository;

    public HttpLogService(HttpLogRepository httpLogRepository) {
        this.httpLogRepository = httpLogRepository;
    }

    public List<HttpLogResponse> getAll() {
        return httpLogRepository.findAll().stream()
                                          .map(this::buildResponse)
                                          .collect(Collectors.toList());
    }

    public boolean log(HttpLogRequest logRequest){
        try {
            httpLogRepository.save(buildRequest(logRequest));
            return true;
        } catch (Exception exception) {
            log.info("Log fail!");
            return false;
        }
    }

    private HttpLog buildRequest(HttpLogRequest logRequest) {
        if (logRequest == null) {
            return null;
        }

        HttpLog httpLog = new HttpLog();

        httpLog.setId(logRequest.getId());
        httpLog.setIp(logRequest.getIp());
        httpLog.setMethod(logRequest.getMethod());
        httpLog.setUrl(logRequest.getUrl());
        httpLog.setTargetMethod(logRequest.getTargetMethod());
        httpLog.setHeaders(logRequest.getHeaders());
        httpLog.setBody(logRequest.getBody());
        httpLog.setArgs(logRequest.getArgs());
        httpLog.setStatusCode(logRequest.getStatusCode());
        httpLog.setDuration(logRequest.getDuration());
        httpLog.setResult(logRequest.getResult());

        return httpLog;
    }

    private HttpLogResponse buildResponse(HttpLog httpLog) {
        if (httpLog == null) {
            return null;
        }

        HttpLogResponse response = new HttpLogResponse();

        response.setId(httpLog.getId());
        response.setIp(httpLog.getIp());
        response.setMethod(httpLog.getMethod());
        response.setUrl(httpLog.getUrl());
        response.setTargetMethod(httpLog.getTargetMethod());
        response.setHeaders(httpLog.getHeaders());
        response.setBody(httpLog.getBody());
        response.setArgs(httpLog.getArgs());
        response.setStatusCode(httpLog.getStatusCode());
        response.setDuration(httpLog.getDuration());
        // response.setResult(httpLog.getResult());

        return response;
    }
}
