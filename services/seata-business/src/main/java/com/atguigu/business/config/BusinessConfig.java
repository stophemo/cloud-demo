package com.atguigu.business.config;

import feign.Logger;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;


@Configuration
public class BusinessConfig {

    @Bean
    Logger.Level feignLoggingLevel() {
        return Logger.Level.FULL;
    }
}
