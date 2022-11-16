package org.mjbot.client.kucoin.builder.rest;

import com.kucoin.sdk.KucoinClientBuilder;
import com.kucoin.sdk.KucoinRestClient;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class KucoinBuilder {

    @Bean
    public KucoinClientBuilder getKucoinClientBuilder() {
        KucoinClientBuilder builder = new KucoinClientBuilder();

        return builder;
    }

    @Bean
    public KucoinRestClient getKucoinRestClient(KucoinClientBuilder kucoinClientBuilder) {
        return kucoinClientBuilder.buildRestClient();
    }
}