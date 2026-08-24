package com.AdilProject.constructerApp.config;

import org.springframework.context.annotation.Configuration;

/**
 * Application-level configuration.
 *
 * NOTE: ObjectMapper is NOT declared here as an explicit @Bean because
 * Spring Boot 4.x uses tools.jackson (Jackson 3.x), which is auto-configured
 * by spring-boot-starter-web. Controllers no longer need ObjectMapper injected;
 * they use Map<String,Object> with Spring MVC's built-in JSON serialization,
 * and the service layer handles JSON parsing internally via com.fasterxml (2.x)
 * which is still on the classpath via jjwt-jackson.
 */
@Configuration
public class AppConfig {
    // Intentionally empty — Spring Boot auto-configures Jackson via
    // spring-boot-starter-jackson (tools.jackson 3.x for SB 4.x)
}
