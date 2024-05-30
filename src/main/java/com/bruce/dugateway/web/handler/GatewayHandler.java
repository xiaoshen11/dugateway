package com.bruce.dugateway.web.handler;

import com.bruce.durpc.core.api.LoadBalancer;
import com.bruce.durpc.core.api.RegistryCenter;
import com.bruce.durpc.core.cluster.RandomRobinLoadBalancer;
import com.bruce.durpc.core.meta.InstanceMeta;
import com.bruce.durpc.core.meta.ServiceMeta;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.server.ServerRequest;
import org.springframework.web.reactive.function.server.ServerResponse;
import reactor.core.publisher.Mono;

import java.util.List;

/**
 * @date 2024/5/21
 */
@Component
public class GatewayHandler {

    @Autowired
    RegistryCenter rc;

    LoadBalancer<InstanceMeta> loadBalancer = new RandomRobinLoadBalancer();

    Mono<ServerResponse> handle(ServerRequest request){
        // 1.通过请求路径获得服务名
        String service = request.path().substring(4);
        ServiceMeta serviceMeta = ServiceMeta.builder().name(service)
                .app("app1").env("dev").namespace("public").build();
        // 2.通过rc拿到所有活着的服务实例
        List<InstanceMeta> instanceMetas = rc.fetchAll(serviceMeta);

        // 3.通过负载均衡，获得实例url
        InstanceMeta instanceMeta = loadBalancer.choose(instanceMetas);
        String url = instanceMeta.toUrl();


        // 4.拿到请求的报文
        Mono<String> requestMono = request.bodyToMono(String.class);

        // 5.通过webclient发送post请求
        WebClient client = WebClient.create(url);
        Mono<ResponseEntity<String>> entity = client.post().header("Content-Type", "application/json")
                .body(requestMono, String.class).retrieve().toEntity(String.class);


        // 6.通过entity获取响应报文
        Mono<String> body = entity.map(ResponseEntity::getBody);
//        body.subscribe(source -> System.out.println("responce:" + source));
        // 7.组装响应报文

        return ServerResponse.ok()
                .header("Content-Type", "application/json")
                .header("du.gw.version", "v1.0.0")
                .body(body, String.class);
    }
}
