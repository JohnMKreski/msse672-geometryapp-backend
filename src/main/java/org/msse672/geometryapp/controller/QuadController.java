package org.msse672.geometryapp.controller;

import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import jakarta.servlet.http.HttpServletRequest;
import org.msse672.geometryapp.auth.core.AuthService;
import org.msse672.geometryapp.model.Quadrilateral;
import org.msse672.geometryapp.service.QuadService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Collections;
import java.util.List;
import java.util.Map;

import org.msse672.geometryapp.dto.QuadResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * REST controller for quadrilateral evaluation.
 * Provides endpoints for submitting, updating, retrieving, and deleting quadrilaterals.
 */
@RestController
@RequestMapping("/quad")
public class QuadController {

    private static final Logger logger = LoggerFactory.getLogger(QuadController.class);

    private final QuadService quadService;
    private final AuthService authService;

    // Use @Qualifier to select QuadService implementation
    @Autowired
    public QuadController(@Qualifier("hibernate") QuadService quadService, AuthService authService) {
        this.quadService = quadService;
        this.authService = authService;
    }

    @Autowired
    private HttpServletRequest request;

    /**
     * POST /quad/type
     * Accepts side lengths as query parameters, validates, and returns quadrilateral type.
     */
//    @SecurityRequirement(name = "X-Auth-Token")
    @PostMapping("/type")
    public ResponseEntity<?> postQuadrilateral(
            @RequestParam double sideA,
            @RequestParam double sideB,
            @RequestParam double sideC,
            @RequestParam double sideD
    ) {
        if (Quadrilateral.hasNullOrInvalid(sideA, sideB, sideC, sideD)) {
            logger.warn("Received null or invalid input: A={}, B={}, C={}, D={}", sideA, sideB, sideC, sideD);
            return ResponseEntity.badRequest().body(Map.of("error", "All inputs must be numeric and non-null."));
        }

        if (!Quadrilateral.isValidQuadrilateral(sideA, sideB, sideC, sideD)) {
            logger.error("Invalid quadrilateral detected: A={}, B={}, C={}, D={}", sideA, sideB, sideC, sideD);
            quadService.reset();
            return ResponseEntity.badRequest().body(Map.of("error", "Invalid Quadrilateral: violates quadrilateral side length rules."));
        }

        Quadrilateral quad = new Quadrilateral(sideA, sideB, sideC, sideD);
        String type = quad.getType();

        if (type.startsWith("Invalid Quadrilateral")) {
            quadService.reset();
            return ResponseEntity.badRequest().body(Map.of("error", type));
        }

        quadService.insertQuad(sideA, sideB, sideC, sideD);
        QuadResponse response = new QuadResponse(sideA, sideB, sideC, sideD, type);
        logger.info("Valid quadrilateral submitted: A={}, B={}, C={}, D={}, Type={}", sideA, sideB, sideC, sideD, type);
        return ResponseEntity.ok(response);
    }

    /**
     * GET /quad/type
     * Returns the type and sides of the current quadrilateral in memory.
     * Requires prior POST initialization.
     */

@GetMapping("/type")
public ResponseEntity<?> getQuadrilateral(@RequestParam Long id) {
    logger.info("GET /quad/type/{}: Attempting to get quad by ID", id);
    if (!quadService.isInitialized()) {
        logger.warn("GET /quad/type attempted before initialization.");
        return ResponseEntity.badRequest().body(Collections.singletonMap("error", "Please POST sides first."));
    }

    try {
        Quadrilateral quad = quadService.getById(id);
        logger.info("GET /quad/type/{} returned: {}", id, quad);
        return ResponseEntity.ok(quad);
    } catch (Exception e) {
        logger.warn("GET /quad/type: Quad ID {} not found", id);
        return ResponseEntity.badRequest().body(Map.of("error", "Quadrilateral with ID " + id + " not found."));
    }
}

    /**
     * PUT /quad/type
     * Updates the sides of an existing quadrilateral by ID.
     * Requires prior POST initialization.
     */
    @PutMapping("/type")
    public ResponseEntity<?> putQuadrilateral(
            @RequestParam Long id,
            @RequestParam double sideA,
            @RequestParam double sideB,
            @RequestParam double sideC,
            @RequestParam double sideD
    ) {
        if (!quadService.isInitialized()) {
            logger.warn("PUT /quad/type: Attempted update before initialization.");
            return ResponseEntity.badRequest().body(Collections.singletonMap("error", "Please POST sides first."));
        }

        try {
            quadService.getById(id);
        } catch (Exception e) {
            logger.warn("PUT /quad/type: Quad ID {} not found", id);
            return ResponseEntity.badRequest().body(Map.of("error", "Quadrilateral with ID " + id + " not found."));
        }

        if (Quadrilateral.hasNullOrInvalid(sideA, sideB, sideC, sideD)) {
            logger.warn("PUT /quad/type: Invalid input: A={}, B={}, C={}, D={}", sideA, sideB, sideC, sideD);
            return ResponseEntity.badRequest().body(Map.of("error", "All inputs must be numeric and non-null."));
        }

        if (!Quadrilateral.isValidQuadrilateral(sideA, sideB, sideC, sideD)) {
            logger.error("PUT /quad/type: Invalid quadrilateral: A={}, B={}, C={}, D={}", sideA, sideB, sideC, sideD);
            quadService.reset();
            return ResponseEntity.badRequest().body(Map.of("error", "Invalid Quadrilateral: violates quadrilateral side length rules."));
        }

        quadService.updateQuadById(id, sideA, sideB, sideC, sideD);
        String type = new Quadrilateral(sideA, sideB, sideC, sideD).getType();
        logger.info("PUT /quad/type: Updated ID={}, sides to A={}, B={}, C={}, D={}, Type={}", id, sideA, sideB, sideC, sideD, type);
        QuadResponse response = new QuadResponse(sideA, sideB, sideC, sideD, type);
        return ResponseEntity.ok(response);
    }

