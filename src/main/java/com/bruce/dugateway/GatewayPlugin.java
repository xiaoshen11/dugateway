package com.bruce.dugateway;

import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

/**
 * gateway plugin
 * @date 2024/5/30
 */
public interface GatewayPlugin {

    public final static String GATEWAY_PREFIX = "/gw";

    void start();
    void stop();
    String getName();

    boolean support(ServerWebExchange exchange);

    Mono<Void> handle(ServerWebExchange exchange);

}
