package com.AdilProject.constructerApp.config;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.core.env.Environment;

import javax.sql.DataSource;
import java.net.URI;

@Slf4j
@Configuration
public class DatabaseConfig {

    public HikariConfig buildHikariConfig(Environment env) {
        String rawUrl = System.getenv("DATABASE_URL");
        if (rawUrl == null || rawUrl.isBlank()) {
            rawUrl = env.getProperty("DATABASE_URL");
        }
        if (rawUrl == null || rawUrl.isBlank()) {
            rawUrl = System.getenv("SPRING_DATASOURCE_URL");
        }
        if (rawUrl == null || rawUrl.isBlank()) {
            rawUrl = env.getProperty("SPRING_DATASOURCE_URL");
        }
        if (rawUrl == null || rawUrl.isBlank()) {
            rawUrl = env.getProperty("spring.datasource.url", "jdbc:postgresql://localhost:5432/adil_construction");
        }

        String username = System.getenv("SPRING_DATASOURCE_USERNAME");
        if (username == null || username.isBlank()) {
            username = env.getProperty("SPRING_DATASOURCE_USERNAME");
        }
        if (username == null || username.isBlank()) {
            username = env.getProperty("spring.datasource.username", "kaifkhan");
        }

        String password = System.getenv("SPRING_DATASOURCE_PASSWORD");
        if (password == null) {
            password = env.getProperty("SPRING_DATASOURCE_PASSWORD");
        }
        if (password == null) {
            password = env.getProperty("spring.datasource.password", "");
        }

        String driverClassName = env.getProperty("spring.datasource.driver-class-name", "org.postgresql.Driver");

        if (rawUrl.startsWith("jdbc:h2:")) {
            HikariConfig h2Config = new HikariConfig();
            h2Config.setJdbcUrl(rawUrl);
            h2Config.setUsername(username);
            h2Config.setPassword(password);
            h2Config.setDriverClassName(driverClassName);
            return h2Config;
        }

        String jdbcUrl = rawUrl.trim();
        String clean = jdbcUrl.startsWith("jdbc:") ? jdbcUrl.substring(5) : jdbcUrl;

        if (clean.startsWith("postgres://") || clean.startsWith("postgresql://")) {
            String httpEquivalent = clean.startsWith("postgresql://")
                    ? "http://" + clean.substring(13)
                    : "http://" + clean.substring(11);
            try {
                URI uri = URI.create(httpEquivalent);
                String host = uri.getHost();
                int port = uri.getPort() != -1 ? uri.getPort() : 5432;
                String path = uri.getPath();
                if (path == null || path.isBlank() || path.equals("/")) {
                    path = "/adil_construction";
                }
                String query = uri.getQuery();
                String userInfo = uri.getUserInfo();

                if (userInfo != null && !userInfo.isBlank()) {
                    String[] parts = userInfo.split(":", 2);
                    username = parts[0];
                    if (parts.length > 1) {
                        password = parts[1];
                    }
                }

                if (host != null && host.endsWith("render.com")) {
                    if (query == null || !query.contains("sslmode=")) {
                        query = (query == null || query.isBlank()) ? "sslmode=require" : query + "&sslmode=require";
                    }
                }

                jdbcUrl = "jdbc:postgresql://" + host + ":" + port + path + (query != null && !query.isBlank() ? "?" + query : "");
            } catch (Exception e) {
                log.warn("Failed to parse database URI, using raw URL with jdbc: prefix: {}", e.getMessage());
                if (!jdbcUrl.startsWith("jdbc:")) {
                    jdbcUrl = "jdbc:" + jdbcUrl;
                }
            }
        } else if (!jdbcUrl.startsWith("jdbc:")) {
            jdbcUrl = "jdbc:" + jdbcUrl;
        }

        HikariConfig config = new HikariConfig();
        config.setJdbcUrl(jdbcUrl);
        config.setUsername(username);
        config.setPassword(password);
        config.setDriverClassName(driverClassName);
        config.setMaximumPoolSize(10);
        config.setMinimumIdle(2);
        config.setConnectionTimeout(30000);
        config.setIdleTimeout(600000);
        config.setMaxLifetime(1800000);
        config.setInitializationFailTimeout(-1);

        return config;
    }

    @Bean
    @Primary
    public DataSource dataSource(Environment env) {
        HikariConfig config = buildHikariConfig(env);
        return new HikariDataSource(config);
    }
}
