package com.vi.openprop.service.fetcher;

import com.vi.openprop.config.URAConfig;
import com.vi.openprop.dto.URATokenResponseDto;
import com.vi.openprop.helpers.URAResult;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;

import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.Objects;
import java.util.concurrent.TimeUnit;

@Component
public class ExternalDataFetcher {
    private Logger logger = LoggerFactory.getLogger(ExternalDataFetcher.class);

    public static final String ACCESS_KEY = "AccessKey";
    public static final String TOKEN = "Token";
    public static final String SUCCESS = "Success";

    private URAConfig uraConfig;
    private WebClient webClient;

    @Autowired
    public ExternalDataFetcher(URAConfig uraConfig, WebClient webClient) {
        this.uraConfig = uraConfig;
        this.webClient = webClient;
    }

    public String loadFromUra(final Integer batchNum) {
        try {
            final String token = getToken(new URI(uraConfig.getTokenUrl()));
            final String finalUrl = String.format(uraConfig.getPropTransactionUrl(), batchNum);
            logger.info("Calling ura with url {} ", finalUrl);
            return getUraData(token, finalUrl);
        } catch (InterruptedException e) {
            logger.error("InterruptedException ", e);
        } catch (URISyntaxException e) {
            logger.error("Unable to build token uri");
        }
        return null;
    }

    /**
     *
     * @param token
     * @param uraUrl
     * @return null or result
     * @throws InterruptedException
     */
    String getUraData(String token, String uraUrl) throws InterruptedException {
        URAResult initUraResult = new URAResult();
        initUraResult.setTryLeft(uraConfig.getRequestRetryTimes());
        URAResult uraResult = getUraDataWithRetry(token, uraUrl, initUraResult);
        while (!uraResult.isSuccess() && uraResult.getTryLeft() > 0) {
            TimeUnit.SECONDS.sleep(uraConfig.getRequestRetryInterval() * uraConfig.getRetryIntervalMultiplier());
            uraResult = getUraDataWithRetry(token, uraUrl, uraResult);
        }
        if (!uraResult.isSuccess()) {
            logger.info("Maximum retry limit {} exceed.", uraConfig.getRequestRetryTimes());
            return null;
        }
        return uraResult.getResult();
    }

    String getUraResponseWebClient(URI uri, final String token) {
        return webClient
                .get()
                .uri(uriBuilder -> uri)
                .headers(headers -> {
                    headers.set(TOKEN, token);
                    headers.set(ACCESS_KEY, uraConfig.getAccessKey());
                })
                .retrieve()
                .bodyToMono(String.class)
                .block();
    }

    URAResult getUraDataWithRetry(String token, String uraUrl, URAResult prevResult) {
        if (prevResult.isSuccess()) return prevResult;
        String error = "";
        URAResult uraResult = new URAResult();
        uraResult.setSuccess(false);
        uraResult.setTryLeft(prevResult.getTryLeft() - 1);
        logger.info("Start to get url data with uraUrl {} and tries left {}", uraUrl, uraResult.getTryLeft());
        String uraResponse;
        try {
            uraResponse = getUraResponseWebClient(new URL(uraUrl).toURI(), token);

            if (!uraResponse.contains(SUCCESS)) {
                error = String.format("Error response from ura %s at try count left %s", uraResponse, uraResult.getTryLeft());
                logger.error(error);
                return uraResult;
            }
            uraResult.setResult(uraResponse);
            uraResult.setSuccess(true);
            return uraResult;
        } catch (Exception e) {
            logger.error("Exception happen during rest call to ura",e);
            return uraResult;
        }
    }

    @Cacheable("tokenCache")
    public String getToken(URI tokenUri) {
        logger.info("Initialize token for ura service.");
        URATokenResponseDto uraTokenResponse = webClient
                .get()
                .uri(uriBuilder -> tokenUri)
                .headers(headers -> {
                    headers.set(ACCESS_KEY, uraConfig.getAccessKey());
                })
                .retrieve()
                .bodyToMono(URATokenResponseDto.class)
                .block();
        if (SUCCESS.equals(Objects.requireNonNull(uraTokenResponse).getStatus())) {
            String token = uraTokenResponse.getResult();
            logger.info("Token returned {}", token);
            return token;
        }
        return null;
    }
}
