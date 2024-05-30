package com.bruce.dugateway;

import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

/**
 * gateway plugin chain.
 * @date 2024/5/30
 */
public interface GatewayPluginChain {

    Mono<Void> handle(ServerWebExchange exchange);


}
