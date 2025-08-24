package org.msse672.geometryapp.auth.config;

import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * Binds all socket-auth configuration under prefix "auth.socket" from application*.properties.
 * Use this bean anywhere you need to read host/port/timeouts/headers, etc.
 */
@ConfigurationProperties(prefix = "auth.socket")
public class AuthSocketProperties {

    private String headerToken = "X-Auth-Token";

    private boolean enabled = true;          // Master on/off switch (useful for tests/demos)
    private String host = "localhost";       // Where the socket server listens
    private int port = 9090;                 // Listening port (override in test profile if needed)
    private int timeoutMs = 2000;            // Connect/read timeout for clients

    private String headerUsername = "X-Username"; // HTTP header name (used later by the filter)
    private String headerPassword = "X-Password"; // HTTP header name (used later by the filter)
    private String protectedPathPrefix = "/quad"; // Which HTTP path to protect (Week 7)

    // Getters/setters required for @ConfigurationProperties binding
    public boolean isEnabled() { return enabled; }
    public void setEnabled(boolean enabled) { this.enabled = enabled; }
    public String getHost() { return host; }
    public void setHost(String host) { this.host = host; }
    public int getPort() { return port; }
    public void setPort(int port) { this.port = port; }
    public int getTimeoutMs() { return timeoutMs; }
    public void setTimeoutMs(int timeoutMs) { this.timeoutMs = timeoutMs; }
    public String getHeaderUsername() { return headerUsername; }
    public void setHeaderUsername(String headerUsername) { this.headerUsername = headerUsername; }
    public String getHeaderPassword() { return headerPassword; }
    public void setHeaderPassword(String headerPassword) { this.headerPassword = headerPassword; }
    public String getProtectedPathPrefix() { return protectedPathPrefix; }
    public void setProtectedPathPrefix(String protectedPathPrefix) { this.protectedPathPrefix = protectedPathPrefix; }
    public String getHeaderToken() {
        return headerToken;
    }
    public void setHeaderToken(String headerToken) {
        this.headerToken = headerToken;
    }
}
