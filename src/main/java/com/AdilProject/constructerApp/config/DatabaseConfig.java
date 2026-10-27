package com.AdilProject.constructerApp.config;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.core.env.Environment;

import javax.sql.DataSource;

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

        String s = rawUrl.trim();
        if (s.startsWith("jdbc:")) {
            s = s.substring(5);
        }

        if (s.startsWith("postgres://") || s.startsWith("postgresql://")) {
            if (s.startsWith("postgres://")) {
                s = s.substring("postgres://".length());
            } else {
                s = s.substring("postgresql://".length());
            }

            String query = null;
            int queryIdx = s.indexOf('?');
            if (queryIdx != -1) {
                query = s.substring(queryIdx + 1);
                s = s.substring(0, queryIdx);
            }

            int slashIdx = s.indexOf('/');
            String path = "/adil_construction";
            String authority = s;
            if (slashIdx != -1) {
                path = s.substring(slashIdx);
                authority = s.substring(0, slashIdx);
            }

            int atIdx = authority.lastIndexOf('@');
            if (atIdx != -1) {
                String userInfo = authority.substring(0, atIdx);
                authority = authority.substring(atIdx + 1);
                int colonIdx = userInfo.indexOf(':');
                if (colonIdx != -1) {
                    username = userInfo.substring(0, colonIdx);
                    password = userInfo.substring(colonIdx + 1);
                } else {
                    username = userInfo;
                }
            }

            int colonIdx = authority.lastIndexOf(':');
            String host = authority;
            int port = 5432;
            if (colonIdx != -1) {
                host = authority.substring(0, colonIdx);
                try {
                    port = Integer.parseInt(authority.substring(colonIdx + 1));
                } catch (Exception ignored) {
                }
            }

            if (host != null && host.contains("render.com")) {
                if (query == null || !query.contains("sslmode=")) {
                    query = (query == null || query.isBlank()) ? "sslmode=require" : query + "&sslmode=require";
                }
            }

            rawUrl = "jdbc:postgresql://" + host + ":" + port + path + (query != null && !query.isBlank() ? "?" + query : "");
        } else if (!rawUrl.startsWith("jdbc:")) {
            rawUrl = "jdbc:" + rawUrl;
        }

        log.info("Configured Database target URL: {} with user: {}", rawUrl, username);

        HikariConfig config = new HikariConfig();
        config.setJdbcUrl(rawUrl);
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
