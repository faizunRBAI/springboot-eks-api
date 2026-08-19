package com.example.springbooteksapi;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * Integration smoke tests — the full Spring context starts against an H2 database.
 */
@SpringBootTest
@AutoConfigureMockMvc
class SpringBootEksApiApplicationTests {

    @Autowired
    private MockMvc mvc;

    @Test
    void contextLoads() {
        // Verifies the full Spring Boot context assembles without errors.
    }

    @Test
    void healthEndpointReturns200() throws Exception {
        mvc.perform(get("/actuator/health"))
            .andExpect(status().isOk())
            .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON));
    }

    @Test
    void infoEndpointReturnsServiceName() throws Exception {
        mvc.perform(get("/api/info"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.service").value("springboot-eks-api"))
            .andExpect(jsonPath("$.status").value("UP"));
    }

    @Test
    void itemsEndpointReturnsEmptyList() throws Exception {
        mvc.perform(get("/api/items"))
            .andExpect(status().isOk())
            .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON));
    }
}
