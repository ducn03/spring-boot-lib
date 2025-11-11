package com.springboot.lib.aop;

import ch.qos.logback.core.util.StringUtil;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.springboot.lib.exception.AppException;
import com.springboot.lib.exception.ErrorCodes;
import com.springboot.lib.helper.JsonHelper;
import com.springboot.lib.service.controller.ControllerService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestAttributes;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;
import org.springframework.web.util.ContentCachingRequestWrapper;

import java.io.IOException;
import java.lang.reflect.Method;
import java.util.*;

@Aspect
@Order(2)
@Component
@Slf4j
public class LogActivityHandler {

    private static final String[] UNAUTHENTIC_PATHS = new String[]{"POST-/app/login"};
    private static final ObjectMapper mapper = new ObjectMapper();
    private final ControllerService controllerService;

    public LogActivityHandler(ControllerService controllerService) {
        this.controllerService = controllerService;
    }

    @Around("@annotation(LogActivity)")
    public Object handle(ProceedingJoinPoint point) {
        long startTime = System.currentTimeMillis();
        try {
            LogActivity logActivity = getLogActivity(point);
            if (logActivity == null) {
                throw new AppException(ErrorCodes.SYSTEM.LOG_ACTIVITY_ERROR);
            }

            HttpServletRequest request = getCurrentHttpRequest();
            HttpServletResponse response = getCurrentHttpResponse();

            String methodType = request != null ? request.getMethod() : "N/A";
            String fullUrl = request != null ? request.getRequestURL().toString() +
                    (request.getQueryString() != null ? "?" + request.getQueryString() : "") : "N/A";

            if (isUnauthenticPath(methodType, fullUrl)) {
                return point.proceed();
            }

            // Log Request (1 dòng)
            if (request != null) {
                log.info("[REQUEST] method={} url={} targetMethod={} headers={} body={} args={}",
                        methodType,
                        fullUrl,
                        point.getSignature().toShortString(),
                        extractHeaders(request),
                        extractBody(request),
                        convertArgsToJson(point.getArgs())
                );
            } else {
                log.warn("[REQUEST] request is null !!!");
            }

            Object result = point.proceed();

            // Duration
            long duration = System.currentTimeMillis() - startTime;

            // Log Response (1 dòng)
            log.info("[RESPONSE] status={} result={} duration={}ms",
                    response != null ? response.getStatus() : "N/A",
                    JsonHelper.toJson(result),
                    duration
            );

            return result;
        } catch (AppException appException) {
            log.error("CdnException: code={} message={}", appException.getErrorCode(), appException.getErrorMessage(), appException);
            if (StringUtil.isNullOrEmpty(appException.getErrorMessage())) {
                return controllerService.error(appException.getErrorCode());
            }
            return controllerService.error(appException.getErrorCode(), appException.getErrorMessage());
        } catch (Throwable ex) {
            log.error("Unexpected error in LogActivityHandler", ex);
            return controllerService.systemError();
        }
    }

    private boolean isUnauthenticPath(String method, String path) {
        String fullString = method.toUpperCase() + "-" + path;
        return Arrays.asList(UNAUTHENTIC_PATHS).contains(fullString);
    }

    private LogActivity getLogActivity(ProceedingJoinPoint point) {
        MethodSignature signature = (MethodSignature) point.getSignature();
        Method method = signature.getMethod();
        return method.getAnnotation(LogActivity.class);
    }

    private HttpServletRequest getCurrentHttpRequest() {
        RequestAttributes requestAttributes = RequestContextHolder.getRequestAttributes();
        if (requestAttributes instanceof ServletRequestAttributes servletRequestAttributes) {
            return servletRequestAttributes.getRequest();
        }
        return null;
    }

    private HttpServletResponse getCurrentHttpResponse() {
        RequestAttributes requestAttributes = RequestContextHolder.getRequestAttributes();
        if (requestAttributes instanceof ServletRequestAttributes servletRequestAttributes) {
            return servletRequestAttributes.getResponse();
        }
        return null;
    }

    private Map<String, String> extractHeaders(HttpServletRequest request) {
        if (request == null) return Collections.emptyMap();
        Map<String, String> headers = new HashMap<>();
        Enumeration<String> headerNames = request.getHeaderNames();
        while (headerNames != null && headerNames.hasMoreElements()) {
            String name = headerNames.nextElement();
            headers.put(name, request.getHeader(name));
        }
        return headers;
    }

    private String extractBody(HttpServletRequest request) throws IOException {
        String contentType = request.getContentType();

        // Nếu là multipart thì không log raw body
        if (contentType != null && contentType.startsWith("multipart/")) {
            return "[multipart request: body skipped]";
        }

        // Nếu không phải multipart => dùng ContentCachingRequestWrapper
        if (request instanceof ContentCachingRequestWrapper wrapper) {
            byte[] buf = wrapper.getContentAsByteArray();
            if (buf.length > 0) {
                return new String(buf, 0, buf.length, wrapper.getCharacterEncoding());
            }
        }
        return "";
    }

    private String convertArgsToJson(Object[] args) {
        try {
            return mapper.writeValueAsString(args);
        } catch (Exception e) {
            return Arrays.toString(args);
        }
    }
}