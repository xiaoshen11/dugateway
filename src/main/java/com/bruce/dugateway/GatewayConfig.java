package com.bruce.dugateway;

import com.bruce.durpc.core.api.RegistryCenter;
import com.bruce.durpc.core.registry.du.DuRegistryCenter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * @date 2024/5/21
 */
@Configuration
public class GatewayConfig {

    @Bean
    public RegistryCenter rc(){
        return new DuRegistryCenter();
    }

}
