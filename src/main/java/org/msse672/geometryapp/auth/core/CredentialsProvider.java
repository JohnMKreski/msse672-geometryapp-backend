package org.msse672.geometryapp.auth.core;

import org.springframework.stereotype.Component;

/**
 * Small abstraction for validating credentials.
 * Lets us swap how creds are stored (properties today, file/DB later) without touching auth logic.
 */
@Component
public interface CredentialsProvider {
    boolean isValid(String username, String password);
}
