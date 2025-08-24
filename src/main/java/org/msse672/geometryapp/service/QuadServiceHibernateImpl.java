package org.msse672.geometryapp.service;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import org.msse672.geometryapp.model.Quadrilateral;
import org.msse672.geometryapp.repository.QuadRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.DoubleStream;

/**
 * Hibernate-based implementation of QuadService.
 * Handles quadrilateral persistence and retrieval using Spring Data JPA.
 */
@Service
@Qualifier("hibernate")
public class QuadServiceHibernateImpl implements QuadService {

    @PersistenceContext
    private EntityManager entityManager;

    private static final Logger log = LoggerFactory.getLogger(QuadServiceHibernateImpl.class);

    private final QuadRepository quadRepository;

    /**
     * Constructor injects the QuadRepository for database operations.
     */
    @Autowired
    public QuadServiceHibernateImpl(QuadRepository quadRepository) {
        this.quadRepository = quadRepository;
    }

    /**
     * Checks if any quadrilaterals exist in the database.
     * @return true if at least one quadrilateral is present
     */
    @Override
    public boolean isInitialized() {
        long count = quadRepository.count();
        boolean initialized = count > 0;
        log.debug("isInitialized() check via Hibernate: count = {}, initialized = {}", count, initialized);
        return initialized;
    }

    /**
     * Inserts a new quadrilateral after validation.
     * @throws IllegalArgumentException if validation fails
     */
    @Override
    public void insertQuad(double sideA, double sideB, double sideC, double sideD) {
        String err = Quadrilateral.validate(sideA, sideB, sideC, sideD);
        if (err != null) {
            log.warn("Attempted to insert invalid quadrilateral: {}", err);
            throw new IllegalArgumentException("Invalid Quadrilateral: " + err);
        }

        Quadrilateral quad = new Quadrilateral(sideA, sideB, sideC, sideD);
        quadRepository.save(quad);
        log.info("Inserted new quadrilateral: {}", quad);
    }

    /**
     * Retrieves the last submitted quadrilateral.
     * @return last Quadrilateral or null if none exist
     */
    @Override
    public Quadrilateral getLastSubmittedQuad() {
        List<Quadrilateral> allQuads = quadRepository.findAll();
        if (allQuads.isEmpty()) {
            log.warn("No quads found in database.");
            return null;
        }

        Quadrilateral last = allQuads.get(allQuads.size() - 1);
        log.debug("Returning last submitted quadrilateral: {}", last);
        return last;
    }

    /**
     * Retrieves a quadrilateral by its ID.
     * @throws IllegalArgumentException if not found
     */
    @Override
    public Quadrilateral getById(Long id) {
        return quadRepository.findById(id)
                .orElseThrow(() -> {
                    log.warn("Quadrilateral with ID {} not found.", id);
                    return new IllegalArgumentException("Quadrilateral with ID " + id + " does not exist.");
                });
    }

    /**
     * Retrieves all submitted quadrilaterals.
     * @return list of all Quadrilaterals
     */
    @Override
    public List<Quadrilateral> getAllSubmittedQuads() {
        List<Quadrilateral> all = quadRepository.findAll();
        log.debug("Retrieved {} quadrilateral(s) from database.", all.size());
        return all;
    }

    /**
     * Counts quadrilaterals by their type.
     * @return map of type to count
     */
    @Override
    public Map<String, Long> countByType() {
        List<Quadrilateral> quads = getAllSubmittedQuads();
        log.debug("Counting by type from {} quads", quads.size());

        Map<String, Long> typeCounts = quads.stream()
                .collect(Collectors.groupingBy(Quadrilateral::getType, Collectors.counting()));

        typeCounts.forEach((type, count) ->
                log.debug("Type '{}' has count {}", type, count));

        return typeCounts;
    }

    /**
     * Retrieves only quadrilaterals classified as squares.
     * @return list of squares
     */
    @Override
    public List<Quadrilateral> getOnlySquares() {
        List<Quadrilateral> all = getAllSubmittedQuads();
        List<Quadrilateral> squares = all.stream()
                .filter(q -> q.getType().contains("Square"))
                .collect(Collectors.toList());

        log.debug("Filtered {} squares from {} total quads", squares.size(), all.size());
        return squares;
    }

    /**
     * Finds the largest side value ever submitted.
     * @return largest side or 0 if none exist
     */
    @Override
    public double getLargestSideEverSubmitted() {
        List<Quadrilateral> quads = quadRepository.findAll();

        double largest = quads.stream()
                .flatMapToDouble(q -> DoubleStream.of(q.getSideA(), q.getSideB(), q.getSideC(), q.getSideD()))
                .max()
                .orElse(0);

        log.debug("Largest side found among {} quads: {}", quads.size(), largest);
        return largest;
    }

    /**
     * Updates a quadrilateral by its ID after validation.
     * @throws IllegalArgumentException if not found or validation fails
     */
    @Override
    public void updateQuadById(Long id, double sideA, double sideB, double sideC, double sideD) {
        if (!quadRepository.existsById(id)) {
            log.warn("Attempted to update non-existent quadrilateral with ID {}", id);
            throw new IllegalArgumentException("Quadrilateral with ID " + id + " does not exist.");
        }

        String err = Quadrilateral.validate(sideA, sideB, sideC, sideD);
        if (err != null) {
            log.warn("Attempted to update with invalid quadrilateral: {}", err);
            throw new IllegalArgumentException("Invalid Quadrilateral: " + err);
        }

        Quadrilateral updated = new Quadrilateral(sideA, sideB, sideC, sideD);
        updated.setId(id);
        quadRepository.save(updated);
        log.info("Updated quadrilateral ID {}: {}", id, updated);
    }

    /**
     * Deletes all quadrilaterals from the database.
     */
    @Override
    public void reset() {
        log.warn("Deleting all quadrilaterals from database.");
        quadRepository.deleteAll();
    }

    /**
     * Deletes a quadrilateral by its ID.
     * @throws IllegalArgumentException if not found
     */
    @Override
    public void deleteById(Long id) {
        if (!quadRepository.existsById(id)) {
            log.warn("Attempted to delete non-existent quadrilateral with ID {}", id);
            throw new IllegalArgumentException("Quadrilateral with ID " + id + " does not exist.");
        }

        quadRepository.deleteById(id);
        log.info("Deleted quadrilateral with ID {}", id);
    }

    // Stubbed methods for interface compliance (not used in Hibernate implementation)

    @Override
    public double getSideA() {
        log.warn("getSideA() is not applicable in Hibernate implementation.");
        return 0;
    }

    @Override
    public double getSideB() {
        log.warn("getSideB() is not applicable in Hibernate implementation.");
        return 0;
    }

    @Override
    public double getSideC() {
        log.warn("getSideC() is not applicable in Hibernate implementation.");
        return 0;
    }

    @Override
    public double getSideD() {
        log.warn("getSideD() is not applicable in Hibernate implementation.");
        return 0;
    }

    @Override
    public void updateSides(double sideA, double sideB, double sideC, double sideD) {
        log.warn("updateSides() is not used in Hibernate. Use insertQuad() or updateQuadById().");
    }
}