package com.eventlinkr.userservice;

import io.r2dbc.spi.ConnectionFactory;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.core.io.ClassPathResource;
import org.springframework.data.r2dbc.repository.config.EnableR2dbcRepositories;
import org.springframework.r2dbc.connection.init.ConnectionFactoryInitializer;
import org.springframework.r2dbc.connection.init.ResourceDatabasePopulator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Entry point for the User Service Application.
 * Includes R2DBC configuration and database initialization.
 */
@SpringBootApplication
@EnableR2dbcRepositories
public class UserServiceApplication {
    private static final Logger logger = LoggerFactory.getLogger(UserServiceApplication.class);

    public static void main(String[] args) {
        logger.info("Starting User Service Application...");
        SpringApplication.run(UserServiceApplication.class, args);
        logger.info("User Service Application started successfully");
    }

    /**
     * Initializes the database with schema if needed.
     */
    @Bean
    ConnectionFactoryInitializer initializer(ConnectionFactory connectionFactory) {
        ConnectionFactoryInitializer initializer = new ConnectionFactoryInitializer();
        initializer.setConnectionFactory(connectionFactory);

        ResourceDatabasePopulator populator = new ResourceDatabasePopulator(
                new ClassPathResource("db/migration/V1__create_users_table_postgres.sql")
        );
        initializer.setDatabasePopulator(populator);

        logger.info("Database initialization configured");
        return initializer;
    }
}