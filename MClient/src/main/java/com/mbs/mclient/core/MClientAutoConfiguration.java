package com.mbs.mclient.core;

import com.mbs.mclient.controller.MClientController;
import org.springframework.cloud.client.loadbalancer.LoadBalanced;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestTemplate;

@Configuration
public class MClientAutoConfiguration {
    @Bean
    public MClientController mClientController() {
        return new MClientController();
    }

    @Bean
    @LoadBalanced
    RestTemplate initRestTemplate() {
        return new RestTemplate();
    }
}
