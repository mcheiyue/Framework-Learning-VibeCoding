package com.example.demo;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
class HelloControllerTests {

	@Autowired
	private MockMvc mockMvc;

	@Test
	void hello_shouldReturnFixedText() throws Exception {
		mockMvc.perform(get("/hello"))
				.andExpect(status().isOk())
				.andExpect(content().string("Hello, Spring Boot!"));
	}

	@Test
	void devtools_shouldReturnFixedText() throws Exception {
		mockMvc.perform(get("/devtools"))
				.andExpect(status().isOk())
				.andExpect(content().string("DevTools 热部署测试成功！"));
	}

	@Test
	void config_shouldContainActiveProfileAndServerPort() throws Exception {
		mockMvc.perform(get("/config"))
				.andExpect(status().isOk())
				.andExpect(jsonPath("$.activeProfile").isNotEmpty())
				.andExpect(jsonPath("$.serverPort").isNotEmpty());
	}
}
