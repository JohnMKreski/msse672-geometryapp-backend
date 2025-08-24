package org.msse672.geometryapp.service;

import org.msse672.geometryapp.model.Quadrilateral;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * JDBC-based implementation of QuadService.
 * Handles quadrilateral persistence and retrieval using JdbcTemplate.
 */
@Service
@Qualifier("jdbc")
public class QuadServiceJdbcImpl implements QuadService {

    private static final Logger log = LoggerFactory.getLogger(QuadServiceJdbcImpl.class);

    private final JdbcTemplate jdbcTemplate;

    /**
     * Constructor injects JdbcTemplate for database operations.
     */
    public QuadServiceJdbcImpl(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    /**
     * Checks if a quadrilateral with the given ID exists in the database.
     */
    private boolean existsById(Long id) {
        String sql = "SELECT COUNT(*) FROM quads WHERE id=?";
        Integer count = jdbcTemplate.queryForObject(sql, Integer.class, id);
        return count != null && count > 0;
    }

    /**
     * Maps a SQL result row to a Quadrilateral object.
     */
    private RowMapper<Quadrilateral> mapRowToQuad() {
        return (rs, rowNum) -> new Quadrilateral(
                rs.getLong("id"),
                rs.getDouble("sideA"),
                rs.getDouble("sideB"),
                rs.getDouble("sideC"),
                rs.getDouble("sideD")
        );
    }

    /**
     * Checks if any quadrilaterals exist in the database.
     */
    @Override
    public boolean isInitialized() {
        String sql = "SELECT COUNT(*) FROM quads";
        Integer count = jdbcTemplate.queryForObject(sql, Integer.class);
        return count != null && count > 0;
    }

    // Getters for the sides of the last submitted quadrilateral
    @Override
    public double getSideA() {
        String sql = "SELECT sideA FROM quads ORDER BY id DESC LIMIT 1";
        return jdbcTemplate.queryForObject(sql, Double.class);
    }
    @Override
    public double getSideB() {
        String sql = "SELECT sideB FROM quads ORDER BY id DESC LIMIT 1";
        return jdbcTemplate.queryForObject(sql, Double.class);
    }
    @Override
    public double getSideC() {
        String sql = "SELECT sideC FROM quads ORDER BY id DESC LIMIT 1";
        return jdbcTemplate.queryForObject(sql, Double.class);
    }
    @Override
    public double getSideD() {
        String sql = "SELECT sideD FROM quads ORDER BY id DESC LIMIT 1";
        return jdbcTemplate.queryForObject(sql, Double.class);
    }

    /**
     * Retrieves a quadrilateral by its ID.
     * @throws IllegalArgumentException if not found
     */
    @Override
    public Quadrilateral getById(Long id) {
        String sql = "SELECT id, sideA, sideB, sideC, sideD FROM quads WHERE id = ?";
        return jdbcTemplate.queryForObject(sql, mapRowToQuad(), id);
    }

    /**
     * Updates the sides of an existing quadrilateral by ID.
     * @throws IllegalArgumentException if not found or validation fails
     */
    @Override
    public void updateQuadById(Long id, double sideA, double sideB, double sideC, double sideD) {
        if (!existsById(id)) {
            log.warn("Attempted to update non-existent quad with ID {}", id);
            throw new IllegalArgumentException("Quadrilateral with ID " + id + " does not exist.");
        }

        String err = Quadrilateral.validate(sideA, sideB, sideC, sideD);
        if (err != null) {
            log.warn("Attempted to update with invalid quadrilateral: {}", err);
            throw new IllegalArgumentException("Invalid Quadrilateral: " + err);
        }

        String sql = "UPDATE quads SET sideA = ?, sideB = ?, sideC = ?, sideD = ? WHERE id = ?";
        log.debug("Executing SQL: {}", sql);
        jdbcTemplate.update(sql, sideA, sideB, sideC, sideD, id);
        log.info("Successfully updated quad with ID {}: A={}, B={}, C={}, D={}", id, sideA, sideB, sideC, sideD);
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

        String sql = "INSERT INTO quads (sideA, sideB, sideC, sideD) VALUES (?, ?, ?, ?)";
        log.debug("Executing POST SQL: {}", sql);
        log.debug("POST Values: {}, {}, {}, {}", sideA, sideB, sideC, sideD);
        jdbcTemplate.update(sql, sideA, sideB, sideC, sideD);
        log.info("Successfully inserted quad: A={}, B={}, C={}, D={}", sideA, sideB, sideC, sideD);
    }

    /**
     * Deletes all quadrilaterals from the database.
     */
    @Override
    public void reset() {
        log.debug("Resetting quads table with TRUNCATE");
        jdbcTemplate.execute("TRUNCATE TABLE quads");
    }

    /**
     * Deletes a quadrilateral by its ID.
     * @throws IllegalArgumentException if not found
     */
    @Override
    public void deleteById(Long id) {
        if (!existsById(id)) {
            log.warn("Attempted to delete quad with non-existent ID: {}", id);
            throw new IllegalArgumentException("Quadrilateral with ID " + id + " does not exist.");
        }

        String sql = "DELETE FROM quads WHERE id = ?";
        jdbcTemplate.update(sql, id);
        log.info("Successfully deleted quad with ID {}", id);
    }

    /**
     * Retrieves the last submitted quadrilateral.
     * @return last Quadrilateral or null if none exist
     */
    @Override
    public Quadrilateral getLastSubmittedQuad() {
        String sql = "SELECT id, sideA, sideB, sideC, sideD FROM quads ORDER BY id DESC LIMIT 1";
        log.debug("Executing SQL to retrieve last submitted quad: {}", sql);
        try {
            Quadrilateral quad = jdbcTemplate.queryForObject(sql, mapRowToQuad());
            log.debug("Retrieved last quad: {}", quad);
            return quad;
        } catch (EmptyResultDataAccessException e) {
            log.warn("No quads found in database.");
            return null;
        }
    }

    /**
     * Retrieves all submitted quadrilaterals.
     */
    @Override
    public List<Quadrilateral> getAllSubmittedQuads() {
        String sql = "SELECT id, sideA, sideB, sideC, sideD FROM quads";
        log.debug("Executing SQL: {}", sql);
        List<Quadrilateral> results = jdbcTemplate.query(sql, mapRowToQuad());
        log.debug("Returned {} quads", results.size());
        return results;
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
     */
    @Override
    public double getLargestSideEverSubmitted() {
        String sql = "SELECT GREATEST(MAX(sideA), MAX(sideB), MAX(sideC), MAX(sideD)) AS largest FROM quads";
        log.debug("Executing SQL for largest side: {}", sql);
        double result = jdbcTemplate.queryForObject(sql, Double.class);
        log.debug("Largest side found: {}", result);
        return result;
    }

    // Stubbed InMemory methods (not supported in JDBC implementation)
    @Override
    public void updateSides(double sideA, double sideB, double sideC, double sideD) {
        log.warn("InMemory method called in JdbcImpl - this should not happen!");
        throw new UnsupportedOperationException("This method is not supported in Jdbc implementation");
    }
}