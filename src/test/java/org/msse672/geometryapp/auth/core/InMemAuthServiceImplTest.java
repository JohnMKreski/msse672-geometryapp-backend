package org.msse672.geometryapp.auth.core;

import org.junit.jupiter.api.Test;
import org.msse672.geometryapp.TriangleMiddlewareApplication;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.beans.factory.annotation.Autowired;


import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest(classes = TriangleMiddlewareApplication.class)
@ActiveProfiles("test")
class InMemAuthServiceImplTest {

    @Autowired
    private InMemAuthServiceImpl authService;


    @Test
    void testAuthenticateSuccessReturnsToken() {
        String token = authService.authenticate("admin", "password123");
        assertNotNull(token, "Expected a non-null token on successful authentication.");
        assertTrue(authService.isTokenValid(token), "Token should be valid after successful authentication.");
    }

    @Test
    void testAuthenticateFailureReturnsNull() {
        String token = authService.authenticate("admin", "wrongpassword");
        assertNull(token, "Expected null token on failed authentication.");
    }

    @Test
    void testIsTokenValidWithInvalidToken() {
        assertFalse(authService.isTokenValid("nonexistent-token"), "Invalid token should return false.");
    }


    @Test
    void testLogoutInvalidatesToken() {
        String token = authService.authenticate("admin", "password123");
        assertNotNull(token);
        authService.logout(token);
        assertFalse(authService.isTokenValid(token));
    }


}
