package org.msse672.geometryapp.controller;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.msse672.geometryapp.model.Quadrilateral;
import org.msse672.geometryapp.service.QuadService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.test.context.TestContextManager;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import java.util.List;
import java.util.Map;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(QuadController.class) // Uncomment this line if you want to test only the QuadController
//@SpringBootTest
@AutoConfigureMockMvc
public class QuadControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Mock
    private QuadService quadService;

    @BeforeEach
    void setup(@Autowired WebApplicationContext context) {
        TestContextManager testContextManager = new TestContextManager(getClass());
        try {
            testContextManager.prepareTestInstance(this);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }

        // Replace beans in context manually
        MockMvcBuilders.webAppContextSetup(context)
                .build();
    }

//    @Test
//    void post_returns400ForInvalidStringInput() throws Exception {
//        mockMvc.perform(post("/quad/type")
//                .param("sideA", "abc")
//                .param("sideB", "5")
//                .param("sideC", "6")
//                .param("sideD", "7"))
//                .andExpect(status().isBadRequest());
//    }
//
//    @Test
//    void post_returns400ForZeroOrNegativeInput() throws Exception {
//        mockMvc.perform(post("/quad/type")
//                .param("sideA", "0")
//                .param("sideB", "5")
//                .param("sideC", "6")
//                .param("sideD", "7"))
//                .andExpect(status().isBadRequest())
//                .andExpect(jsonPath("$.error").value("Invalid Quadrilateral: violates quadrilateral side length rules."));
//    }
//
//    @Test
//    void post_returns400ForInvalidQuadrilateralInequality() throws Exception {
//        mockMvc.perform(post("/quad/type")
//                .param("sideA", "1")
//                .param("sideB", "2")
//                .param("sideC", "3")
//                .param("sideD", "10"))
//                .andExpect(status().isBadRequest())
//                .andExpect(jsonPath("$.error").value("Invalid Quadrilateral: violates quadrilateral side length rules."));
//    }
//
//    @Test
//    void post_returns200ForSquare() throws Exception {
//        mockMvc.perform(post("/quad/type")
//                .param("sideA", "4")
//                .param("sideB", "4")
//                .param("sideC", "4")
//                .param("sideD", "4"))
//                .andExpect(status().isOk())
//                .andExpect(jsonPath("$.type").value("Type of Quadrilateral: Square"));
//    }
//
//    @Test
//    void post_returns200ForRectangle() throws Exception {
//        mockMvc.perform(post("/quad/type")
//                .param("sideA", "6")
//                .param("sideB", "4")
//                .param("sideC", "6")
//                .param("sideD", "4"))
//                .andExpect(status().isOk())
//                .andExpect(jsonPath("$.type").value("Type of Quadrilateral: Rectangle"));
//    }
//
//    @Test
//    void post_returns200ForDecimalInput() throws Exception {
//        mockMvc.perform(post("/quad/type")
//                .param("sideA", "3.5")
//                .param("sideB", "3.5")
//                .param("sideC", "3.5")
//                .param("sideD", "3.5"))
//                .andExpect(status().isOk())
//                .andExpect(jsonPath("$.type").value("Type of Quadrilateral: Square"));
//    }
//
//    @Test
//    void post_returns200ForGenericConvexQuadrilateral() throws Exception {
//        mockMvc.perform(post("/quad/type")
//                .param("sideA", "2")
//                .param("sideB", "3")
//                .param("sideC", "4")
//                .param("sideD", "5"))
//                .andExpect(status().isOk())
//                .andExpect(jsonPath("$.type").value("Valid Quadrilateral (Generic convex quadrilateral)"));
//    }
//
//    @Test
//    void put_returns400WhenNotInitialized() throws Exception {
//        Mockito.when(quadService.isInitialized()).thenReturn(false);
//
//        mockMvc.perform(put("/quad/type")
//                .param("sideA", "2")
//                .param("sideB", "3")
//                .param("sideC", "4")
//                .param("sideD", "5"))
//                .andExpect(status().isBadRequest())
//                .andExpect(jsonPath("$.error").value("Please POST sides first."));
//    }
//
//    @Test
//    void put_returns400ForInvalidStringInput() throws Exception {
//        Mockito.when(quadService.isInitialized()).thenReturn(true);
//
//        mockMvc.perform(put("/quad/type")
//                .param("sideA", "abc")
//                .param("sideB", "5")
//                .param("sideC", "6")
//                .param("sideD", "7"))
//                .andExpect(status().isBadRequest());
//    }
//
//    @Test
//    void put_returns200ForValidSquare() throws Exception {
//        Mockito.when(quadService.isInitialized()).thenReturn(true);
//
//        mockMvc.perform(put("/quad/type")
//                .param("sideA", "5")
//                .param("sideB", "5")
//                .param("sideC", "5")
//                .param("sideD", "5"))
//                .andExpect(status().isOk())
//                .andExpect(jsonPath("$.type").value("Type of Quadrilateral: Square"));
//    }
//
//    @Test
//    void post_invalidQuadrilateral_uninitializesState() throws Exception {
//        // Step 1: Send invalid POST
//        mockMvc.perform(post("/quad/type")
//                .param("sideA", "1")
//                .param("sideB", "2")
//                .param("sideC", "3")
//                .param("sideD", "10"))
//                .andExpect(status().isBadRequest())
//                .andExpect(jsonPath("$.error").exists());
//
//        // Step 2: Try to GET the current quadrilateral
//        mockMvc.perform(get("/quad/type"))
//                .andExpect(status().isBadRequest())
//                .andExpect(jsonPath("$.error").value("Please POST sides first."));
//    }
//
//    @Test
//    void put_invalidQuadrilateral_uninitializesState() throws Exception {
//        // Arrange: Pretend the app is initialized (this is important!)
//        Mockito.when(quadService.isInitialized()).thenReturn(true);
//
//        // Step 1: Send invalid PUT (bad sides that fail validation)
//        mockMvc.perform(put("/quad/type")
//                .param("sideA", "1")
//                .param("sideB", "2")
//                .param("sideC", "3")
//                .param("sideD", "10"))
//                .andExpect(status().isBadRequest())
//                .andExpect(jsonPath("$.error").exists());
//
//        // Step 2: Try to GET the current quadrilateral
//        Mockito.when(quadService.isInitialized()).thenReturn(false); // Because reset() would have been called
//
//        mockMvc.perform(get("/quad/type"))
//                .andExpect(status().isBadRequest())
//                .andExpect(jsonPath("$.error").value("Please POST sides first."));
//    }

    //======== Additional tests for collections edge cases ==========

    @Test
    void getOnlySquares_returns400WhenNotInitialized() throws Exception {
        Mockito.when(quadService.isInitialized()).thenReturn(false);

        mockMvc.perform(get("/quad/history/squares"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error").value("Please POST sides first."));
    }

    @Test
    void getOnlySquares_returnsEmptyMessageWhenNoSquares() throws Exception {
        Mockito.when(quadService.isInitialized()).thenReturn(true);
        Mockito.when(quadService.getOnlySquares()).thenReturn(List.of());

        mockMvc.perform(get("/quad/history/squares"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value("No squares submitted yet."));
    }

    @Test
    void getOnlySquares_returnsListOfSquares() throws Exception {
        Mockito.when(quadService.isInitialized()).thenReturn(true);
        Mockito.when(quadService.getOnlySquares()).thenReturn(List.of(
                new Quadrilateral(4, 4, 4, 4),
                new Quadrilateral(5, 5, 5, 5)
        ));

        mockMvc.perform(get("/quad/history/squares"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].sideA").value(4))
                .andExpect(jsonPath("$[0].sideB").value(4))
                .andExpect(jsonPath("$[0].sideC").value(4))
                .andExpect(jsonPath("$[0].sideD").value(4))
                .andExpect(jsonPath("$[1].sideA").value(5))
                .andExpect(jsonPath("$[1].sideB").value(5))
                .andExpect(jsonPath("$[1].sideC").value(5))
                .andExpect(jsonPath("$[1].sideD").value(5));
    }

    @Test
    void getQuadStats_returns400WhenNotInitialized() throws Exception {
        Mockito.when(quadService.isInitialized()).thenReturn(false);

        mockMvc.perform(get("/quad/history/stats"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error").value("Please POST sides first."));
    }

    @Test
    void getQuadStats_returnsStats() throws Exception {
        Mockito.when(quadService.isInitialized()).thenReturn(true);
        Mockito.when(quadService.countByType()).thenReturn(Map.of(
                "Square", 3L,
                "Rectangle", 2L
        ));

        mockMvc.perform(get("/quad/history/stats"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.Square").value(3))
                .andExpect(jsonPath("$.Rectangle").value(2));
    }

    // ======== Additional tests for REST functionality ==========

    @Test
    void testPostQuad_Success() throws Exception {
        mockMvc.perform(post("/quads")
                        .param("sideA", "4")
                        .param("sideB", "4")
                        .param("sideC", "4")
                        .param("sideD", "4"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.type").value("Square"));
    }

    @Test
    void testGetQuad_ReturnsLastQuad() throws Exception {
        mockMvc.perform(get("/quads"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.sideA").exists());
    }

    @Test
    void testPutQuad_ValidUpdate() throws Exception {
        // Insert one first
        mockMvc.perform(post("/quads")
                .param("sideA", "4")
                .param("sideB", "4")
                .param("sideC", "4")
                .param("sideD", "4"));

        // Assume ID = 1
        mockMvc.perform(put("/type")
                        .param("id", "1")
                        .param("sideA", "6")
                        .param("sideB", "6")
                        .param("sideC", "6")
                        .param("sideD", "6"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.type").value("Square"));
    }

    @Test
    void testDeleteAllQuads() throws Exception {
        mockMvc.perform(delete("/type"))
                .andExpect(status().isOk());
    }

    @Test
    void testDeleteQuadById() throws Exception {
        // Insert quad
        mockMvc.perform(post("/quads")
                .param("sideA", "3")
                .param("sideB", "3")
                .param("sideC", "3")
                .param("sideD", "3"));

        // Delete by ID = 1
        mockMvc.perform(delete("/type/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value("Deleted quad with ID 1"));
    }

}