    /**
     * DELETE /quad/type
     * Resets the quadrilateral data in memory.
     * Requires prior POST initialization.
     */
    @DeleteMapping("/type")
    public ResponseEntity<?> deleteQuadrilateral() {
        if (!quadService.isInitialized()) {
            logger.warn("DELETE /quad/type attempted before initialization.");
            return ResponseEntity.badRequest().body(Collections.singletonMap("error", "Please POST sides first."));
        }
        quadService.reset();
        logger.info("DELETE /quad/type: Quadrilateral data has been reset.");
        return ResponseEntity.ok(Collections.singletonMap("type", "Quadrilateral data has been reset."));
    }

    /**
     * DELETE /quad/type/{id}
     * Deletes a quadrilateral by its ID.
     */
    @DeleteMapping("/type/{id}")
    public ResponseEntity<?> deleteQuadById(@PathVariable Long id) {
        logger.info("DELETE /quad/type/{}: Attempting to delete quad by ID", id);
        try {
            quadService.deleteById(id);
            return ResponseEntity.ok(Map.of("message", "Deleted quad with ID " + id));
        } catch (IllegalArgumentException ex) {
            logger.warn("DELETE /quad/type/{}: Failed to delete - {}", id, ex.getMessage());
            return ResponseEntity.badRequest().body(Map.of("error", ex.getMessage()));
        }
    }

    // ======================== Collection Endpoints ========================

    /**
     * GET /quad/history/allQuads
     * Returns all submitted quadrilaterals.
     */
    @GetMapping("/history/allQuads")
    public ResponseEntity<?> getAllQuads() {
        logger.info("GET /history called.");

        if (!quadService.isInitialized()) {
            logger.warn("GET /quads attempted before initialization.");
            return ResponseEntity.badRequest().body(Collections.singletonMap("error", "Please POST sides first."));
        }

        List<Quadrilateral> quads = quadService.getAllSubmittedQuads();
        if (quads.isEmpty()) {
            logger.info("GET /quads returned empty array.");
            return ResponseEntity.ok(Collections.singletonMap("message", "No quadrilaterals submitted yet."));
        }

        logger.info("GET /quads successful. Retrieved {} quadrilaterals.", quads.size());
        return ResponseEntity.ok(quads);
    }

    /**
     * GET /quad/history/squares
     * Returns all submitted squares.
     */
    @GetMapping("/history/squares")
    public ResponseEntity<?> getOnlySquares() {
        logger.info("GET /history/squares called.");

        if (!quadService.isInitialized()) {
            logger.warn("GET /history/squares attempted before initialization.");
            return ResponseEntity.badRequest().body(Collections.singletonMap("error", "Please POST sides first."));
        }

        List<Quadrilateral> squares = quadService.getOnlySquares();
        if (squares.isEmpty()) {
            logger.info("GET /history/squares returned empty array.");
            return ResponseEntity.ok(Collections.singletonMap("message", "No squares submitted yet."));
        }

        logger.info("GET /history/squares successful. Retrieved {} squares.", squares.size());
        return ResponseEntity.ok(squares);
    }

    /**
     * GET /quad/history/stats
     * Returns statistics for submitted quadrilaterals by type.
     */
    @GetMapping("/history/stats")
    public ResponseEntity<?> getQuadStats() {
        logger.info("GET /history/stats called.");

        if (!quadService.isInitialized()) {
            logger.warn("GET /history/stats attempted before initialization.");
            return ResponseEntity.badRequest().body(Collections.singletonMap("error", "Please POST sides first."));
        }

        Map<String, Long> stats = quadService.countByType();
        logger.info("GET /history/stats successful. Stats: {}", stats);
        return ResponseEntity.ok(stats);
    }

    /**
     * GET /quad/quads/last
     * Returns the last submitted quadrilateral.
     */
    @GetMapping("/quads/last")
    public ResponseEntity<?> getLastQuad() {
        Quadrilateral last = quadService.getLastSubmittedQuad();
        if (last == null) {
            logger.warn("No last quadrilateral found.");
            return ResponseEntity.badRequest().body(Map.of("error", "Please POST sides first."));
        }
        logger.info("Retrieved last quad: {}", last);
        return ResponseEntity.ok(last);
    }
}