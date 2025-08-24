package org.msse672.geometryapp.service;

import org.msse672.geometryapp.model.Quadrilateral;
import org.msse672.geometryapp.model.QuadrilateralSet;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * In-memory implementation of QuadService.
 * Stores quadrilateral side values and history for the current application session.
 * Data is not persisted and will be lost on application restart.
 */
@Service
@Qualifier("inMemory") // Enables switching between in-memory and JDBC implementations
public class QuadServiceInMemoryImpl implements QuadService {

    private static final Logger logger = LoggerFactory.getLogger(QuadServiceInMemoryImpl.class);

    // Current quadrilateral side values
    private double sideA;
    private double sideB;
    private double sideC;
    private double sideD;

    // Tracks if sides have been initialized by user input
    private boolean initialized = false;

    // Stores history of submitted quadrilaterals
    private final QuadrilateralSet history;

    /**
     * Constructor injects the QuadrilateralSet for history management.
     */
    public QuadServiceInMemoryImpl(QuadrilateralSet history) {
        this.history = history;
    }

    /**
     * Checks if quadrilateral sides have been initialized.
     */
    @Override
    public boolean isInitialized() {
        logger.debug("Checking if quad is initialized: {}", initialized);
        return initialized;
    }

    // Getters for current quadrilateral side values
    @Override
    public double getSideA() {
        logger.debug("Fetching sideA: {}", sideA);
        return sideA;
    }
    @Override
    public double getSideB() {
        logger.debug("Fetching sideB: {}", sideB);
        return sideB;
    }
    @Override
    public double getSideC() {
        logger.debug("Fetching sideC: {}", sideC);
        return sideC;
    }
    @Override
    public double getSideD() {
        logger.debug("Fetching sideD: {}", sideD);
        return sideD;
    }

    /**
     * Updates the current quadrilateral sides and adds to history.
     * Called by POST and PUT endpoints.
     */
    @Override
    public void updateSides(double sideA, double sideB, double sideC, double sideD) {
        logger.info("Updating sides with A={}, B={}, C={}, D={}", sideA, sideB, sideC, sideD);
        this.sideA = sideA;
        this.sideB = sideB;
        this.sideC = sideC;
        this.sideD = sideD;
        this.initialized = true;

        Quadrilateral quad = new Quadrilateral(sideA, sideB, sideC, sideD);
        history.addQuadrilateral(quad);

        logger.debug("Quad sides updated. Initialized state set to true.");
    }

    /**
     * Resets the initialized state and clears current side values.
     */
    @Override
    public void reset() {
        logger.info("Resetting quad side values and state.");
        this.initialized = false;
    }

    // ==================== History and Analysis Methods ====================

    /**
     * Returns all submitted quadrilaterals in history.
     */
    @Override
    public List<Quadrilateral> getAllSubmittedQuads() {
        return history.getAll();
    }

    /**
     * Counts submitted quadrilaterals by type.
     */
    @Override
    public Map<String, Long> countByType() {
        return history.getAll().stream()
                .collect(Collectors.groupingBy(
                        Quadrilateral::getType,
                        Collectors.counting()
                ));
    }

    /**
     * Returns only quadrilaterals classified as squares.
     */
    @Override
    public List<Quadrilateral> getOnlySquares() {
        return history.getAll().stream()
                .filter(q -> q.getType().contains("Square"))
                .collect(Collectors.toList());
    }

    /**
     * Finds the largest side value ever submitted.
     */
    @Override
    public double getLargestSideEverSubmitted() {
        return history.getAll().stream()
                .flatMap(q -> Stream.of(q.getSideA(), q.getSideB(), q.getSideC(), q.getSideD()))
                .max(Double::compare)
                .orElse(0.0);
    }

    /**
     * Returns the last submitted quadrilateral.
     */
    @Override
    public Quadrilateral getLastSubmittedQuad() {
        return history.getLast();
    }

    // ==================== Stubbed JDBC Methods ====================

    /**
     * Not supported in in-memory implementation.
     */
    @Override
    public void updateQuadById(Long id, double a, double b, double c, double d) {
        throw new UnsupportedOperationException("updateQuadById is not implemented for in-memory service.");
    }

    /**
     * Not supported in in-memory implementation.
     */
    @Override
    public Quadrilateral getById(Long id) {
        throw new UnsupportedOperationException("getById is not implemented for in-memory service.");
    }

    /**
     * Not supported in in-memory implementation.
     */
    @Override
    public void insertQuad(double sideA, double sideB, double sideC, double sideD) {
        throw new UnsupportedOperationException("insertQuad is not implemented for in-memory service.");
    }

    /**
     * Not supported in in-memory implementation.
     */
    @Override
    public void deleteById(Long id) {
        throw new UnsupportedOperationException("deleteById is not implemented for in-memory service.");
    }
}