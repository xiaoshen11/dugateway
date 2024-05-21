package com.bruce.dugateway;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.server.HandlerFunction;
import org.springframework.web.reactive.function.server.ServerRequest;
import org.springframework.web.reactive.function.server.ServerResponse;
import reactor.core.publisher.Mono;

/**
 * @date 2024/5/21
 */
@Component
public class HelloHandler {


    Mono<ServerResponse> handle(ServerRequest request){
        String url = "http://localhost:8081/durpc";
        String requestJson = """
                {
                    "service": "com.bruce.durpc.demo.api.UserService",
                    "methodSign": "findById@1_int",
                    "args": [100]
                }
                """;

        WebClient client = WebClient.create(url);
        Mono<ResponseEntity<String>> entity = client.post().header("Content-Type", "application/json")
                .bodyValue(requestJson).retrieve().toEntity(String.class);
        Mono<String> body = entity.map(ResponseEntity::getBody);
        body.subscribe(source -> System.out.println("responce:" + source));
        return ServerResponse.ok()
                .header("Content-Type", "application/json")
                .header("du.gw.version", "v1.0.0")
                .body(body, String.class);
    }
}
