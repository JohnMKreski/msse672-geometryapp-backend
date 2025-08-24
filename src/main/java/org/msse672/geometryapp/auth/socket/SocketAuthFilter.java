package org.msse672.geometryapp.auth.socket;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.msse672.geometryapp.auth.config.AuthSocketProperties;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

/**
 * Gating filter: applies to /quad/** (configurable). Reads headers, calls TCP socket.
 * Success -> continue; bad/missing -> 401; socket errors -> 503 (configurable later if desired).
 */
@Component
public class SocketAuthFilter extends OncePerRequestFilter {
    private static final Logger log = LoggerFactory.getLogger(SocketAuthFilter.class);

    // Configuration properties for authentication
    private final AuthSocketProperties props;
    // Client used to validate tokens via TCP socket
    private final SocketAuthClient client;

    // Constructor injection with explicit bean qualifier for SocketAuthClient
    @Autowired
    public SocketAuthFilter(AuthSocketProperties props, @Qualifier("defaultSocketAuthClient") SocketAuthClient client) {
        this.props = props;
        this.client = client;
    }

    /**
     * Determines if the filter should be skipped for this request.
     * Skips if authentication is disabled, or if the path matches excluded patterns.
     */
    @Override
    protected boolean shouldNotFilter(HttpServletRequest request) {
        if (!props.isEnabled()) return true; // Auth disabled

        String path = request.getRequestURI();
        // Exclude certain paths from filtering
        if (path.startsWith("/auth/") || path.startsWith("/actuator/") || path.startsWith("/error")) return true;

        String prefix = props.getProtectedPathPrefix();
        // Only filter requests that match the protected path prefix
        return prefix == null || prefix.isBlank() || !path.startsWith(prefix);
    }

    /**
     * Main filter logic: checks for token header, validates it via TCP socket,
     * and sets appropriate HTTP status codes for errors.
     */
    @Override
    protected void doFilterInternal(HttpServletRequest req, HttpServletResponse res, FilterChain chain)
            throws ServletException, IOException {

        // Get the expected token header name from config
        String tokenHeader = props.getHeaderToken();
        String token = req.getHeader(tokenHeader);

        // Reject if token is missing or blank
        if (token == null || token.isBlank()) {
            res.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            res.setContentType("text/plain");
            res.getWriter().write("Missing token. Provide " + tokenHeader + " header.");
            return;
        }

        try {
            // Validate token using the TCP socket client
            boolean ok = client.sendTokenValidation(token);
            if (!ok) {
                // Token is invalid
                res.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
                res.setContentType("text/plain");
                res.getWriter().write("Invalid token.");
                return;
            }
        } catch (Exception e) {
            // Socket or validation error
            log.warn("Token validation error: {}", e.toString());
            res.setStatus(HttpServletResponse.SC_SERVICE_UNAVAILABLE);
            res.setContentType("text/plain");
            res.getWriter().write("Authentication service unavailable.");
            return;
        }

        // Token is valid, continue with the filter chain
        chain.doFilter(req, res);
    }

}