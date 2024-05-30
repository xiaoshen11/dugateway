package com.bruce.dugateway.web.handler;

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

        for (GatewayPlugin plugin : plugins) {
            if(plugin.support(exchange)){
                return plugin.handle(exchange);
            }
        }

        String mock = """
                    {"result": "no supported plugin"} 
                    """;
        return exchange.getResponse().writeWith(
                Mono.just(exchange.getResponse().bufferFactory().wrap(mock.getBytes())));
    }
}
