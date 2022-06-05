package com.vi.openprop.service.fetcher;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.vi.openprop.dto.URATokenResponseDto;
import okhttp3.mockwebserver.MockResponse;
import okhttp3.mockwebserver.MockWebServer;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.platform.commons.util.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.io.IOException;
import java.net.URI;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
class ExternalDataFetcherMockServerTest {
    public static MockWebServer mockBackEnd;

    @Autowired
    ExternalDataFetcher externalDataFetcher;

    @BeforeAll
    static void setUp() throws IOException {
        mockBackEnd = new MockWebServer();
        mockBackEnd.start();
    }

    @Test
    void getToken() throws Exception {
        String tokenUrl = String.format("http://localhost:%s", mockBackEnd.getPort());
        URATokenResponseDto mockURATokenResponseDto = new URATokenResponseDto();
        mockURATokenResponseDto.setResult("dummyToken");
        mockURATokenResponseDto.setStatus("Success");

        mockBackEnd.enqueue(new MockResponse()
                .setBody(new ObjectMapper().writeValueAsString(mockURATokenResponseDto))
                .addHeader("Content-Type", "application/json"));

        String token = externalDataFetcher.getToken(new URI(tokenUrl));
        assertFalse(StringUtils.isBlank(token));
    }

    @Test
    void test_token_cache() throws Exception {
        String tokenUrl = String.format("http://localhost:%s", mockBackEnd.getPort());
        URATokenResponseDto mockURATokenResponseDto = new URATokenResponseDto();
        mockURATokenResponseDto.setResult("dummyToken");
        mockURATokenResponseDto.setStatus("Success");

        mockBackEnd.enqueue(new MockResponse()
                .setBody(new ObjectMapper().writeValueAsString(mockURATokenResponseDto))
                .addHeader("Content-Type", "application/json"));

        URI tokenUri = new URI(tokenUrl);
        String token = externalDataFetcher.getToken(tokenUri);
        assertEquals("dummyToken", token);


        mockURATokenResponseDto = new URATokenResponseDto();
        mockURATokenResponseDto.setResult("dummyToken-2");
        mockURATokenResponseDto.setStatus("Success");
        mockBackEnd.enqueue(new MockResponse()
                .setBody(new ObjectMapper().writeValueAsString(mockURATokenResponseDto))
                .addHeader("Content-Type", "application/json"));

        String token2 = externalDataFetcher.getToken(tokenUri);
        assertEquals("dummyToken", token2);
    }

    @AfterAll
    static void tearDown() throws IOException {
        mockBackEnd.shutdown();
    }
}
