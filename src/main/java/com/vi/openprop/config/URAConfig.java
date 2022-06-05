package com.vi.openprop.config;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestTemplate;

import java.time.Duration;

@Configuration
@ConfigurationProperties(prefix = "ura")
@Getter
@Setter
@NoArgsConstructor
public class  URAConfig {
    private String accessKey;
    private String propTransactionUrl;
    private String tokenUrl;
    private String uraResponseSavePath;
    private int requestRetryTimes;
    private int requestRetryInterval;
    private int retryIntervalMultiplier;
    private int batchStart;
    private int batchEnd;

    final static int TIMEOUT = 20;
    private boolean persistUraResponse;

    @Bean
    public RestTemplate restTemplate() {

        return new RestTemplateBuilder()
                .setConnectTimeout(Duration.ofMinutes(TIMEOUT))
                .setReadTimeout(Duration.ofMinutes(TIMEOUT))
                .build();
    }
}
