
package com.priyanka.taskflow_backend;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import static org.junit.jupiter.api.Assertions.*;
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

    // Test 10: Invalid priority should return standard error response.
    @Test
    void invalidPriorityReturnsStandardErrorResponse() throws Exception {

        mockMvc.perform(post("/api/tasks")
                .contentType(MediaType.APPLICATION_JSON)
                .content("""
                        {
                            "title": "Test Task",
                            "description": "Testing validation",
                            "category": "Work",
                            "priority": "Urgent"
                        }
                        """))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status").value(400))
                .andExpect(jsonPath("$.message").value("Validation failed"))
                .andExpect(jsonPath("$.validationErrors.priority")
                        .value("Priority must be Low, Medium, or High"));
    }

    // Helper method to extract a numeric value from JSON response.
    private double extractNumber(String json, String key) {

        java.util.regex.Pattern pattern =
                java.util.regex.Pattern.compile(
                        "\"" + key + "\"\\s*:\\s*([0-9]+(?:\\.[0-9]+)?)");

        java.util.regex.Matcher matcher = pattern.matcher(json);

        if (!matcher.find()) {
            throw new AssertionError("Missing JSON field: " + key);
        }

        return Double.parseDouble(matcher.group(1));
    }

    // Helper method to create a task for statistics testing.
    private Long createStatisticsTestTask(
            String title, boolean completed) throws Exception {

        String jsonRequest = """
                {
                    "title": "%s",
                    "category": "Testing",
                    "priority": "Medium",
                    "completed": %s
                }
                """.formatted(title, completed);

        MvcResult result = mockMvc.perform(post("/api/tasks")
                .contentType(MediaType.APPLICATION_JSON)
                .content(jsonRequest))
                .andExpect(status().isCreated())
                .andReturn();

        String response = result.getResponse().getContentAsString();

        return (long) extractNumber(response, "id");
    }

    // Test 11: Statistics should return correct task counts and percentage.
    @Test
    void testTaskStatistics() throws Exception {

        Long completedTaskId = null;
        Long pendingTaskId = null;

        try {
            MvcResult beforeResult = mockMvc.perform(
                    get("/api/tasks/statistics"))
                    .andExpect(status().isOk())
                    .andReturn();

            String beforeJson =
                    beforeResult.getResponse().getContentAsString();

            long beforeTotal =
                    (long) extractNumber(beforeJson, "totalTasks");

            long beforeCompleted =
                    (long) extractNumber(beforeJson, "completedTasks");

            long beforePending =
                    (long) extractNumber(beforeJson, "pendingTasks");

            completedTaskId = createStatisticsTestTask(
                    "Completed Statistics Test", true);

            pendingTaskId = createStatisticsTestTask(
                    "Pending Statistics Test", false);

            MvcResult result = mockMvc.perform(
                    get("/api/tasks/statistics"))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.totalTasks").isNumber())
                    .andExpect(jsonPath("$.completedTasks").isNumber())
                    .andExpect(jsonPath("$.pendingTasks").isNumber())
                    .andExpect(jsonPath("$.completionPercentage").isNumber())
                    .andReturn();

            String response = result.getResponse().getContentAsString();

            long total = (long) extractNumber(response, "totalTasks");

            long completed =
                    (long) extractNumber(response, "completedTasks");

            long pending =
                    (long) extractNumber(response, "pendingTasks");

            double percentage =
                    extractNumber(response, "completionPercentage");

            assertEquals(beforeTotal + 2, total);
            assertEquals(beforeCompleted + 1, completed);
            assertEquals(beforePending + 1, pending);

            assertEquals(total, completed + pending);

            double expectedPercentage =
                    Math.round((completed * 10000.0) / total) / 100.0;

            assertEquals(expectedPercentage, percentage, 0.001);

        } finally {

            if (completedTaskId != null) {
                mockMvc.perform(delete("/api/tasks/" + completedTaskId))
                        .andExpect(status().isNoContent());
            }

            if (pendingTaskId != null) {
                mockMvc.perform(delete("/api/tasks/" + pendingTaskId))
                        .andExpect(status().isNoContent());
            }
        }
    }
}
