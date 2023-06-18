package com.saied.binaryvault;

import org.flywaydb.core.Flyway;
import org.junit.jupiter.api.BeforeAll;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

@Testcontainers
public abstract class AbstractTestContainers {

    @Container
    public static final PostgreSQLContainer<?> psqlContainer =
        new PostgreSQLContainer<>("postgres:latest")
            .withDatabaseName("binaryvault")
            .withUsername("pickbas")
            .withPassword("password");

    @BeforeAll
    static void beforeAll() {
        Flyway flyway = Flyway
            .configure()
            .dataSource(
                psqlContainer.getJdbcUrl(),
                psqlContainer.getUsername(),
                psqlContainer.getPassword()
            ).load();
        flyway.migrate();
    }

    @DynamicPropertySource
    private static void registerDataSourceProperties(
        DynamicPropertyRegistry registry) {
        registry.add(
            "spring.datasource.url",
            psqlContainer::getJdbcUrl
        );
        registry.add(
            "spring.datasource.username",
            psqlContainer::getUsername
        );
        registry.add(
            "spring.datasource.password",
            psqlContainer::getPassword
        );
    }

}
