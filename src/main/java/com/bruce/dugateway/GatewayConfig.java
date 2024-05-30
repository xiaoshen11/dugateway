package com.bruce.dugateway;

import com.bruce.durpc.core.api.RegistryCenter;
import com.bruce.durpc.core.registry.du.DuRegistryCenter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.ApplicationRunner;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.handler.SimpleUrlHandlerMapping;

import java.util.Map;
import java.util.Properties;

import static com.bruce.dugateway.GatewayPlugin.GATEWAY_PREFIX;

/**
 * @date 2024/5/21
 */
@Configuration
public class GatewayConfig {

    @Bean
    public RegistryCenter rc(){
        return new DuRegistryCenter();
    }

    @Bean
    ApplicationRunner runner(@Autowired ApplicationContext context){
        return args -> {
            SimpleUrlHandlerMapping handlerMapping = context.getBean(SimpleUrlHandlerMapping.class);
            Properties mappings = new Properties();
            mappings.put(GATEWAY_PREFIX + "/**","gatewayWebHandler");
            handlerMapping.setMappings(mappings);
            handlerMapping.initApplicationContext();
            System.out.println("durpc gateway start");
        };
    }

}
