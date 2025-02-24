package com.springboot.prj.route;

public interface RouteConstant {
    interface APP {

        interface LOGIN {
            String APP_LOGIN = "/app/login";
            String APP_LOGOUT = "/app/logout";
        }

        interface USER {
            String USER = "/app/user";
        }
    }
}
