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
				.andExpect(header().string("Access-Control-Allow-Origin", "https://taskflow-pro-sandy-eta.vercel.app"));
	}

	@Test
	void testCorsPreflightLocalhost() throws Exception {
		mockMvc.perform(options("/api/tasks")
				.header("Origin", "http://localhost:5173")
				.header("Access-Control-Request-Method", "GET"))
				.andExpect(status().isOk())
				.andExpect(header().string("Access-Control-Allow-Origin", "http://localhost:5173"));
	}

	@Test
	void testGetAllTasks() throws Exception {
		mockMvc.perform(get("/api/tasks")
				.header("Origin", "https://taskflow-pro-sandy-eta.vercel.app"))
				.andExpect(status().isOk())
				.andExpect(header().string("Access-Control-Allow-Origin", "https://taskflow-pro-sandy-eta.vercel.app"));
	}

	@Test
	void testCreateUpdateDeleteTask() throws Exception {
		String jsonRequest = "{\"title\":\"Integration Test Task\",\"category\":\"Learning\",\"priority\":\"High\",\"completed\":false}";
		MvcResult result = mockMvc.perform(post("/api/tasks")
				.header("Origin", "https://taskflow-pro-sandy-eta.vercel.app")
				.contentType(MediaType.APPLICATION_JSON)
				.content(jsonRequest))
				.andExpect(status().isCreated())
				.andExpect(header().string("Access-Control-Allow-Origin", "https://taskflow-pro-sandy-eta.vercel.app"))
				.andExpect(jsonPath("$.id").isNotEmpty())
				.andExpect(jsonPath("$.title").value("Integration Test Task"))
				.andExpect(jsonPath("$.category").value("Learning"))
				.andExpect(jsonPath("$.priority").value("High"))
				.andExpect(jsonPath("$.completed").value(false))
				.andReturn();

		String content = result.getResponse().getContentAsString();
		Long id = Long.parseLong(content.replaceAll(".*\"id\":([0-9]+).*", "$1"));

		String updateJson = "{\"title\":\"Updated Task\",\"category\":\"Projects\",\"priority\":\"Low\",\"completed\":true}";
		mockMvc.perform(put("/api/tasks/" + id)
				.header("Origin", "https://taskflow-pro-sandy-eta.vercel.app")
				.contentType(MediaType.APPLICATION_JSON)
				.content(updateJson))
				.andExpect(status().isOk())
				.andExpect(header().string("Access-Control-Allow-Origin", "https://taskflow-pro-sandy-eta.vercel.app"))
				.andExpect(jsonPath("$.completed").value(true))
				.andExpect(jsonPath("$.title").value("Updated Task"))
				.andExpect(jsonPath("$.category").value("Projects"));

		mockMvc.perform(delete("/api/tasks/" + id)
				.header("Origin", "https://taskflow-pro-sandy-eta.vercel.app"))
				.andExpect(status().isNoContent())
				.andExpect(header().string("Access-Control-Allow-Origin", "https://taskflow-pro-sandy-eta.vercel.app"));
	}

}
