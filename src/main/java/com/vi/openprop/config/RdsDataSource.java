package com.vi.openprop.config;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import javax.sql.DataSource;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.function.Supplier;

/**
 * The class provide connectivity the RDS database with IAM token refresh every 10 mins.
 * Refreshing token is not mandatory, but it is highly recommended by AWS for external DB connections.
 * A worker thread (user thread) refresh the token every 10 mins
 *
 * Reference https://docs.aws.amazon.com/AmazonRDS/latest/UserGuide/UsingWithRDS.IAMDBAuth.DBAccounts.html
 */
@Configuration
public class RdsDataSource {
    private static final long TEN_MINUTES = 10;
    private static final Logger logger = LoggerFactory.getLogger(RdsDataSource.class);

    @Value("${spring.datasource.url}")
    String jdbcUrl;
    @Value("${spring.datasource.username}")
    String username;
    @Value("${cloud.aws.region}")
    String regionName;
    @Value("${local.database.password}")
    String localDatabasePassword;

    @Bean
    public DataSource dataSource() {
        return createDataSource(jdbcUrl, username, regionName);
    }

    private HikariDataSource createDataSource(String jdbcUrl, String username, String regionName) {
        logger.info("Creating custom HikariDataSource.....");
        HikariConfig config = new HikariConfig();
        config.setJdbcUrl(jdbcUrl);
        config.setUsername(username);
        config.setPassword(generateAuthToken(jdbcUrl, username, regionName));
        HikariDataSource hikariDataSource = new HikariDataSource(config);

        // starting a scheduled thread to refresh IAM password
        Executors.newSingleThreadScheduledExecutor().scheduleAtFixedRate(
                new IamWorker(hikariDataSource, () -> generateAuthToken(jdbcUrl, username, regionName)),
                RdsDataSource.TEN_MINUTES,
                RdsDataSource.TEN_MINUTES,
                TimeUnit.MINUTES
        );

        return hikariDataSource;
    }

    private String generateAuthToken(String jdbcUrl, String username, String regionName) {
        String authToken = null;
        if (jdbcUrl.contains("localhost")) {
            authToken = localDatabasePassword;
        } else {
            authToken = GenerateRDSAuthToken.generateAuthToken(jdbcUrl, username, regionName);
        }
        System.out.println("authToken = " + authToken);
        return authToken;
    }

    private class IamWorker implements Runnable {
        private final Logger logger = LoggerFactory.getLogger(IamWorker.class);
        private HikariDataSource hikariDataSource;
        private Supplier<String> tokenGenerator;

        public IamWorker(HikariDataSource hikariDataSource, Supplier<String> tokenGenerator) {
            this.hikariDataSource = hikariDataSource;
            this.tokenGenerator = tokenGenerator;
        }

        @Override
        public void run() {
            String authToken = tokenGenerator.get();
            logger.info("Refreshing IAM credentials...");
            System.out.println("New Token :" + authToken);
            hikariDataSource.getHikariConfigMXBean().setPassword(authToken);
        }
    }

}
