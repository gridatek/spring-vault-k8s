package com.example.vaultapp;

import com.example.vaultapp.controller.HealthController;
import com.example.vaultapp.service.UserService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.ActiveProfiles;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@ActiveProfiles("test")
class VaultApplicationTests {

    @Autowired
    private HealthController healthController;

    @MockBean
    private UserService userService;

    @Test
    void contextLoads() {
        assertThat(healthController).isNotNull();
    }
}