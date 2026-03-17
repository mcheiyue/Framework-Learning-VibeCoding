package com.example.demo.controller;

import com.example.demo.common.Result;
import com.example.demo.common.ResultCode;
import com.example.demo.dto.UserRegisterDTO;
import com.example.demo.dto.UserUpdateDTO;
import com.example.demo.entity.User;
import com.example.demo.exception.BusinessException;
import com.example.demo.validation.ValidationGroup;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import jakarta.validation.Valid;
import java.util.Arrays;
import java.util.List;

@Slf4j
@RestController
@RequestMapping("/user")
public class UserController {

	@GetMapping("/get")
	public Result<User> getUser() {
		User user = new User();
		user.setId(1L);
		user.setUsername("张三");
		user.setEmail("zhangsan@example.com");
		user.setAge(20);
		return Result.success(user);
	}

	@GetMapping("/list")
	public Result<List<User>> getUserList() {
		User user1 = new User();
		user1.setId(1L);
		user1.setUsername("张三");

		User user2 = new User();
		user2.setId(2L);
		user2.setUsername("李四");

		List<User> users = Arrays.asList(user1, user2);
		return Result.success(users);
	}

	@GetMapping("/empty")
	public Result<String> empty() {
		return Result.success();
	}

	@GetMapping("/fail")
	public Result<String> fail() {
		return Result.error(ResultCode.USER_NOT_FOUND);
	}

	@GetMapping("/custom-error")
	public Result<String> customError() {
		return Result.error("操作失败，请稍后重试");
	}

	@GetMapping("/business-exception")
	public Result<String> businessException() {
		throw new BusinessException(ResultCode.USER_NOT_FOUND);
	}

	@GetMapping("/business-exception-custom")
	public Result<String> businessExceptionCustom() {
		throw new BusinessException("用户名或密码错误");
	}

	@GetMapping("/arithmetic-exception")
	public Result<String> arithmeticException() {
		int i = 1 / 0;
		return Result.success(String.valueOf(i));
	}

	@GetMapping("/null-exception")
	public Result<String> nullException() {
		String str = null;
		str.length();
		return Result.success();
	}

	@GetMapping("/illegal-argument")
	public Result<String> illegalArgument() {
		throw new IllegalArgumentException("参数不合法");
	}

	@PostMapping("/register")
	public Result<String> register(@Valid @RequestBody UserRegisterDTO dto) {
		if (!dto.getPassword().equals(dto.getConfirmPassword())) {
			return Result.error("两次密码不一致");
		}

		log.info("用户注册: {}", dto.getUsername());
		return Result.success("注册成功");
	}

	@PostMapping("/test-enum")
	public Result<String> testEnum(@Valid @RequestBody UserRegisterDTO dto) {
		log.info("性别: {}", dto.getGender());
		return Result.success("测试成功");
	}

	@PostMapping("/create")
	public Result<String> createUser(@Validated(ValidationGroup.Create.class) @RequestBody UserUpdateDTO dto) {
		return Result.success("新增用户成功: " + dto.getUsername());
	}

	@PostMapping("/update")
	public Result<String> updateUser(@Validated(ValidationGroup.Update.class) @RequestBody UserUpdateDTO dto) {
		return Result.success("更新用户成功: ID=" + dto.getId());
	}
}
