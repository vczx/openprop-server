package com.vi.openprop.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.client.reactive.ReactorClientHttpConnector;
import org.springframework.http.codec.ClientCodecConfigurer;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.netty.http.client.HttpClient;

import java.time.Duration;
import java.util.function.Consumer;

/**
 * Provide a web client to fetch the data from URA.
 */
@Configuration
public class WebConfig {

    public static final int MAX_MEM_SIZE = 20;
    public static final int MAX_TIMEOUT = 10;

    @Bean
    public WebClient webClient() {
        final Consumer<ClientCodecConfigurer> consumer = configurer -> {
            final ClientCodecConfigurer.ClientDefaultCodecs codecs = configurer.defaultCodecs();
            codecs.maxInMemorySize(MAX_MEM_SIZE * 1024 * 1024);
        };

        final HttpClient client = HttpClient.create()
                .responseTimeout(Duration.ofMinutes(MAX_TIMEOUT));

        return WebClient
                .builder()
                .codecs(consumer)
                .clientConnector(new ReactorClientHttpConnector(client))
                .build();
    }
}
