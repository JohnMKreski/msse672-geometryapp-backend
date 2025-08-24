package org.msse672.geometryapp.auth.controller;

import org.msse672.geometryapp.auth.socket.SocketAuthClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

/**
 * REST controller for authentication endpoints.
 * Uses token-based authentication validated via socket-auth logic.
 */
@RestController
@RequestMapping("/auth")
public class AuthController {

    private static final Logger logger = LoggerFactory.getLogger(AuthController.class);

    private final SocketAuthClient socketAuthClient;

    @Autowired
    public AuthController(SocketAuthClient socketAuthClient) {
        this.socketAuthClient = socketAuthClient;
    }

    /**
     * Authenticate user and return a token if credentials are valid.
     * Example: POST /auth/authenticate
     * Body: { "username": "admin", "password": "password123" }
     */
    @PostMapping("/authenticate")
    public ResponseEntity<String> authenticate(@RequestParam String username, @RequestParam String password) {
        String token;
        try {
            token = socketAuthClient.sendAuthRequest(username, password);
        } catch (Exception e) {
            logger.error("Socket error during authentication: {}", e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.SERVICE_UNAVAILABLE).body("Socket auth failed.");
        }
        if (token != null) {
            logger.info("Token generated for user '{}'", token);
            return ResponseEntity.ok(token);
        } else {
            logger.warn("Invalid credentials for user '{}'", username);
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Invalid credentials");
        }
    }

    /**
     * Validates if the provided token is valid.
     * Example: GET /auth/validate with header X-Auth-Token: <token>
     */
    @GetMapping("/validate")
    public ResponseEntity<String> validateToken(@RequestHeader("X-Auth-Token") String token) {
        boolean valid;
        try {
            valid = socketAuthClient.sendTokenValidation(token);
        } catch (Exception e) {
            logger.error("Socket error during token validation: {}", e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.SERVICE_UNAVAILABLE).body("Socket validation failed.");
        }

        if (valid) {
            return ResponseEntity.ok("Token is valid");
        } else {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Invalid token");
        }
    }


    /**
     * Logout by invalidating the token.
     * Example: POST /auth/logout with header X-Auth-Token: <token>
     */
    @PostMapping("/logout")
    public ResponseEntity<String> logout(
            @RequestHeader("X-Auth-Token") String token
    ) {
        try {
            boolean revoked = socketAuthClient.revokeToken(token);
            if (revoked) {
                logger.info("Token '{}' revoked via socket.", token);
                return ResponseEntity.ok("Logged out successfully");
            } else {
                logger.warn("Failed to revoke token '{}'", token);
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Invalid token");
            }
        } catch (Exception e) {
            logger.error("Socket logout failed", e);
            return ResponseEntity.status(HttpStatus.SERVICE_UNAVAILABLE).body("Logout service unavailable");
        }
    }
}
