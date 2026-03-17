package com.example.demo.controller;

import com.example.demo.common.Result;
import com.example.demo.common.ResultCode;
import com.example.demo.annotation.Log;
import com.example.demo.dto.UserRegisterDTO;
import com.example.demo.dto.UserUpdateDTO;
import com.example.demo.entity.User;
import com.example.demo.exception.BusinessException;
import com.example.demo.service.UserService;
import com.example.demo.validation.ValidationGroup;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import jakarta.validation.Valid;
import java.util.List;

@Slf4j
@RequiredArgsConstructor
@RestController
@RequestMapping("/user")
@Tag(name = "用户管理", description = "用户相关接口")
public class UserController {

	private final UserService userService;

	@GetMapping("/get")
	@Log(value = "查询单个用户", module = "用户管理")
	@Operation(summary = "查询单个用户")
	public Result<User> getUser(
			@Parameter(description = "用户ID，不传默认 1", example = "1")
			@RequestParam(required = false) Long id
	) {
		Long userId = id == null ? 1L : id;
		User user = userService.getById(userId);
		return Result.success(user);
	}

	@GetMapping("/list")
	@Log(value = "查询用户列表", module = "用户管理")
	@Operation(summary = "查询用户列表")
	public Result<List<User>> getUserList() {
		return Result.success(userService.list());
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

	@GetMapping("/exception")
	@Log(value = "测试异常", module = "测试")
	public Result<String> exception() {
		int i = 1 / 0;
		return Result.success(String.valueOf(i));
	}

	@PostMapping("/register")
	@Log(value = "用户注册", module = "用户管理")
	@Operation(summary = "用户注册")
	public Result<String> register(@Valid @RequestBody UserRegisterDTO dto) {
		if (!dto.getPassword().equals(dto.getConfirmPassword())) {
			return Result.error("两次密码不一致");
		}

		userService.register(dto);
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
