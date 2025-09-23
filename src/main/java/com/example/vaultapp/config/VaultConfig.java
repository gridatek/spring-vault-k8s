package com.example.vaultapp.config;

import org.springframework.cloud.vault.config.VaultProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.vault.authentication.ClientAuthentication;
import org.springframework.vault.authentication.KubernetesAuthentication;
import org.springframework.vault.authentication.KubernetesAuthenticationOptions;
import org.springframework.vault.client.VaultEndpoint;
import org.springframework.vault.core.VaultTemplate;
import org.springframework.vault.support.SslConfiguration;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;

@Configuration
public class VaultConfig {

    @Bean
    public VaultTemplate vaultTemplate() throws IOException {
        VaultEndpoint endpoint = VaultEndpoint.create("vault", 8200);
        endpoint.setScheme("https");

        // SSL Configuration with PEM certificates
        SslConfiguration sslConfiguration = SslConfiguration.forTrustStore(
                Paths.get("/etc/ssl/certs/vault-ca.pem"),
                "changeit".toCharArray()
        );

        // Kubernetes Authentication
        String jwt = new String(Files.readAllBytes(
                Paths.get("/var/run/secrets/kubernetes.io/serviceaccount/token")
        ));

        KubernetesAuthenticationOptions options = KubernetesAuthenticationOptions.builder()
                .role("vault-app-role")
                .jwt(jwt)
                .path("kubernetes")
                .build();

        ClientAuthentication clientAuthentication = new KubernetesAuthentication(
                options,
                VaultTemplate.create(endpoint, sslConfiguration).getVaultOperations()
        );

        return VaultTemplate.create(endpoint, sslConfiguration, clientAuthentication);
    }
}