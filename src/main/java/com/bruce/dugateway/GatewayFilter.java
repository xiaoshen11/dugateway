package com.bruce.dugateway;

import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

/**
 * @date 2024/5/30
 */
public interface GatewayFilter {

    Mono<Void> filter(ServerWebExchange exchange);
}
