package org.mjbot.config;

import com.kucoin.sdk.KucoinClientBuilder;
import com.kucoin.sdk.KucoinPublicWSClient;
import com.kucoin.sdk.KucoinRestClient;
import java.io.IOException;
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
