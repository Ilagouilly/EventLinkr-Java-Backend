package com.eventlinkr.userservice.config;

import org.springframework.context.annotation.Configuration;
import org.testcontainers.junit.jupiter.Testcontainers;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.containers.PostgreSQLContainer;

@Testcontainers
@Configuration
public class TestContainersConfiguration {
    @Container
    public static PostgreSQLContainer<?> postgreSQLContainer = new PostgreSQLContainer<>("postgres:15")
            .withDatabaseName("testdb").withUsername("test").withPassword("test");

}