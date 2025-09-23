package com.example.vaultapp.integration;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.containers.GenericContainer;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.containers.wait.strategy.Wait;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import org.testcontainers.utility.DockerImageName;

import java.time.Duration;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@Testcontainers
public class VaultIntegrationTest {

    @Container
    static GenericContainer<?> vault = new GenericContainer<>(DockerImageName.parse("hashicorp/vault:1.15.2"))
            .withExposedPorts(8200)
            .withEnv("VAULT_DEV_ROOT_TOKEN_ID", "myroot")
            .withEnv("VAULT_DEV_LISTEN_ADDRESS", "0.0.0.0:8200")
            .withEnv("VAULT_ADDR", "http://0.0.0.0:8200")
            .withEnv("VAULT_LOCAL_CONFIG", "{\"backend\": {\"file\": {\"path\": \"/vault/file\"}}, \"default_lease_ttl\": \"168h\", \"max_lease_ttl\": \"720h\"}")
            .waitingFor(Wait.forHttp("/v1/sys/health").forPort(8200).withStartupTimeout(Duration.ofMinutes(2)));

    @Container
    static PostgreSQLContainer<?> postgres = new PostgreSQLContainer<>("postgres:15-alpine")
            .withDatabaseName("testdb")
            .withUsername("test")
            .withPassword("test")
            .withStartupTimeout(Duration.ofMinutes(2));

    @DynamicPropertySource
    static void properties(DynamicPropertyRegistry registry) {
        registry.add("spring.cloud.vault.uri", () -> "http://localhost:" + vault.getMappedPort(8200));
        registry.add("spring.cloud.vault.token", () -> "myroot");
        registry.add("spring.cloud.vault.authentication", () -> "TOKEN");
        // Override database properties for test
        registry.add("database.password", postgres::getPassword);
        registry.add("spring.datasource.url", postgres::getJdbcUrl);
        registry.add("spring.datasource.username", postgres::getUsername);
    }

    @Test
    void contextLoads() {
        // Test that the application context loads with Vault running
    }
}