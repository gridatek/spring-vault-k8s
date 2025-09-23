package com.example.vaultapp.e2e;

import com.example.vaultapp.entity.User;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.ActiveProfiles;

import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles("test")
public class EndToEndTest {

    @LocalServerPort
    private int port;

    @Autowired
    private TestRestTemplate restTemplate;

    @Test
    void testHealthEndpoint() {
        ResponseEntity<Map> response = restTemplate.getForEntity(
                "http://localhost:" + port + "/api/health", Map.class);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).containsKey("status");
        assertThat(response.getBody().get("status")).isEqualTo("UP");
    }

    @Test
    void testUserCrudOperations() {
        User user = new User("Test User", "test@example.com");

        ResponseEntity<User> createResponse = restTemplate.postForEntity(
                "http://localhost:" + port + "/api/users", user, User.class);

        assertThat(createResponse.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(createResponse.getBody().getName()).isEqualTo("Test User");
        assertThat(createResponse.getBody().getEmail()).isEqualTo("test@example.com");

        Long userId = createResponse.getBody().getId();

        ResponseEntity<User> getResponse = restTemplate.getForEntity(
                "http://localhost:" + port + "/api/users/" + userId, User.class);

        assertThat(getResponse.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(getResponse.getBody().getName()).isEqualTo("Test User");
    }
}