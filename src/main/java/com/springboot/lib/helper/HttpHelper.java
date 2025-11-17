package com.springboot.lib.helper;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.web.context.request.RequestAttributes;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;
import org.springframework.web.util.ContentCachingRequestWrapper;

import java.io.IOException;
import java.util.Collections;
import java.util.Enumeration;
import java.util.LinkedHashMap;
import java.util.Map;

public class HttpHelper {

    /**
     * Lấy HttpServletRequest hiện tại từ context
     */
    public static HttpServletRequest getCurrentHttpRequest() {
        RequestAttributes attrs = RequestContextHolder.getRequestAttributes();
        if (attrs instanceof ServletRequestAttributes servletAttrs) {
            return servletAttrs.getRequest();
        }
        return null;
    }

    /**
     * Lấy HttpServletResponse hiện tại từ context
     */
    public static HttpServletResponse getCurrentHttpResponse() {
        RequestAttributes attrs = RequestContextHolder.getRequestAttributes();
        if (attrs instanceof ServletRequestAttributes servletAttrs) {
            return servletAttrs.getResponse();
        }
        return null;
    }

    /**
     * Lấy method HTTP (GET, POST, ...)
     */
    public static String getMethodType(HttpServletRequest request) {
        if (request == null) {
            return "N/A";
        }
        return request.getMethod();
    }

    /**
     * Lấy URL đầy đủ bao gồm query string
     */
    public static String getFullUrl(HttpServletRequest request) {
        if (request == null) {
            return "N/A";
        }
        StringBuilder sb = new StringBuilder(request.getRequestURL());
        String query = request.getQueryString();
        if (query != null && !query.isEmpty()) {
            sb.append('?').append(query);
        }
        return sb.toString();
    }

    /**
     * Lấy URL đầy đủ bao gồm query string
     */
    public static String getPath(HttpServletRequest request) {
        if (request == null) {
            return "N/A";
        }
        return request.getRequestURI();
    }

    /**
     * Trích xuất tất cả header của request
     */
    public static Map<String, String> extractHeaders(HttpServletRequest request) {
        if (request == null) {
            return Collections.emptyMap();
        }

        Map<String, String> headers = new LinkedHashMap<>();
        Enumeration<String> names = request.getHeaderNames();
        while (names != null && names.hasMoreElements()) {
            String name = names.nextElement();
            headers.put(name, request.getHeader(name));
        }
        return headers;
    }

    /**
     * Đọc raw body từ ContentCachingRequestWrapper
     */
    public static String extractBody(HttpServletRequest request) {
        try {
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
        } catch (IOException ioException) {
            System.out.println("Exception error: " + ioException.getMessage());
            return "";
        }
    }

    public static String getClientIP(HttpServletRequest request) {
        String ip = request.getHeader("X-Forwarded-For");
        if (ip == null || ip.isEmpty() || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getHeader("Proxy-Client-IP");
        }
        if (ip == null || ip.isEmpty() || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getHeader("WL-Proxy-Client-IP");
        }
        if (ip == null || ip.isEmpty() || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getRemoteAddr();
        }

        // Nếu có nhiều IP (VD: "123.45.67.89, 10.0.0.1"), lấy IP đầu tiên
        if (ip != null && ip.contains(",")) {
            ip = ip.split(",")[0].trim();
        }

        return ip;
    }
}
