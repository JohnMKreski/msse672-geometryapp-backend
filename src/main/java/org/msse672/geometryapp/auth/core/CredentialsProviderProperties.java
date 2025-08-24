// Provides demo credentials from application properties for authentication
package org.msse672.geometryapp.auth.core;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

/**
 * Reads demo credentials from application*.properties:
 *   auth.demo.username=
 *   auth.demo.password=
 * Used for simple authentication in demo environments.
 */
@Component
public class CredentialsProviderProperties implements CredentialsProvider {

    // Expected username from properties (default: "admin")
    private final String expectedUser;
    // Expected password from properties (default: "password123")
    private final String expectedPass;

    /**
     * Constructor injects expected credentials from properties.
     * Defaults are provided if properties are missing.
     */
    @Autowired
    public CredentialsProviderProperties(
            @Value("${auth.demo.username:admin}") String expectedUser,
            @Value("${auth.demo.password:password123}") String expectedPass) {
        this.expectedUser = expectedUser;
        this.expectedPass = expectedPass;
    }

    /**
     * Validates provided username and password against expected values.
     * Returns true if both match, false otherwise.
     */
    @Override
    public boolean isValid(String username, String password) {
        if (username == null || password == null) return false;
        return expectedUser.equals(username) && expectedPass.equals(password);
    }
}