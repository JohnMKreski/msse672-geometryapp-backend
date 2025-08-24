package org.msse672.geometryapp.auth.socket;

        import jakarta.annotation.PostConstruct;
        import jakarta.annotation.PreDestroy;
        import org.msse672.geometryapp.auth.core.AuthService;
        import org.msse672.geometryapp.auth.config.AuthSocketProperties;
        import org.slf4j.Logger;
        import org.slf4j.LoggerFactory;
        import org.springframework.beans.factory.annotation.Autowired;
        import org.springframework.stereotype.Component;

        import java.io.*;
        import java.net.ServerSocket;
        import java.net.Socket;
        import java.util.concurrent.ExecutorService;
        import java.util.concurrent.Executors;

        /**
         * Socket-based authentication server.
         * Listens for client connections and handles authentication, token checks, and token revocation.
         */
        @Component
        public class AuthSocketServer {

            private static final Logger logger = LoggerFactory.getLogger(AuthSocketServer.class);

            // Service for authentication logic (login, token validation, logout)
            private final AuthService authService;
            // Configuration properties for socket server (host, port, enabled)
            private final AuthSocketProperties props;

            private ServerSocket serverSocket;
            private ExecutorService executorService;

            @Autowired
            public AuthSocketServer(AuthService authService, AuthSocketProperties props) {
                this.authService = authService;
                this.props = props;
                logger.info("AuthSocketServer initialized with host={}, port={}, enabled={}",
                        props.getHost(), props.getPort(), props.isEnabled());
            }

            /**
             * Starts the socket server and listens for incoming client connections.
             * Runs in a background daemon thread.
             */
            @PostConstruct
            public void startServer() {
                logger.info("Starting AuthSocketServer...");

                // Only start if enabled in configuration
                if (!props.isEnabled()) {
                    logger.info("AuthSocketServer is disabled via configuration.");
                    return;
                }

                // Use a cached thread pool for handling multiple clients concurrently
                executorService = Executors.newCachedThreadPool();

                Thread serverThread = new Thread(() -> {
                    try {
                        // Bind server socket to configured port
                        serverSocket = new ServerSocket(props.getPort());
                        logger.info("Auth socket server started on {}:{}", props.getHost(), props.getPort());

                        // Accept client connections in a loop
                        while (!serverSocket.isClosed()) {
                            Socket clientSocket = serverSocket.accept();
                            logger.debug("Accepted connection from {}", clientSocket.getRemoteSocketAddress());
                            // Handle each client in a separate thread
                            executorService.submit(() -> handleClient(clientSocket));
                        }
                    } catch (IOException e) {
                        if (serverSocket != null && serverSocket.isClosed()) {
                            logger.info("startServer: Server socket closed.");
                        } else {
                            logger.error("Socket server error: {}", e.getMessage(), e);
                        }
                    }
                }, "auth-socket-server");

                serverThread.setDaemon(true);
                serverThread.start();
            }

            /**
             * Stops the socket server and releases resources.
             * Called automatically on bean destruction.
             */
            @PreDestroy
            public void stopServer() {
                logger.info("Stopping AuthSocketServer...");
                try {
                    // Close server socket if open
                    if (serverSocket != null && !serverSocket.isClosed()) {
                        serverSocket.close();
                        logger.info("stopServer: Server socket closed.");
                    }
                    // Shut down thread pool
                    if (executorService != null) executorService.shutdownNow();
                    logger.info("Executor service shut down.");
                } catch (IOException e) {
                    logger.error("Error during shutdown: {}", e.getMessage(), e);
                }
            }

            /**
             * Handles a single client socket request.
             * Reads two lines from the client and dispatches to the appropriate handler.
             */
            private void handleClient(Socket clientSocket) {
                logger.debug("Handling client from {}", clientSocket.getRemoteSocketAddress());
                try (
                        BufferedReader reader = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
                        PrintWriter writer = new PrintWriter(clientSocket.getOutputStream(), true)
                ) {
                    // Read protocol lines from client
                    String line1 = reader.readLine();
                    String line2 = reader.readLine();

                    // If input is invalid, respond with failure
                    if (line1 == null || line2 == null) {
                        logger.warn("Null input received from client. Closing connection.");
                        writer.println(false);
                        return;
                    }

                    logger.info("Received request: line1='{}', line2='{}'", line1, line2);

                    // Dispatch based on first line (protocol marker)
                    switch (line1) {
                        case "TOKEN_CHECK" -> handleTokenCheck(line2, writer);   // Token validation
                        case "TOKEN_REVOKE" -> handleTokenRevoke(line2, writer); // Token revocation
                        default -> handleLogin(line1, line2, writer);            // Username/password authentication
                    }

                } catch (IOException e) {
                    logger.error("Error handling client: {}", e.getMessage(), e);
                } finally {
                    try {
                        clientSocket.close();
                        logger.debug("Closed connection with {}", clientSocket.getRemoteSocketAddress());
                    } catch (IOException ignore) {}
                }
            }

            /**
             * Checks if a given token is valid and writes the result to the client.
             * @param token The token to validate
             * @param writer Output stream to client
             */
            private void handleTokenCheck(String token, PrintWriter writer) {
                logger.info("Checking token: '{}'", token);
                boolean valid = authService.isTokenValid(token);
                writer.println(valid);
                logger.info("Token valid: {}", valid);
            }

            /**
             * Revokes (logs out) the given token and confirms to the client.
             * @param token The token to revoke
             * @param writer Output stream to client
             */
            private void handleTokenRevoke(String token, PrintWriter writer) {
                logger.info("Revoking token: '{}'", token);
                authService.logout(token);
                writer.println(true);
                logger.info("Token revoked.");
            }

            /**
             * Authenticates a user with username and password, returns a token if successful.
             * @param username The username
             * @param password The password
             * @param writer Output stream to client
             */
            private void handleLogin(String username, String password, PrintWriter writer) {
                logger.info("Login attempt for user: '{}'", username);
                String token = null;
                try {
                    token = authService.authenticate(username, password);
                } catch (Exception e) {
                    logger.error("Authentication failed for user '{}': {}", username, e.getMessage());
                }

                boolean authenticated = (token != null);
                writer.println(authenticated);

                if (authenticated) {
                    writer.println(token);
                    logger.info("Authentication successful for '{}', token issued.", username);
                } else {
                    writer.println("false");
                    logger.info("Authentication failed for '{}'.", username);
                }
            }
        }