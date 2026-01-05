package com.springboot.prj.route;

public interface RouteConstant {
    interface APP {

        interface LOGIN {
            String APP_LOGIN = "/app/login";
            String APP_LOGOUT = "/app/logout";
        }

        interface USER {
            String USERS = "/app/users";
            String USER = "/app/users/{userId}";
            String CREATE = "/app/users";
            String UPDATE = "/app/users/{userId}";
            String STATUS_CHANGE = "/app/users/{userId}/status-change";
        }
    }
}
