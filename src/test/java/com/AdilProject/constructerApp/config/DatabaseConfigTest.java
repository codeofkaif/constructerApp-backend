package com.AdilProject.constructerApp.config;

import com.zaxxer.hikari.HikariConfig;
import org.junit.jupiter.api.Test;
import org.springframework.mock.env.MockEnvironment;

import static org.junit.jupiter.api.Assertions.*;

class DatabaseConfigTest {

    private final DatabaseConfig databaseConfig = new DatabaseConfig();

    @Test
    void testStandardPostgreSqlUriWithCredentials() {
        MockEnvironment env = new MockEnvironment();
        env.setProperty("DATABASE_URL", "postgres://admin_usr:super_pass@db.example.com:5432/my_database");

        HikariConfig config = databaseConfig.buildHikariConfig(env);
        assertEquals("jdbc:postgresql://db.example.com:5432/my_database", config.getJdbcUrl());
        assertEquals("admin_usr", config.getUsername());
        assertEquals("super_pass", config.getPassword());
    }

    @Test
    void testComplexPasswordWithSpecialCharacters() {
        MockEnvironment env = new MockEnvironment();
        env.setProperty("DATABASE_URL", "postgresql://adil_user:p@ss#word%123@dpg-abc123-a.oregon-postgres.render.com:5432/adil_db");

        HikariConfig config = databaseConfig.buildHikariConfig(env);
        assertEquals("jdbc:postgresql://dpg-abc123-a.oregon-postgres.render.com:5432/adil_db?sslmode=require", config.getJdbcUrl());
        assertEquals("adil_user", config.getUsername());
        assertEquals("p@ss#word%123", config.getPassword());
    }

    @Test
    void testRawCredentialsWithSymbols() {
        MockEnvironment env = new MockEnvironment();
        env.setProperty("DATABASE_URL", "postgres://user_one:my@secret#pass@dpg-xyz.singapore-postgres.render.com/prod_db");

        HikariConfig config = databaseConfig.buildHikariConfig(env);
        assertEquals("jdbc:postgresql://dpg-xyz.singapore-postgres.render.com:5432/prod_db?sslmode=require", config.getJdbcUrl());
        assertEquals("user_one", config.getUsername());
        assertEquals("my@secret#pass", config.getPassword());
    }

    @Test
    void testJdbcPrefixWithEmbeddedCredentials() {
        MockEnvironment env = new MockEnvironment();
        env.setProperty("DATABASE_URL", "jdbc:postgresql://postgres_user:sec_pass@localhost:5432/adil_construction");

        HikariConfig config = databaseConfig.buildHikariConfig(env);
        assertEquals("jdbc:postgresql://localhost:5432/adil_construction", config.getJdbcUrl());
        assertEquals("postgres_user", config.getUsername());
        assertEquals("sec_pass", config.getPassword());
    }

    @Test
    void testH2TestingUrl() {
        MockEnvironment env = new MockEnvironment();
        env.setProperty("spring.datasource.url", "jdbc:h2:mem:testdb;MODE=PostgreSQL");
        env.setProperty("spring.datasource.driver-class-name", "org.h2.Driver");
        env.setProperty("spring.datasource.username", "sa");
        env.setProperty("spring.datasource.password", "");

        HikariConfig config = databaseConfig.buildHikariConfig(env);
        assertEquals("jdbc:h2:mem:testdb;MODE=PostgreSQL", config.getJdbcUrl());
        assertEquals("sa", config.getUsername());
    }
}
