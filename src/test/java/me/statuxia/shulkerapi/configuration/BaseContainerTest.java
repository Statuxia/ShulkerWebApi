package me.statuxia.shulkerapi.configuration;

import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

@Testcontainers(disabledWithoutDocker = true)
public class BaseContainerTest {

    @Container
    private static final PostgreSQLContainer<?> CONTAINER = new PostgreSQLContainer<>("postgres:17.5");

    @DynamicPropertySource
    static void configureDatabase(DynamicPropertyRegistry registry) {
        registry.add("spring.datasource.url", () -> CONTAINER.getJdbcUrl());
        registry.add("spring.datasource.username", () -> CONTAINER.getUsername());
        registry.add("spring.datasource.password", () -> CONTAINER.getPassword());
    }
}
