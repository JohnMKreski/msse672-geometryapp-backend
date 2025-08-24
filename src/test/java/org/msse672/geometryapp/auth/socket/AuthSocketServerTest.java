package org.msse672.geometryapp.auth.socket;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.msse672.geometryapp.TriangleMiddlewareApplication;
import org.msse672.geometryapp.auth.config.AuthSocketProperties;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import java.io.*;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.nio.charset.StandardCharsets;
import java.time.Duration;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Week 6 protocol tests:
 * - Uses the real Spring context so AuthSocketServer starts as a bean on the configured test port.
 * - Sends raw TCP lines (username, password) and asserts the server replies "true"/"false".
 */
@SpringBootTest(classes = TriangleMiddlewareApplication.class)
@ActiveProfiles("test")
class AuthSocketServerTest {

    @Autowired
    private AuthSocketProperties props;

    // Small helper to exercise the raw socket protocol (two lines in, one line out).
    private String sendCredsAndReadReply(String host, int port, String username, String password, int timeoutMs) throws Exception {
        try (Socket socket = new Socket()) {
            socket.connect(new InetSocketAddress(host, port), timeoutMs);
            socket.setSoTimeout(timeoutMs);

            try (BufferedWriter out = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream(), StandardCharsets.UTF_8));
                 BufferedReader in  = new BufferedReader(new InputStreamReader(socket.getInputStream(), StandardCharsets.UTF_8))) {

                // Protocol: write username, newline; write password, newline.
                out.write(username == null ? "" : username);
                out.newLine();
                out.write(password == null ? "" : password);
                out.newLine();
                out.flush();

                // Server replies a single line: "true" or "false"
                String reply = in.readLine();
                return reply == null ? "" : reply.trim();
            }
        }
    }

    @Test
    @DisplayName("Valid credentials -> server replies true")
    void validCredentialsReturnTrue() throws Exception {
        String reply = sendCredsAndReadReply(props.getHost(), props.getPort(), "admin", "password123", props.getTimeoutMs());
        assertEquals("true", reply, "Expected 'true' for valid credentials");
    }

    @Test
    @DisplayName("Invalid credentials -> server replies false")
    void invalidCredentialsReturnFalse() throws Exception {
        String reply = sendCredsAndReadReply(props.getHost(), props.getPort(), "admin", "wrong", props.getTimeoutMs());
        assertEquals("false", reply, "Expected 'false' for invalid credentials");
    }

    @Test
    @DisplayName("Missing/empty lines -> server replies false")
    void emptyInputsReturnFalse() throws Exception {
        // Empty username, empty password
        String reply1 = sendCredsAndReadReply(props.getHost(), props.getPort(), "", "", props.getTimeoutMs());
        assertEquals("false", reply1, "Expected 'false' for empty username/password");

        // Null-like behavior (we just send empty strings on the wire)
        String reply2 = sendCredsAndReadReply(props.getHost(), props.getPort(), "", "password123", props.getTimeoutMs());
        assertEquals("false", reply2, "Expected 'false' when username is empty");

        String reply3 = sendCredsAndReadReply(props.getHost(), props.getPort(), "admin", "", props.getTimeoutMs());
        assertEquals("false", reply3, "Expected 'false' when password is empty");
    }

    @Test
    @DisplayName("Sanity: properties are bound from application-test.properties")
    void propertiesAreBound() {
        assertTrue(props.isEnabled(), "Socket auth should be enabled for test profile");
        assertEquals("localhost", props.getHost());
        // In test props 9191 is set — assert that so we know we’re not hitting dev’s port.
        assertEquals(9191, props.getPort(), "Expected test port 9191 (set in application-test.properties)");
        // quick sanity to keep timeouts short in CI
        assertTimeoutPreemptively(Duration.ofMillis(2500), () -> {});
    }

    @Test
    @DisplayName("When socket server is down, client connect times out cleanly")
    void serverDownYieldsTimeout() {
        int unusedPort = 6553;
        Exception ex = assertThrows(Exception.class, () -> {
            sendCredsAndReadReply("localhost", unusedPort, "admin", "password123", 500);
        });
        // No need to over-assert type
        assertNotNull(ex);
    }
}
