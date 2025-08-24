package org.msse672.geometryapp.model;

import org.springframework.stereotype.Component;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@Component
public class QuadrilateralSet {
    private static final Logger logger = LoggerFactory.getLogger(QuadrilateralSet.class);
    private final List<Quadrilateral> history = new ArrayList<>();

    public QuadrilateralSet() {
        logger.info("QuadrilateralSet created.");
    }

    /**
     * Adds a quadrilateral to the history.
     * If the quadrilateral is null, it will not be added.
     *
     * @param quad the quadrilateral to add
     */
    public void addQuadrilateral(Quadrilateral quad) {
        if (quad != null) {
            history.add(quad);
            logger.info("Quadrilateral added: {}", quad);
        }
    }

    /**
     * Returns an unmodifiable view of all submitted quadrilaterals.
     */
    public List<Quadrilateral> getAll() {
        logger.debug("Retrieving all quadrilaterals. Count: {}", history.size());
        return Collections.unmodifiableList(history);
    }

    /**
     * Returns the last submitted quadrilateral, or null if none exist.
     */
    public Quadrilateral getLast() {
        if (history.isEmpty()) {
            logger.warn("No quadrilaterals found in history.");
            return null;
        }
        Quadrilateral last = history.get(history.size() - 1);
        logger.debug("Returning last submitted quadrilateral: {}", last);
        return last;
    }

    /**
     * Clears all quadrilaterals from the history.
     */
    public void reset() {
        logger.info("Resetting quadrilateral history.");
        history.clear();
    }

    /**
     * Returns the total number of stored quads (for stats or tests).
     */
    public int count() {
        return history.size();
    }
}

