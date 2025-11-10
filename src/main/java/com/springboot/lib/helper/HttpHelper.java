package com.springboot.lib.helper;

import com.springboot.lib.dto.UserInfo;
import com.springboot.lib.exception.AppException;
import com.springboot.lib.exception.ErrorCodes;
import org.springframework.web.servlet.function.ServerRequest;

import java.util.Optional;

public class HttpHelper {
    public static<T> T body(ServerRequest request, Class<T> clazz) {
        try {
            String json = (String) request.attributes().get("request-body");
            return JsonHelper.toObject(json, clazz);
        } catch (Exception e) {
            throw new AppException(ErrorCodes.SYSTEM.BAD_REQUEST);
        }
    }

    public static UserInfo getUserInfo(ServerRequest request) {
        Optional<Object> userInfoOptional = request.attribute("user-info");
        if (userInfoOptional.isEmpty()) {
            throw new AppException(ErrorCodes.SYSTEM.BAD_REQUEST);
        }
        return (UserInfo) userInfoOptional.get();
    }
}
