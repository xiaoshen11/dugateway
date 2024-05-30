package com.bruce.dugateway.plugin;

import com.bruce.dugateway.AbstractGatewayPlugin;
import com.bruce.durpc.core.api.LoadBalancer;
import com.bruce.durpc.core.api.RegistryCenter;
import com.bruce.durpc.core.cluster.RandomRobinLoadBalancer;
import com.bruce.durpc.core.meta.InstanceMeta;
import com.bruce.durpc.core.meta.ServiceMeta;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.buffer.DataBuffer;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.List;

/**
 * @date 2024/5/30
 */
@Component("directPlugin")
public class DirectPlugin extends AbstractGatewayPlugin {

    public final static String NAME = "direct";
    private String prefix = GATEWAY_PREFIX + "/" + NAME +"/";

    @Override
    public Mono<Void> doHandle(ServerWebExchange exchange) {
        System.out.println("======== [DirectPlugin] ...");
        String backend = exchange.getRequest().getQueryParams().getFirst("backend");
        Flux<DataBuffer> requestBody = exchange.getRequest().getBody();

        exchange.getResponse().getHeaders().add("Content-Type", "application/json");
        exchange.getResponse().getHeaders().add("du.gw.version", "v1.0.0");
        exchange.getResponse().getHeaders().add("du.gw.plugin", getName());

        if(backend == null || backend.isEmpty()){
            return requestBody.flatMap(x -> exchange.getResponse().writeWith(Mono.just(x))).then();
        }

        WebClient client = WebClient.create(backend);
        Mono<ResponseEntity<String>> entity = client.post().header("Content-Type", "application/json")
                .body(requestBody, DataBuffer.class).retrieve().toEntity(String.class);

        Mono<String> body = entity.map(ResponseEntity::getBody);

        return body.flatMap(x -> exchange.getResponse()
                .writeWith(Mono.just(exchange.getResponse().bufferFactory().wrap(x.getBytes()))));
    }

    @Override
    public boolean doSupport(ServerWebExchange exchange) {
        return exchange.getRequest().getPath().value().startsWith(prefix);
    }

    @Override
    public String getName() {
        return NAME;
    }
}
