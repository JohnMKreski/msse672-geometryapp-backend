package org.msse672.geometryapp.auth.core;

import org.springframework.stereotype.Service;

/**
 * AuthService defines token-based authentication behavior.
 * It no longer relies on HttpSession and instead supports token-based auth.
 */
@Service
public interface AuthService {

        /**
         * Authenticates the given username and password.
         *
         * @param username The user identifier.
         * @param password The raw password to validate.
         * @return A generated auth token if successful, or null if failed.
         */
        String authenticate(String username, String password);

        /**
         * Validates whether the provided token is currently active.
         *
         * @param token The token to validate.
         * @return true if valid, false otherwise.
         */
        boolean isTokenValid(String token);

        /**
         * Invalidates the token, logging out the associated user.
         *
         * @param token The token to revoke.
         */
        void logout(String token);
}
