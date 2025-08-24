package org.msse672.geometryapp.auth.socket;

import org.junit.jupiter.api.Test;
import org.msse672.geometryapp.TriangleMiddlewareApplication;
import org.msse672.geometryapp.auth.config.AuthSocketProperties;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import org.springframework.beans.factory.annotation.Autowired;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * Verifies that /quad/** is gated by the socket-auth filter.
 */
@SpringBootTest(classes = { TriangleMiddlewareApplication.class, SocketAuthFilterTest.TestController.class })
@AutoConfigureMockMvc
@ActiveProfiles("test")
class SocketAuthFilterTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private AuthSocketProperties props;

    /** Minimal controller under /quad/** so we can assert 200 when auth passes. */
    @RestController
    static class TestController {
        @GetMapping("/quad/test-filter-ping")
        public String ping() { return "ok"; }
    }

    @Test
    void socketServerShouldStart() {
        assertTrue(props.isEnabled());
        assertEquals(9191, props.getPort());
    }


    @Test
    void missingHeadersYields401() throws Exception {
        mockMvc.perform(get("/quad/test-filter-ping"))
                .andExpect(status().isUnauthorized())
                .andExpect(content().string(org.hamcrest.Matchers.containsString("Missing credentials")));
    }

    @Test
    void badCredentialsYields401() throws Exception {
        mockMvc.perform(get("/quad/test-filter-ping")
                        .header(props.getHeaderUsername(), "admin")
                        .header(props.getHeaderPassword(), "wrong"))
                .andExpect(status().isUnauthorized())
                .andExpect(content().string(org.hamcrest.Matchers.containsString("Unauthorized")));
    }

    @Test
    void goodCredentialsYields200() throws Exception {
        mockMvc.perform(get("/quad/test-filter-ping")
                        .header(props.getHeaderUsername(), "admin")
                        .header(props.getHeaderPassword(), "password123")
                        .accept(MediaType.TEXT_PLAIN))
                .andExpect(status().isOk())
                .andExpect(content().string("ok"));
    }
}
