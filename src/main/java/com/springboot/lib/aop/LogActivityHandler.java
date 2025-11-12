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

    public LogActivityHandler(MessageSource messageSource, HttpLogService httpLogService) {
        this.messageSource = messageSource;
        this.httpLogService = httpLogService;
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

            // HttpLog Request (1 dòng)
            if (request != null) {
                log.info("[REQUEST] method={} url={} ip={} targetMethod={} headers={} body={} args={}",
                        methodType,
                        HttpHelper.getFullUrl(request),
                        HttpHelper.getClientIP(request),
                        point.getSignature().toShortString(),
                        HttpHelper.extractHeaders(request),
                        HttpHelper.extractBody(request),
                        JsonHelper.convertArgsToJson(point.getArgs())
                );
            } else {
                log.warn("[REQUEST] request is null !!!");
            }

            Object result = point.proceed();

            // Duration
            long duration = System.currentTimeMillis() - startTime;

            // HttpLog Response (1 dòng)
            HttpServletResponse response = HttpHelper.getCurrentHttpResponse();
            log.info("[RESPONSE] status={} duration={}ms result={} ",
                    response != null ? response.getStatus() : "N/A",
                    duration,
                    JsonHelper.toJson(result)
            );

            // Lưu db để test - Gỡ để giảm latancy
//            HttpLogRequest httpLogRequest = new HttpLogRequest();
//            if (request != null) {
//                httpLogRequest.setIp(HttpHelper.getClientIP(request));
//            }
//            httpLogRequest.setMethod(methodType);
//            httpLogRequest.setUrl(HttpHelper.getFullUrl(request));
//            httpLogRequest.setTargetMethod(point.getSignature().toShortString());
//            httpLogRequest.setHeaders(HttpHelper.extractHeaders(request).toString());
//            if (request != null) {
//                httpLogRequest.setBody(HttpHelper.extractBody(request));
//            }
//            httpLogRequest.setArgs(JsonHelper.convertArgsToJson(point.getArgs()));
//            httpLogRequest.setStatusCode(response != null ? response.getStatus() : -1);
//            httpLogRequest.setDuration(duration);
//            httpLogRequest.setResult(JsonHelper.toJson(result));
//            httpLogService.log(httpLogRequest);

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