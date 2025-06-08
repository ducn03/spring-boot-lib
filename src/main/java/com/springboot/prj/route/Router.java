package com.springboot.prj.route;

import com.springboot.prj.features.notify.NotifyController;
import com.springboot.prj.features.user.UserController;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.function.RouterFunction;
import org.springframework.web.servlet.function.ServerResponse;

import static org.springframework.web.servlet.function.RouterFunctions.route;

@Component
public class Router {
    private final UserController userController;
    private final NotifyController notifyController;

    public Router(UserController userController, NotifyController notifyController) {
        this.userController = userController;
        this.notifyController = notifyController;
    }

    @Bean
    public RouterFunction<ServerResponse> allApplicationRouters() {
        return route().add(
                route()
                // Thêm ROUTE ngay đây
                        //example
                        .GET(RouteConstant.APP.USER.USERS, userController::getUsers)
                        .GET(RouteConstant.APP.USER.USER, userController::getUsers)
                        // Send noti
                        .POST(RouteConstant.APP.NOTIFY.NOTIFY, notifyController::sendNoti)
                        .build()

        ).build();
    }
}
