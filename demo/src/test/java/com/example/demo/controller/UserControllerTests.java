package com.example.demo.controller;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
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

	@Test
	void register_shouldReturnSuccess() throws Exception {
		mockMvc.perform(post("/user/register")
						.contentType(MediaType.APPLICATION_JSON)
						.content("{\"username\":\"zhangsan\",\"password\":\"Password123\",\"confirmPassword\":\"Password123\",\"email\":\"zhangsan@example.com\",\"phone\":\"13800138000\",\"age\":20,\"birthDate\":\"2000-01-01\",\"gender\":\"MALE\"}"))
				.andExpect(status().isOk())
				.andExpect(jsonPath("$.code").value(200))
				.andExpect(jsonPath("$.success").value(true))
				.andExpect(jsonPath("$.data").value("注册成功"));
	}

	@Test
	void register_passwordMismatch_shouldReturnError() throws Exception {
		mockMvc.perform(post("/user/register")
						.contentType(MediaType.APPLICATION_JSON)
						.content("{\"username\":\"zhangsan\",\"password\":\"Password123\",\"confirmPassword\":\"Password456\",\"email\":\"zhangsan@example.com\",\"age\":20}"))
				.andExpect(status().isOk())
				.andExpect(jsonPath("$.code").value(400))
				.andExpect(jsonPath("$.success").value(false))
				.andExpect(jsonPath("$.message").value("两次密码不一致"));
	}

	@Test
	void register_invalid_shouldReturn400() throws Exception {
		mockMvc.perform(post("/user/register")
						.contentType(MediaType.APPLICATION_JSON)
						.content("{\"username\":\"\",\"password\":\"123\",\"confirmPassword\":\"123\",\"email\":\"invalid-email\",\"age\":150}"))
				.andExpect(status().isBadRequest())
				.andExpect(jsonPath("$.code").value(400))
				.andExpect(jsonPath("$.success").value(false))
				.andExpect(jsonPath("$.message").isNotEmpty());
	}

	@Test
	void enum_invalid_shouldReturn400() throws Exception {
		mockMvc.perform(post("/user/test-enum")
						.contentType(MediaType.APPLICATION_JSON)
						.content("{\"username\":\"testuser\",\"password\":\"Password123\",\"confirmPassword\":\"Password123\",\"email\":\"test@example.com\",\"age\":20,\"gender\":\"UNKNOWN\"}"))
				.andExpect(status().isBadRequest())
				.andExpect(jsonPath("$.code").value(400))
				.andExpect(jsonPath("$.success").value(false))
				.andExpect(jsonPath("$.message").value("性别值必须是 MALE、FEMALE 或 OTHER"));
	}

	@Test
	void groupValidation_create_missingUsername_shouldReturn400() throws Exception {
		mockMvc.perform(post("/user/create")
						.contentType(MediaType.APPLICATION_JSON)
						.content("{\"email\":\"test@example.com\",\"age\":20}"))
				.andExpect(status().isBadRequest())
				.andExpect(jsonPath("$.code").value(400))
				.andExpect(jsonPath("$.success").value(false))
				.andExpect(jsonPath("$.message").value("用户名不能为空"));
	}

	@Test
	void groupValidation_update_missingId_shouldReturn400() throws Exception {
		mockMvc.perform(post("/user/update")
						.contentType(MediaType.APPLICATION_JSON)
						.content("{\"username\":\"zhangsan\",\"age\":20}"))
				.andExpect(status().isBadRequest())
				.andExpect(jsonPath("$.code").value(400))
				.andExpect(jsonPath("$.success").value(false))
				.andExpect(jsonPath("$.message").value("用户ID不能为空"));
	}
}
