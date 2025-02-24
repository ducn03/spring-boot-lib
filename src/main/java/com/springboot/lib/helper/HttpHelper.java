package com.springboot.lib.helper;

import com.springboot.lib.dto.UserInfo;
import com.springboot.lib.exception.AppThrower;
import com.springboot.lib.exception.ErrorCodes;
import org.springframework.web.servlet.function.ServerRequest;

import java.util.Optional;

public class HttpHelper {
    public static<T> T body(ServerRequest request, Class<T> clazz) {
        try {
            String json = (String) request.attributes().get("request-body");
            return JsonHelper.toObject(json, clazz);
        } catch (Exception e) {
            AppThrower.ep(ErrorCodes.SYSTEM.BAD_REQUEST);
            return null;
        }
    }

    public static UserInfo getUserInfo(ServerRequest request) {
        Optional<Object> userInfoOptional = request.attribute("user-info");
        if (userInfoOptional.isEmpty()) {
            AppThrower.ep(ErrorCodes.SYSTEM.BAD_REQUEST);
        }
        return (UserInfo) userInfoOptional.get();
    }
}
