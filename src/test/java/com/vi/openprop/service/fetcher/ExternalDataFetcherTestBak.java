/*
package com.vi.openprop.service.fetcher;

import com.vi.openprop.config.URAConfig;
import com.vi.openprop.service.persistor.DataPersistor;
import org.junit.Test;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.reactive.function.client.WebClient;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

import static org.junit.Assert.assertTrue;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.BDDMockito.given;

@ExtendWith(MockitoExtension.class)
class ExternalDataFetcherTestBak {
    @Mock
    URAConfig uraConfig;

    @Mock
    WebClient webClient;

    @Mock
    DataPersistor dataPersistor;

    final String sampleResponse = "src/test/resources/sampleResponse.json";
    String response = "";

    @BeforeEach
    public void initTest() throws IOException {
        MockitoAnnotations.openMocks(this);
        response = Files.readString(Path.of(sampleResponse));
    }

    @Test
    public void get_ura_with_retry_SUCCESS() throws InterruptedException {
        RestTemplate restTemplate = mock(RestTemplate.class);
//        given(webClient.get().retrieve().bodyToMono(String.class).block()).willReturn("dummy");
        ExternalDataFetcher externalDataFetcher = spy(new ExternalDataFetcher(restTemplate, uraConfig, webClient,dataPersistor));
        ResponseEntity<String> responseEntity = new ResponseEntity<>(response, HttpStatus.OK);
        given(restTemplate.exchange(anyString(), eq(HttpMethod.GET), any(), eq(String.class))).willReturn(responseEntity);
        String uraResult = externalDataFetcher.getUraData("", "");
        verify(externalDataFetcher, times(1)).getUraDataWithRetry(anyString(), anyString(), any());
        assertTrue(uraResult.contains("Success"));
    }

    @Test
    public void get_ura_with_retry_BADGATEWAY() throws InterruptedException {
        RestTemplate restTemplate = mock(RestTemplate.class);
        given(uraConfig.getRetryIntervalMultiplier()).willReturn(1);
        given(uraConfig.getRequestRetryTimes()).willReturn(5);
        given(uraConfig.getRequestRetryInterval()).willReturn(1);
        ExternalDataFetcher externalDataFetcher = spy(new ExternalDataFetcher(restTemplate, uraConfig, webClient,dataPersistor));
        ResponseEntity<String> responseEntity = new ResponseEntity<>(response, HttpStatus.BAD_GATEWAY);
        given(restTemplate.exchange(anyString(), eq(HttpMethod.GET), any(), eq(String.class))).willReturn(responseEntity);
        externalDataFetcher.getUraData("", "");
        verify(externalDataFetcher, times(5)).getUraDataWithRetry(anyString(), anyString(), any());
    }

    @Test
    public void get_ura_with_retry_error() throws InterruptedException {
        RestTemplate restTemplate = mock(RestTemplate.class);
        given(uraConfig.getRetryIntervalMultiplier()).willReturn(1);
        given(uraConfig.getRequestRetryTimes()).willReturn(5);
        given(uraConfig.getRequestRetryInterval()).willReturn(1);
        ExternalDataFetcher externalDataFetcher = spy(new ExternalDataFetcher(restTemplate, uraConfig, webClient,dataPersistor));
        ResponseEntity<String> responseEntityError = new ResponseEntity<>("Error", HttpStatus.OK);
        given(restTemplate.exchange(anyString(), eq(HttpMethod.GET), any(), eq(String.class))).willReturn(responseEntityError);
        externalDataFetcher.getUraData("", "");
        verify(externalDataFetcher, times(5)).getUraDataWithRetry(anyString(), anyString(), any());
    }

    @Test
    public void get_ura_with_retry_exception() throws InterruptedException {
        RestTemplate restTemplate = mock(RestTemplate.class);
        given(uraConfig.getRetryIntervalMultiplier()).willReturn(1);
        given(uraConfig.getRequestRetryTimes()).willReturn(5);
        given(uraConfig.getRequestRetryInterval()).willReturn(1);
        ExternalDataFetcher externalDataFetcher = spy(new ExternalDataFetcher(restTemplate, uraConfig, webClient,dataPersistor));
        given(restTemplate.exchange(anyString(), eq(HttpMethod.GET), any(), eq(String.class))).willThrow(new RuntimeException());
        externalDataFetcher.getUraData("", "");
        verify(externalDataFetcher, times(5)).getUraDataWithRetry(anyString(), anyString(), any());
    }
}
*/
