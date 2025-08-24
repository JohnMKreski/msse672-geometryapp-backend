package org.msse672.geometryapp.auth.socket;

    import org.msse672.geometryapp.auth.config.AuthSocketProperties;
    import org.slf4j.Logger;
    import org.slf4j.LoggerFactory;
    import org.springframework.beans.factory.annotation.Autowired;
    import org.springframework.beans.factory.annotation.Qualifier;
    import org.springframework.stereotype.Component;

    import java.io.*;
    import java.net.InetSocketAddress;
    import java.net.Socket;
    import java.nio.charset.StandardCharsets;

    /**
     * Default implementation of SocketAuthClient.
     * Communicates with AuthSocketServer using a simple text protocol over TCP.
     */
    @Component
    @Qualifier("defaultSocketAuthClient")
    public class DefaultSocketAuthClient implements SocketAuthClient {
        private static final Logger log = LoggerFactory.getLogger(DefaultSocketAuthClient.class);

        // Configuration properties for socket connection
        private final AuthSocketProperties props;

        // Constructor injection for configuration
        @Autowired
        public DefaultSocketAuthClient(AuthSocketProperties props) {
            this.props = props;
        }

        /**
         * Authenticates a user by sending username and password to the AuthSocketServer.
         * Protocol: send username, send password, read "true"/"false", read token or "false".
         * Returns token if authentication succeeds, otherwise null.
         */
        @Override
        public String sendAuthRequest(String username, String password) throws Exception {
            try (Socket socket = new Socket()) {
                // Connect to AuthSocketServer with configured host, port, and timeout
                socket.connect(new InetSocketAddress(props.getHost(), props.getPort()), props.getTimeoutMs());
                socket.setSoTimeout(props.getTimeoutMs());

                try (BufferedWriter out = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream(), StandardCharsets.UTF_8));
                     BufferedReader in  = new BufferedReader(new InputStreamReader(socket.getInputStream(), StandardCharsets.UTF_8))) {

                    // Send username and password, each on a new line
                    out.write(username == null ? "" : username);
                    out.newLine();
                    out.write(password == null ? "" : password);
                    out.newLine();
                    out.flush();

                    // Read authentication result and token
                    String authSuccess = in.readLine();
                    String tokenOrFail = in.readLine();

                    log.debug("Socket auth result for '{}': {}", username, authSuccess);
                    log.debug("Socket auth token for '{}': {}", username, tokenOrFail);

                    // Return null if authentication failed or token is blank
                    if (tokenOrFail == null || tokenOrFail.isBlank() || "false".equalsIgnoreCase(tokenOrFail.trim())) {
                        return null;
                    }
                    // Return token if authentication succeeded
                    return (tokenOrFail);
                }
            }
        }

        /**
         * Validates a token by sending it to the AuthSocketServer.
         * Protocol: send "TOKEN_CHECK", send token, read "true"/"false".
         * Returns true if token is valid, false otherwise.
         */
        @Override
        public boolean sendTokenValidation(String token) throws Exception {
            try (Socket socket = new Socket()) {
                // Connect to AuthSocketServer
                socket.connect(new InetSocketAddress(props.getHost(), props.getPort()), props.getTimeoutMs());
                socket.setSoTimeout(props.getTimeoutMs());

                try (BufferedWriter out = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream(), StandardCharsets.UTF_8));
                     BufferedReader in  = new BufferedReader(new InputStreamReader(socket.getInputStream(), StandardCharsets.UTF_8))) {

                    // Send token validation marker and token
                    out.write("TOKEN_CHECK");
                    out.newLine();
                    out.write(token == null ? "" : token);
                    out.newLine();
                    out.flush();

                    // Read validation result
                    String line = in.readLine();
                    boolean ok = Boolean.parseBoolean(line != null ? line.trim() : "false");
                    log.debug("Socket token check result: {}", ok);
                    return ok;
                }
            }
        }

        /**
         * Revokes a token by sending it to the AuthSocketServer.
         * Protocol: send "TOKEN_REVOKE", send token, read "true"/"false".
         * Returns true if revocation succeeded, false otherwise.
         */
        @Override
        public boolean revokeToken(String token) throws Exception {
            try (Socket socket = new Socket()) {
                // Connect to AuthSocketServer
                socket.connect(new InetSocketAddress(props.getHost(), props.getPort()), props.getTimeoutMs());
                socket.setSoTimeout(props.getTimeoutMs());

                try (
                        BufferedWriter out = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream(), StandardCharsets.UTF_8));
                        BufferedReader in  = new BufferedReader(new InputStreamReader(socket.getInputStream(), StandardCharsets.UTF_8))
                ) {
                    // Send token revocation marker and token
                    out.write("TOKEN_REVOKE");
                    out.newLine();
                    out.write(token == null ? "" : token);
                    out.newLine();
                    out.flush();

                    // Read revocation result
                    String response = in.readLine();
                    boolean success = Boolean.parseBoolean(response != null ? response.trim() : "false");
                    log.debug("Token revoke result: {}", success);
                    return success;
                }
            }
        }
    }