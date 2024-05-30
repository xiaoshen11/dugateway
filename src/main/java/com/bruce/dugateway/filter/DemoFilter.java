package com.bruce.dugateway.filter;

import com.bruce.dugateway.GatewayFilter;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

/**
 * @date 2024/5/30
 */
@Component("demoFilter")
public class DemoFilter implements GatewayFilter {

    @Override
    public Mono<Void> filter(ServerWebExchange exchange) {
        System.out.println(" =====>>> demo filter ...");
        exchange.getRequest().getHeaders().toSingleValueMap()
                .forEach((k,v) -> System.out.println(k + ": " + v));
        return Mono.empty();
    }
}
