package org.msse672.geometryapp.legacy;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.msse672.geometryapp.model.Quadrilateral;
import org.msse672.geometryapp.service.QuadService;

import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

public class ServiceFactoryTest {

    private QuadService quadService;

    @BeforeEach
    void setUp() {
        String xmlPath = "src/main/resources/services.xml";
        String xsdPath = "src/main/resources/services.xsd";
        ServiceFactory factory = new ServiceFactory(xmlPath, xsdPath);
        quadService = factory.getQuadService();
        quadService.reset();
        System.out.println("\n[SETUP] QuadService initialized and reset.");
    }

    @Test
    void testInitializationAndInsert() {
        System.out.println("[TEST] testInitializationAndInsert");
        assertFalse(quadService.isInitialized(), "Service should be empty after reset");

        quadService.insertQuad(3, 3, 3, 3); // Square
        assertTrue(quadService.isInitialized(), "Service should be initialized after insert");

        List<Quadrilateral> all = quadService.getAllSubmittedQuads();
        System.out.println("Inserted quads: " + all.size());
        assertEquals(1, all.size());
    }

    @Test
    void testGetOnlySquares() {
        System.out.println("[TEST] testGetOnlySquares");
        quadService.insertQuad(2, 2, 2, 2); // Square
        quadService.insertQuad(4, 5, 4, 5); // Not a square

        List<Quadrilateral> squares = quadService.getOnlySquares();
        System.out.println("Square count: " + squares.size());
        assertEquals(1, squares.size());
    }

    @Test
    void testGetLargestSide() {
        System.out.println("[TEST] testGetLargestSide");
        quadService.insertQuad(4, 4, 4, 4); // 4
        quadService.insertQuad(10, 2, 3, 1); // 10

        double max = quadService.getLargestSideEverSubmitted();
        System.out.println("Largest side found: " + max);
        assertEquals(10.0, max);
    }

    @Test
    void testCountByType() {
        System.out.println("[TEST] testCountByType");
        quadService.insertQuad(3, 3, 3, 3); // Square
        quadService.insertQuad(4, 5, 4, 5); // Rectangle or Other

        Map<String, Long> counts = quadService.countByType();
        System.out.println("Type counts: " + counts);
        long total = counts.values().stream().mapToLong(Long::longValue).sum();

        assertEquals(2, total);
        assertTrue(counts.containsKey("Square") || counts.containsKey("Quadrilateral"));
    }

    @Test
    void testDeleteById() {
        System.out.println("[TEST] testDeleteById");
        quadService.insertQuad(2, 2, 2, 2); // ID 1
        quadService.insertQuad(3, 3, 3, 3); // ID 2

        List<Quadrilateral> allBefore = quadService.getAllSubmittedQuads();
        System.out.println("Before delete: " + allBefore.size());

        Quadrilateral last = quadService.getLastSubmittedQuad();
        quadService.deleteById(last.getId());

        List<Quadrilateral> allAfter = quadService.getAllSubmittedQuads();
        System.out.println("After delete: " + allAfter.size());
        assertEquals(1, allAfter.size());
    }
}
