package com.playtika.carshop.client;

import feign.codec.ErrorDecoder;
import org.springframework.boot.autoconfigure.AutoConfigureAfter;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.cloud.netflix.feign.EnableFeignClients;
import org.springframework.cloud.netflix.feign.ribbon.FeignRibbonClientAutoConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@EnableFeignClients(clients = CarClient.class)
@AutoConfigureAfter(FeignRibbonClientAutoConfiguration.class)
@ConditionalOnMissingBean(CarClient.class)
public class ClientAutoConfiguration {
    @Bean
    public ErrorDecoder CarShopErrorDecoder() {
        return new CarShopErrorDecoder();
    }
}

