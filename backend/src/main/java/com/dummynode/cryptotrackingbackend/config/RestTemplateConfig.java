/**
 * HuaiHao Mo
 */
package com.dummynode.cryptotrackingbackend.config;


import com.dummynode.cryptotrackingbackend.service.interceptor.ApiInterceptor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestTemplate;

import java.util.Collections;

@Configuration
public class RestTemplateConfig {

    @Bean
    public RestTemplate restTemplate() {

        RestTemplate restTemplate = new RestTemplate();
        restTemplate.setInterceptors(Collections.singletonList(new ApiInterceptor()));
        return restTemplate;
    }
}
