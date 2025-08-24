package org.msse672.geometryapp.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.msse672.geometryapp.model.Quadrilateral;
import org.msse672.geometryapp.model.QuadrilateralSet;

import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;


class QuadServiceTest {

    private QuadServiceInMemoryImpl quadService;
    private QuadrilateralSet mockHistory;

    @BeforeEach
    void setUp() {
        mockHistory = mock(QuadrilateralSet.class);
        quadService = new QuadServiceInMemoryImpl(mockHistory);
    }

    //==============================================Initialization Tests ==================================================
//    @Test
//    void isInitialized() {
//    }
//
//    @Test
//    void getSideA() {
//    }
//
//    @Test
//    void getSideB() {
//    }
//
//    @Test
//    void getSideC() {
//    }
//
//    @Test
//    void getSideD() {
//    }
//
//    @Test
//    void updateSides() {
//    }
//
//    @Test
//    void reset() {
//    }
//
//    @Test
//    void getAllSubmittedQuads() {
//    }

    //==============================================Collections Tests ==================================================

    @Test
    void testUpdateSidesAddsQuadrilateral() {
        quadService.updateSides(1, 2, 3, 4);
        verify(mockHistory, times(1)).addQuadrilateral(any(Quadrilateral.class));
        assertTrue(quadService.isInitialized());
    }

    @Test
    void testGetAllSubmittedQuadsReturnsHistory() {
        Quadrilateral quad1 = new Quadrilateral(1, 2, 3, 4);
        Quadrilateral quad2 = new Quadrilateral(5, 6, 7, 8);
        List<Quadrilateral> expected = Arrays.asList(quad1, quad2);

        when(mockHistory.getAll()).thenReturn(expected);

        List<Quadrilateral> actual = quadService.getAllSubmittedQuads();
        assertEquals(expected, actual);
        verify(mockHistory, times(1)).getAll();
    }

    @Test
    void testResetUninitializes() {
        quadService.updateSides(1, 2, 3, 4);
        quadService.reset();
        assertFalse(quadService.isInitialized());
    }
}