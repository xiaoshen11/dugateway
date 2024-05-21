package com.bruce.dugateway;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.server.RouterFunction;
import org.springframework.web.reactive.function.server.ServerResponse;

import static org.springframework.web.reactive.function.server.RequestPredicates.GET;
import static org.springframework.web.reactive.function.server.RequestPredicates.POST;
import static org.springframework.web.reactive.function.server.RouterFunctions.route;

/**
 * @date 2024/5/21
 */
@Component
public class GatewayRouter {

    @Autowired
    HelloHandler helloHandler;
    @Autowired
    GatewayHandler gatewayHandler;

    @Bean
    public RouterFunction<?> userRouteFunction() {
        return route(GET("/hello"), helloHandler::handle);
    }

    @Bean
    public RouterFunction<?> gwRouteFunction() {
        return route(GET("/gw").or(POST("/gw/**")), gatewayHandler::handle);
    }
}
