package org.msse672.geometryapp.auth.socket.client;


import java.io.*;
import java.net.Socket;
import java.util.Scanner;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

/**
 * A simple client program that connects to the AuthSocketServer via a raw socket.
 * Sends a username and password, then prints the authentication response.
 * Cli for testing only, not part of the main application.
 */
@Component
public class AuthSocketClient {

    private static final Logger logger = LoggerFactory.getLogger(AuthSocketClient.class);

    public static void main(String[] args) {
        String serverHost = "localhost";  // Server address
        int port = 9090;                  // Server port

        try (
                // Establish a socket connection to the server
                Socket socket = new Socket(serverHost, port);

                // Create writer to send data to server
                PrintWriter writer = new PrintWriter(socket.getOutputStream(), true);

                // Create reader to receive server response
                BufferedReader reader = new BufferedReader(new InputStreamReader(socket.getInputStream()));

                // Scanner for reading user input from terminal
                Scanner scanner = new Scanner(System.in)
        ) {
            logger.info("Connecting to server at {}:{}", serverHost, port);

            // Ask for username
            System.out.print("Enter username: ");
            String username = scanner.nextLine();
            writer.println(username);  // Send to server
            logger.debug("Sending credentials: {}", username);

            // Ask for password
            System.out.print("Enter password: ");
            String password = scanner.nextLine();
            writer.println(password);  // Send to server
            logger.debug("Sending credentials: {}", password);

            // Read and print token (if any)
            String token = reader.readLine();

            if (token != null && !token.isBlank()) {
                System.out.println("Authentication successful. Token: " + token);
                logger.info("Authentication token received: {}", token);
            } else {
                System.out.println("Authentication failed.");
                logger.warn("No token received — authentication failed.");
            }


        } catch (IOException e) {
            System.err.println("Connection error: " + e.getMessage());
            logger.error("Authentication failed or error occurred", e);
        }
    }
}
