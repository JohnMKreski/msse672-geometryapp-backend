package org.msse672.geometryapp;

import java.io.IOException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * Entry point for the Triangle Middleware Spring Boot application.
 * Starts the application and attempts to open the home page in the default browser.
 */
@SpringBootApplication
public class TriangleMiddlewareApplication {

    private static final Logger logger = LoggerFactory.getLogger(TriangleMiddlewareApplication.class);

    /**
     * Main method to launch the Spring Boot application.
     * Attempts to open the home page after startup.
     */
    public static void main(String[] args) throws IOException {
        logger.info("Starting TriangleMiddlewareApplication...");
        SpringApplication.run(TriangleMiddlewareApplication.class, args);

        try {
            openHomePage();
        } catch (IOException e) {
            logger.error("Failed to open home page", e);
            // Exception is logged; stack trace is not printed to console.
        }
    }

    /**
     * Opens the application home page in the default browser based on OS.
     * Supports Windows, macOS, and Linux.
     * @throws IOException if the command fails or OS is unsupported
     */
    private static void openHomePage() throws IOException {
        String url = "http://localhost:8080";
        Runtime rt = Runtime.getRuntime();
        String os = System.getProperty("os.name").toLowerCase();

        logger.info("Attempting to open home page at {}", url);

        if (os.contains("win")) {
            // Windows: use rundll32 to open URL
            rt.exec(new String[] { "rundll32", "url.dll,FileProtocolHandler", url });
        } else if (os.contains("mac")) {
            // macOS: use open command
            rt.exec(new String[] { "open", url });
        } else if (os.contains("nix") || os.contains("nux")) {
            // Linux/Unix: use xdg-open
            rt.exec(new String[] { "xdg-open", url });
        } else {
            logger.warn("Unsupported operating system: {}", os);
            throw new UnsupportedOperationException("Unsupported operating system: " + os);
        }
    }
}