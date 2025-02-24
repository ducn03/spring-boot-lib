package com.springboot.prj.route;

import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.function.RouterFunction;
import org.springframework.web.servlet.function.ServerResponse;

import static org.springframework.web.servlet.function.RouterFunctions.route;

@Component
public class Router {
//    @Bean
//    public RouterFunction<ServerResponse> allApplicationRouters() {
//        return route().add(
//                route()
//                // Thêm ROUTE ngay đây
//                        //example
//                        // .GET(RouteConstant.APP.PING.APP_PING, pingController::ping)
//                        .build()
//
//        ).build();
//    }
}
