// QuadServiceIntegrationTest.java
package org.msse672.geometryapp.service;

import org.junit.jupiter.api.Test;
import org.msse672.geometryapp.model.Quadrilateral;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.dao.DataAccessException;
import org.springframework.test.context.ActiveProfiles;

import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@ActiveProfiles("test") // Uses application-test.properties
class QuadServiceIntegrationTest {

    @Autowired
    @Qualifier("jdbc")
    private QuadService quadService;

    @Test
    void testUpdateSidesAndRetrieve() {
        quadService.updateSides(2, 3, 4, 5);
        assertTrue(quadService.isInitialized());

        List<Quadrilateral> quads = quadService.getAllSubmittedQuads();
        quads.forEach(System.out::println);

        assertFalse(quads.isEmpty());
        assertEquals(2, quads.get(0).getSideA());
    }

    @Test
    void testReset() {
        quadService.updateSides(1, 1, 1, 1);
        quadService.reset();
        assertFalse(quadService.isInitialized());
    }

    //TODO: Enable this test once the angled classification logic is implemented
//    @Test
//    void testAllTypeClassifications() {
//        quadService.reset();
//
//        quadService.updateSides(4, 4, 4, 4); // Square
//        quadService.updateSides(4, 2, 4, 2); // Rectangle
//        quadService.updateSides(3, 3, 3, 3); // Rhombus (also square, so won't reach rhombus)
//        quadService.updateSides(5, 3, 5, 3); // Parallelogram
//        quadService.updateSides(6, 6, 3, 3); // Kite
//
//        Map<String, Long> counts = quadService.countByType();
//        counts.forEach((type, count) -> System.out.println(type + ": " + count));
//
//        assertTrue(counts.get("Square") >= 1);
//        assertTrue(counts.get("Rectangle") >= 1);
//        assertTrue(counts.get("Parallelogram") >= 1);
//        assertTrue(counts.get("Kite") >= 1);
//    }


    @Test
    void testGetSidesReturnsLatest() {
        quadService.updateSides(4, 5, 6, 7);
        assertEquals(4, quadService.getSideA());
        assertEquals(5, quadService.getSideB());
        assertEquals(6, quadService.getSideC());
        assertEquals(7, quadService.getSideD());
    }

    @Test
    void testGetOnlySquaresReturnsOnlySquares() {
        quadService.reset();
        quadService.updateSides(3, 3, 3, 3); // square
        quadService.updateSides(2, 3, 2, 3); // not a square
        List<Quadrilateral> squares = quadService.getOnlySquares();
        assertEquals(1, squares.size());
        assertTrue(squares.get(0).getType().contains("Square"));
    }

    @Test
    void testCountByTypeReturnsCorrectCounts() {
        quadService.reset();
        quadService.updateSides(4, 4, 4, 4); // square
        quadService.updateSides(2, 2, 2, 2); // square
        quadService.updateSides(1, 2, 1, 2); // rectangle
        Map<String, Long> counts = quadService.countByType();
        assertEquals(2, counts.get("Square"));
        assertEquals(1, counts.get("Rectangle"));
    }

    @Test
    void testGetLargestSideEverSubmitted() {
        quadService.reset();
        quadService.updateSides(1, 2, 3, 4);
        quadService.updateSides(10, 9, 8, 7);
        double largest = quadService.getLargestSideEverSubmitted();
        assertEquals(10.0, largest);
    }

    @Test
    void testEmptyDatabaseReturnsSafeDefaults() {
        quadService.reset();
        assertFalse(quadService.isInitialized());
        assertEquals(0, quadService.getAllSubmittedQuads().size());
        assertEquals(0, quadService.getOnlySquares().size());
        assertEquals(0, quadService.countByType().size());
    }

    @Test
    void testDuplicateQuadsAreStoredIndependently() {
        quadService.reset();
        quadService.updateSides(2, 2, 2, 2);
        quadService.updateSides(2, 2, 2, 2);
        List<Quadrilateral> quads = quadService.getAllSubmittedQuads();
        assertEquals(2, quads.size());
    }

    @Test
    void testInvalidQuadThrowsException() {
        quadService.reset();

        assertThrows(IllegalArgumentException.class, () -> {
            quadService.updateSides(1, 2, 3, 20); // Invalid by rule
        });
    }

    @Test
    void testZeroAndNegativeSidesThrowException() {
        quadService.reset();

        assertThrows(IllegalArgumentException.class, () -> {
            quadService.updateSides(0, 2, 2, 2);
        });

        assertThrows(IllegalArgumentException.class, () -> {
            quadService.updateSides(-1, 2, 2, 2);
        });

        assertThrows(IllegalArgumentException.class, () -> {
            quadService.updateSides(2, 2, 2, -5);
        });
    }

    @Test
    void testUpdateFailsWithHugeValue() {
        quadService.reset();
        try {
            quadService.updateSides(Double.MAX_VALUE, Double.MAX_VALUE, Double.MAX_VALUE, Double.MAX_VALUE);
        } catch (Exception e) {
            System.out.println("Caught exception: " + e.getMessage());
            assertTrue(e instanceof DataAccessException);
        }
    }

    // =============== New Service Tests ===============

    @Test
    void testInsertQuadAndRetrieve() {
        quadService.insertQuad(2, 2, 2, 2);
        List<Quadrilateral> quads = quadService.getAllSubmittedQuads();
        assertFalse(quads.isEmpty());
    }

    @Test
    void testUpdateQuadById() {
        quadService.insertQuad(3, 3, 3, 3);
        List<Quadrilateral> quads = quadService.getAllSubmittedQuads();
        Long id = quads.get(0).getId();

        quadService.updateQuadById(id, 6, 6, 6, 6);
        Quadrilateral updated = quadService.getById(id);
        assertEquals(6, updated.getSideA());
    }

    @Test
    void testDeleteQuadById() {
        quadService.insertQuad(2, 2, 2, 2);
        Long id = quadService.getAllSubmittedQuads().get(0).getId();
        quadService.deleteById(id);
        assertThrows(Exception.class, () -> quadService.getById(id));
    }


}