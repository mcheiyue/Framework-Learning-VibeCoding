package com.example.demo.dto;

import com.example.demo.validation.ValidationGroup;
import lombok.Data;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

@Data
public class UserUpdateDTO {

	@NotNull(message = "用户ID不能为空", groups = ValidationGroup.Update.class)
	private Long id;

	@NotBlank(message = "用户名不能为空", groups = ValidationGroup.Create.class)
	@Size(min = 3, max = 20, message = "用户名长度必须在3-20个字符之间",
			groups = {ValidationGroup.Create.class, ValidationGroup.Update.class})
	private String username;

	@NotBlank(message = "邮箱不能为空", groups = ValidationGroup.Create.class)
	@Email(message = "邮箱格式不正确", groups = {ValidationGroup.Create.class, ValidationGroup.Update.class})
	private String email;

	@Min(value = 1, message = "年龄必须大于0")
	@Max(value = 120, message = "年龄必须小于120")
	private Integer age;
}
