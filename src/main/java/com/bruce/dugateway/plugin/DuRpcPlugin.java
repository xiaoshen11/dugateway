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
@Component("durpcPlugin")
public class DuRpcPlugin extends AbstractGatewayPlugin {

    public final static String NAME = "durpc";
    private String prefix = GATEWAY_PREFIX + "/" + NAME +"/";

    @Autowired
    RegistryCenter rc;

    LoadBalancer<InstanceMeta> loadBalancer = new RandomRobinLoadBalancer();

    @Override
    public Mono<Void> doHandle(ServerWebExchange exchange) {
        System.out.println("======== [DuRpcPlugin] ...");
        // 1.通过请求路径获得服务名
        String service = exchange.getRequest().getPath().value().substring(prefix.length());
        ServiceMeta serviceMeta = ServiceMeta.builder().name(service)
                .app("app1").env("dev").namespace("public").build();
        // 2.通过rc拿到所有活着的服务实例
        List<InstanceMeta> instanceMetas = rc.fetchAll(serviceMeta);

        // 3.通过负载均衡，获得实例url
        InstanceMeta instanceMeta = loadBalancer.choose(instanceMetas);
        String url = instanceMeta.toUrl();


        // 4.拿到请求的报文
        Flux<DataBuffer> requestBody = exchange.getRequest().getBody();

        // 5.通过webclient发送post请求
        WebClient client = WebClient.create(url);
        Mono<ResponseEntity<String>> entity = client.post().header("Content-Type", "application/json")
                .body(requestBody, DataBuffer.class).retrieve().toEntity(String.class);


        // 6.通过entity获取响应报文
        Mono<String> body = entity.map(ResponseEntity::getBody);
//        body.subscribe(source -> System.out.println("responce:" + source));
        // 7.组装响应报文
        exchange.getResponse().getHeaders().add("Content-Type", "application/json");
        exchange.getResponse().getHeaders().add("du.gw.version", "v1.0.0");
        exchange.getResponse().getHeaders().add("du.gw.plugin", getName());

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
