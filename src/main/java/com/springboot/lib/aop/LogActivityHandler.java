package com.springboot.lib.aop;

import com.springboot.lib.constant.RestConstant;
import com.springboot.lib.exception.AppException;
import com.springboot.lib.exception.ErrorCodes;
import com.springboot.lib.helper.ControllerHelper;
import com.springboot.lib.helper.HttpHelper;
import com.springboot.lib.helper.JsonHelper;
import com.springboot.lib.service.log.HttpLogRequest;
import com.springboot.lib.service.log.HttpLogService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.core.annotation.Order;
import org.springframework.core.task.TaskExecutor;
import org.springframework.stereotype.Component;
import java.lang.reflect.Method;
import java.util.*;

@Aspect
@Order(2)
@Component
@Slf4j
public class LogActivityHandler {

    private static final String[] UNAUTHENTIC_PATHS = new String[]{"POST-/app/login"};
    private static final String ERROR_MESSAGE_DEFAULT = "Message chưa được định nghĩa";
    private final MessageSource messageSource;
    private final HttpLogService httpLogService;
    private final TaskExecutor taskExecutor;

    public LogActivityHandler(MessageSource messageSource, HttpLogService httpLogService, TaskExecutor taskExecutor) {
        this.messageSource = messageSource;
        this.httpLogService = httpLogService;
        this.taskExecutor = taskExecutor;
    }

    @Around("@annotation(LogActivity)")
    public Object handle(ProceedingJoinPoint point) {
        long startTime = System.currentTimeMillis();
        try {
            LogActivity logActivity = getLogActivity(point);
            if (logActivity == null) {
                throw new AppException(ErrorCodes.SYSTEM.LOG_ACTIVITY_ERROR);
            }

            HttpServletRequest request = HttpHelper.getCurrentHttpRequest();
            setLocaleFromRequest(request);

            // Lấy method và endpoint
            String methodType = HttpHelper.getMethodType(request);
            String path = HttpHelper.getPath(request);

            // Những endpoint ngoại lệ ko cần validate
            if (isUnauthenticPath(methodType, path)) {
                return point.proceed();
            }

            // ========== EXTRACT DATA SYNC ==========
            final String ip = request != null ? HttpHelper.getClientIP(request) : null;
            final String fullUrl = HttpHelper.getFullUrl(request);
            final String targetMethod = point.getSignature().toShortString();
            final String headers = request != null ? HttpHelper.extractHeaders(request).toString() : null;
            final String body = request != null ? HttpHelper.extractBody(request) : null;
            final String args = JsonHelper.convertArgsToJson(point.getArgs());

            // Log request
            taskExecutor.execute(() -> {
                if (request != null) {
                    log.info("[REQUEST] method={} url={} ip={} targetMethod={} headers={} body={} args={}",
                            methodType,
                            fullUrl,
                            ip,
                            targetMethod,
                            headers,
                            body,
                            args
                    );
                } else {
                    log.warn("[REQUEST] request is null !!!");
                }
            });

            Object result = point.proceed();

            // ========== EXTRACT RESPONSE DATA SYNC ==========
            HttpServletResponse response = HttpHelper.getCurrentHttpResponse();
            final int statusCode = response != null ? response.getStatus() : -1;
            final String resultJson = JsonHelper.toJson(result);

            // Duration
            long duration = System.currentTimeMillis() - startTime;

            // ========== LOG RESPONSE ==========
            taskExecutor.execute(() -> {
                log.info("[RESPONSE] status={} duration={}ms result={}",
                        statusCode,
                        duration,
                        resultJson
                );

                // Save to DB
                HttpLogRequest httpLogRequest = new HttpLogRequest();
                httpLogRequest.setIp(ip);
                httpLogRequest.setMethod(methodType);
                httpLogRequest.setUrl(fullUrl);
                httpLogRequest.setTargetMethod(targetMethod);
                httpLogRequest.setHeaders(headers);
                httpLogRequest.setBody(body);
                httpLogRequest.setArgs(args);
                httpLogRequest.setStatusCode(statusCode);
                httpLogRequest.setDuration(duration);
                httpLogRequest.setResult(resultJson);

                httpLogService.log(httpLogRequest);
            });

            return result;
        } catch (AppException appException) {
            int errorCode = appException.getErrorCode();
            log.error("AppException: code={} message={}", errorCode, getMessage(errorCode), appException);
            return ControllerHelper.error(errorCode, getMessage(errorCode));
        } catch (Throwable ex) {
            log.error("Unexpected error in LogActivityHandler", ex);
            return ControllerHelper.systemError();
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

    private String getMessage(int code) {
        if (messageSource == null) {
            return "MessageSource not initialized";
        }

        Locale locale = LocaleContextHolder.getLocale();
        return messageSource.getMessage(String.valueOf(code), null, ERROR_MESSAGE_DEFAULT, locale);
    }

    private void setLocaleFromRequest(HttpServletRequest request) {
        if (request == null) {
            return;
        }

        String lang = request.getParameter("lang");
        if (lang == null || lang.isEmpty()) {
            lang = request.getHeader("Accept-Language");
        }

        Locale locale;
        if (lang != null && lang.toLowerCase().startsWith(RestConstant.LANG.EN)) {
            locale = Locale.ENGLISH;
        } else {
            locale = new Locale(RestConstant.LANG.VI);
        }

        LocaleContextHolder.setLocale(locale);
    }

}