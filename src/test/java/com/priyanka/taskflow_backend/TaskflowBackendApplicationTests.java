
package com.priyanka.taskflow_backend;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
class TaskflowBackendApplicationTests {

    @Autowired
    private MockMvc mockMvc;

    @Test
    void contextLoads() {
    }

    @Test
    void testCorsPreflightVercel() throws Exception {
        mockMvc.perform(options("/api/tasks")
                .header("Origin", "https://taskflow-pro-sandy-eta.vercel.app")
                .header("Access-Control-Request-Method", "POST")
                .header("Access-Control-Request-Headers", "content-type"))
                .andExpect(status().isOk())
                .andExpect(header().string(
                        "Access-Control-Allow-Origin",
                        "https://taskflow-pro-sandy-eta.vercel.app"));
    }

    @Test
    void testCorsPreflightLocalhost() throws Exception {
        mockMvc.perform(options("/api/tasks")
                .header("Origin", "http://localhost:5173")
                .header("Access-Control-Request-Method", "GET"))
                .andExpect(status().isOk())
                .andExpect(header().string(
                        "Access-Control-Allow-Origin",
                        "http://localhost:5173"));
    }

    @Test
    void testGetAllTasks() throws Exception {
        mockMvc.perform(get("/api/tasks")
                .header("Origin", "https://taskflow-pro-sandy-eta.vercel.app"))
                .andExpect(status().isOk())
                .andExpect(header().string(
                        "Access-Control-Allow-Origin",
                        "https://taskflow-pro-sandy-eta.vercel.app"));
    }

    @Test
    void testCreateUpdateDeleteTask() throws Exception {

        String jsonRequest = """
                {
                    "title": "Integration Test Task",
                    "category": "Learning",
                    "priority": "High",
                    "completed": false
                }
                """;

        MvcResult result = mockMvc.perform(post("/api/tasks")
                .header("Origin", "https://taskflow-pro-sandy-eta.vercel.app")
                .contentType(MediaType.APPLICATION_JSON)
                .content(jsonRequest))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").isNotEmpty())
                .andExpect(jsonPath("$.title").value("Integration Test Task"))
                .andExpect(jsonPath("$.category").value("Learning"))
                .andExpect(jsonPath("$.priority").value("High"))
                .andExpect(jsonPath("$.completed").value(false))
                .andReturn();

        String content = result.getResponse().getContentAsString();
        Long id = Long.parseLong(
                content.replaceAll(".*\"id\":([0-9]+).*", "$1"));

        String updateJson = """
                {
                    "title": "Updated Task",
                    "category": "Projects",
                    "priority": "Low",
                    "completed": true
                }
                """;

        mockMvc.perform(put("/api/tasks/" + id)
                .header("Origin", "https://taskflow-pro-sandy-eta.vercel.app")
                .contentType(MediaType.APPLICATION_JSON)
                .content(updateJson))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.completed").value(true))
                .andExpect(jsonPath("$.title").value("Updated Task"))
                .andExpect(jsonPath("$.category").value("Projects"));

        mockMvc.perform(delete("/api/tasks/" + id)
                .header("Origin", "https://taskflow-pro-sandy-eta.vercel.app"))
                .andExpect(status().isNoContent());
    }

    // Test 6: Empty task title should be rejected.
    @Test
    void testCreateTaskWithEmptyTitle() throws Exception {

        String jsonRequest = """
                {
                    "title": "",
                    "category": "Learning",
                    "priority": "High"
                }
                """;

        mockMvc.perform(post("/api/tasks")
                .contentType(MediaType.APPLICATION_JSON)
                .content(jsonRequest))
                .andExpect(status().isBadRequest());
    }

    // Test 7: Invalid priority should be rejected.
    @Test
    void testCreateTaskWithInvalidPriority() throws Exception {

        String jsonRequest = """
                {
                    "title": "Test Task",
                    "priority": "Urgent"
                }
                """;

        mockMvc.perform(post("/api/tasks")
                .contentType(MediaType.APPLICATION_JSON)
                .content(jsonRequest))
                .andExpect(status().isBadRequest());
    }

    // Test 8: Partial update should preserve unchanged fields.
    @Test
    void testPartialTaskUpdate() throws Exception {

        String createJson = """
                {
                    "title": "Original Task",
                    "category": "Work",
                    "priority": "Medium"
                }
                """;

        MvcResult result = mockMvc.perform(post("/api/tasks")
                .contentType(MediaType.APPLICATION_JSON)
                .content(createJson))
                .andExpect(status().isCreated())
                .andReturn();

        String content = result.getResponse().getContentAsString();
        Long id = Long.parseLong(
                content.replaceAll(".*\"id\":([0-9]+).*", "$1"));

        mockMvc.perform(put("/api/tasks/" + id)
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"completed\":true}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.title").value("Original Task"))
                .andExpect(jsonPath("$.category").value("Work"))
                .andExpect(jsonPath("$.completed").value(true));

        mockMvc.perform(delete("/api/tasks/" + id))
                .andExpect(status().isNoContent());
    }

    // Test 9: Updating a non-existing task should return 404.
    @Test
    void testUpdateNonExistingTask() throws Exception {

        mockMvc.perform(put("/api/tasks/999999")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"title\":\"Updated Task\"}"))
                .andExpect(status().isNotFound());
    }
}
