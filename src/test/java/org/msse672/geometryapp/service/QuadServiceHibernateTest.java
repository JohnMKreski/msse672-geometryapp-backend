package org.msse672.geometryapp.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.msse672.geometryapp.model.Quadrilateral;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@ActiveProfiles("test")
public class QuadServiceHibernateTest {

    @Autowired
    @Qualifier("hibernate")
    private QuadService quadService;

    @BeforeEach
    void setUp() {
        quadService.reset(); // Ensure clean state before each test
    }

    @Test
    void testInsertQuadAndRetrieve() {
        quadService.insertQuad(2, 2, 2, 2);
        List<Quadrilateral> quads = quadService.getAllSubmittedQuads();
        assertFalse(quads.isEmpty());
    }

    @Test
    void testGetByIdReturnsCorrectQuad() {
        quadService.insertQuad(3, 3, 3, 3);
        Long id = quadService.getAllSubmittedQuads().get(0).getId();
        Quadrilateral retrieved = quadService.getById(id);
        assertNotNull(retrieved);
        assertEquals(3.0, retrieved.getSideA());
    }

    @Test
    void testUpdateQuadById() {
        quadService.insertQuad(4, 4, 4, 4);
        Long id = quadService.getAllSubmittedQuads().get(0).getId();
        quadService.updateQuadById(id, 6, 6, 6, 6);
        Quadrilateral updated = quadService.getById(id);
        assertEquals(6.0, updated.getSideA());
    }

    @Test
    void testDeleteById() {
        quadService.insertQuad(2, 2, 2, 2);
        Long id = quadService.getAllSubmittedQuads().get(0).getId();
        quadService.deleteById(id);
        assertThrows(IllegalArgumentException.class, () -> quadService.getById(id));
    }

    @Test
    void testGetLastSubmittedQuad() {
        quadService.insertQuad(5, 5, 5, 5);
        quadService.insertQuad(6, 6, 6, 6);
        Quadrilateral last = quadService.getLastSubmittedQuad();
        assertNotNull(last);
        assertEquals(6.0, last.getSideA());
    }

    @Test
    void testGetOnlySquares() {
        quadService.insertQuad(4, 4, 4, 4); // square
        quadService.insertQuad(2, 3, 2, 3); // not square
        List<Quadrilateral> squares = quadService.getOnlySquares();
        assertEquals(1, squares.size());
        assertTrue(squares.get(0).getType().contains("Square"));
    }

    @Test
    void testCountByType() {
        quadService.insertQuad(3, 3, 3, 3); // square
        quadService.insertQuad(1, 2, 1, 2); // rectangle
        Map<String, Long> counts = quadService.countByType();
        assertEquals(1, counts.get("Square"));
        assertEquals(1, counts.get("Rectangle"));
    }

    @Test
    void testGetLargestSideEverSubmitted() {
        quadService.insertQuad(1, 2, 3, 4);
        quadService.insertQuad(10, 9, 8, 7);
        double largest = quadService.getLargestSideEverSubmitted();
        assertEquals(10.0, largest);
    }

    @Test
    void testInvalidInsertThrowsException() {
        assertThrows(IllegalArgumentException.class, () -> {
            quadService.insertQuad(1, 2, 3, 100); // Invalid by business rule
        });
    }

    @Test
    void testDeleteAllResetsDatabase() {
        quadService.insertQuad(3, 3, 3, 3);
        quadService.reset();
        assertTrue(quadService.getAllSubmittedQuads().isEmpty());
    }
}
