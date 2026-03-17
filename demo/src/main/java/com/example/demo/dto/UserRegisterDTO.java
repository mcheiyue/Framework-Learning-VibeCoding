package com.example.demo.dto;

import com.example.demo.enums.Gender;
import com.example.demo.validation.EnumValue;
import com.example.demo.validation.PasswordStrength;
import lombok.Data;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Past;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import java.time.LocalDate;

@Data
public class UserRegisterDTO {

	@NotBlank(message = "用户名不能为空")
	@Size(min = 3, max = 20, message = "用户名长度必须在3-20个字符之间")
	@Pattern(regexp = "^[a-zA-Z0-9_]+$", message = "用户名只能包含字母、数字和下划线")
	private String username;

	@NotBlank(message = "密码不能为空")
	@PasswordStrength(min = 8, max = 20, message = "密码必须包含大小写字母和数字，长度8-20位")
	private String password;

	@NotBlank(message = "确认密码不能为空")
	private String confirmPassword;

	@NotBlank(message = "邮箱不能为空")
	@Email(message = "邮箱格式不正确")
	private String email;

	@Pattern(regexp = "^1[3-9]\\d{9}$", message = "手机号格式不正确")
	private String phone;

	@NotNull(message = "年龄不能为空")
	@Min(value = 1, message = "年龄必须大于0")
	@Max(value = 120, message = "年龄必须小于120")
	private Integer age;

	@Past(message = "出生日期必须是过去的日期")
	private LocalDate birthDate;

	@EnumValue(enumClass = Gender.class, message = "性别值必须是 MALE、FEMALE 或 OTHER")
	private String gender;
}
