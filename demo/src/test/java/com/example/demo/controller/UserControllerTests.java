package com.example.demo.controller;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
class UserControllerTests {

	@Autowired
	private MockMvc mockMvc;

	@Test
	void get_shouldReturnSuccess() throws Exception {
		mockMvc.perform(get("/user/get"))
				.andExpect(status().isOk())
				.andExpect(jsonPath("$.code").value(200))
				.andExpect(jsonPath("$.message").value("操作成功"))
				.andExpect(jsonPath("$.success").value(true))
				.andExpect(jsonPath("$.data.id").value(1))
				.andExpect(jsonPath("$.data.username").value("张三"));
	}

	@Test
	void list_shouldReturnArray() throws Exception {
		mockMvc.perform(get("/user/list"))
				.andExpect(status().isOk())
				.andExpect(jsonPath("$.code").value(200))
				.andExpect(jsonPath("$.success").value(true))
				.andExpect(jsonPath("$.data").isArray());
	}

	@Test
	void empty_shouldReturnNullData() throws Exception {
		mockMvc.perform(get("/user/empty"))
				.andExpect(status().isOk())
				.andExpect(jsonPath("$.code").value(200))
				.andExpect(jsonPath("$.success").value(true))
				.andExpect(jsonPath("$.data").doesNotExist());
	}

	@Test
	void fail_shouldReturnUserNotFound() throws Exception {
		mockMvc.perform(get("/user/fail"))
				.andExpect(status().isOk())
				.andExpect(jsonPath("$.code").value(1001))
				.andExpect(jsonPath("$.success").value(false))
				.andExpect(jsonPath("$.message").value("用户不存在"));
	}

	@Test
	void businessException_shouldReturnUnifiedResult() throws Exception {
		mockMvc.perform(get("/user/business-exception"))
				.andExpect(status().isOk())
				.andExpect(jsonPath("$.code").value(1001))
				.andExpect(jsonPath("$.success").value(false))
				.andExpect(jsonPath("$.message").value("用户不存在"));
	}

	@Test
	void arithmeticException_shouldReturn500() throws Exception {
		mockMvc.perform(get("/user/arithmetic-exception"))
				.andExpect(status().isInternalServerError())
				.andExpect(jsonPath("$.code").value(500))
				.andExpect(jsonPath("$.success").value(false));
	}
}
