package com.springboot.prj.route;

import com.springboot.prj.features.user.UserController;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.function.RouterFunction;
import org.springframework.web.servlet.function.ServerResponse;

import static org.springframework.web.servlet.function.RouterFunctions.route;

@Component
public class Router {
    private final UserController userController;

    public Router(UserController userController) {
        this.userController = userController;
    }

    @Bean
    public RouterFunction<ServerResponse> allApplicationRouters() {
        return route().add(
                route()
                // Thêm ROUTE ngay đây
                        //example
                        .GET(RouteConstant.APP.USER.USERS, userController::getUsers)
                        .GET(RouteConstant.APP.USER.USER, userController::getUsers)
                        .build()

        ).build();
    }
}
