package com.vi.openprop.config;

import com.amazonaws.auth.DefaultAWSCredentialsProviderChain;
import com.amazonaws.services.rds.auth.GetIamAuthTokenRequest;
import com.amazonaws.services.rds.auth.RdsIamAuthTokenGenerator;

/**
 * This class creates the access token for AWS RDS
 */
public class GenerateRDSAuthToken {
    private GenerateRDSAuthToken() {
    }

    public static String generateAuthToken(String jdbcUrl, String username, String region) {
        String hostname = getHostname(jdbcUrl);
        Integer port = getPort(jdbcUrl);

        RdsIamAuthTokenGenerator generator = RdsIamAuthTokenGenerator.builder()
                .credentials(new DefaultAWSCredentialsProviderChain())
                .region(region)
                .build();

        return generator.getAuthToken(
                GetIamAuthTokenRequest.builder()
                        .hostname(hostname)
                        .port(port)
                        .userName(username)
                        .build());
    }

    private static String getHostname(String url) {
        String hostname = url.substring(url.indexOf("://") + 3);
        hostname = hostname.substring(0, hostname.lastIndexOf(':'));
        return hostname;
    }

    private static Integer getPort(String url) {
        String port = url.substring(url.lastIndexOf(':') + 1);
        port = port.substring(0, port.lastIndexOf('/'));
        return Integer.parseInt(port);
    }
}
