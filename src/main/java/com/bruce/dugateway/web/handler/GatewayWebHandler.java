package com.bruce.dugateway.web.handler;

import com.bruce.dugateway.DefaultGatewayPluginChain;
import com.bruce.dugateway.GatewayFilter;
import com.bruce.dugateway.GatewayPlugin;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import org.springframework.web.server.WebHandler;
import reactor.core.publisher.Mono;

import java.util.List;

/**
 * @date 2024/5/23
 */
@Component("gatewayWebHandler")
public class GatewayWebHandler implements WebHandler {

    @Autowired
    List<GatewayPlugin> plugins;

    @Autowired
    List<GatewayFilter> filters;

    @Override
    public Mono<Void> handle(ServerWebExchange exchange) {
        System.out.println(" =====>>> Du Gateway web handler");
        if(plugins == null || plugins.isEmpty()){
            String mock = """
                    {"result": "no plugin"} 
                    """;
            return exchange.getResponse().writeWith(
                    Mono.just(exchange.getResponse().bufferFactory().wrap(mock.getBytes())));
        }

        for (GatewayFilter filter : filters) {
            filter.filter(exchange);
        }

        return new DefaultGatewayPluginChain(plugins).handle(exchange);
    }
}
