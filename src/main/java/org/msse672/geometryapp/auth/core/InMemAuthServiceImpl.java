package org.msse672.geometryapp.auth.core;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

/**
 * In-memory implementation of AuthService using a CredentialsProvider to validate login attempts.
 */
@Service
public class InMemAuthServiceImpl implements AuthService {

    private static final Logger logger = LoggerFactory.getLogger(InMemAuthServiceImpl.class);

    private final CredentialsProvider credentialsProvider;

    // Thread-safe map to store user tokens (key: username, value: token)
    private final Map<String, String> userTokens = new ConcurrentHashMap<>();

    @Autowired
    public InMemAuthServiceImpl(CredentialsProvider credentialsProvider) {
        this.credentialsProvider = credentialsProvider;
    }

    /**
     * Generates a unique token for a given username.
     * In production, will use a secure method like JWT or encrypted tokens.
     */
    private String generateToken(String username) {
        return UUID.randomUUID().toString() + "-" + username;
    }

    @Override
    public String authenticate(String username, String password) {
        logger.info("Attempting authentication for username: {}", username);

        if (credentialsProvider.isValid(username, password)) {
            String token = generateToken(username);
            userTokens.put(username, token);
            logger.info("Authentication successful for user: {}", username);
            return token;
        }

        logger.info("Authentication failed for user: {}", username);
        return null;
    }

//    private boolean isValidCredentials(String username, String password) {
//        // Validate credentials (e.g., check against a database or in-memory store)
//        return "admin".equals(username) && "password".equals(password);
//    }

    private void storeToken(String username, String token) {
        // Store the token in a database or in-memory map
    }

    @Override
    public boolean isTokenValid(String token) {
        return userTokens.containsValue(token);
    }

    @Override
    public void logout(String token) {
        String userToRemove = userTokens.entrySet().stream()
                .filter(entry -> entry.getValue().equals(token))
                .map(Map.Entry::getKey)
                .findFirst()
                .orElse(null);

        if (userToRemove != null) {
            userTokens.remove(userToRemove);
            logger.info("User '{}' logged out", userToRemove);
        } else {
            logger.warn("Logout attempted for unknown token");
        }
    }

    /**
     * Optional utility method (not required by interface).
     */
    public String getUsernameForToken(String token) {
        return userTokens.entrySet().stream()
                .filter(entry -> entry.getValue().equals(token))
                .map(Map.Entry::getKey)
                .findFirst()
                .orElse(null);
    }
}
