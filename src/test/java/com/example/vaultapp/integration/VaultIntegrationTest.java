package com.example.vaultapp.integration;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.containers.GenericContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import org.testcontainers.utility.DockerImageName;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@Testcontainers
public class VaultIntegrationTest {

    @Container
    static GenericContainer<?> vault = new GenericContainer<>(DockerImageName.parse("vault:1.15.2"))
            .withExposedPorts(8200)
            .withEnv("VAULT_DEV_ROOT_TOKEN_ID", "myroot")
            .withEnv("VAULT_DEV_LISTEN_ADDRESS", "0.0.0.0:8200")
            .withEnv("VAULT_ADDR", "http://0.0.0.0:8200");

    @DynamicPropertySource
    static void properties(DynamicPropertyRegistry registry) {
        registry.add("spring.cloud.vault.uri", () -> "http://localhost:" + vault.getMappedPort(8200));
        registry.add("spring.cloud.vault.token", () -> "myroot");
        registry.add("spring.cloud.vault.authentication", () -> "TOKEN");
    }

    @Test
    void contextLoads() {
        // Test that the application context loads with Vault running
    }
}