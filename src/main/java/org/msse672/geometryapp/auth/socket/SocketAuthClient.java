package org.msse672.geometryapp.auth.socket;

import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;

/** Abstraction so the filter can authenticate via raw TCP. */
@Component
public interface SocketAuthClient {
    String sendAuthRequest(String username, String password) throws Exception;
    boolean sendTokenValidation(String token) throws Exception;
    boolean revokeToken(String token) throws Exception;
}
