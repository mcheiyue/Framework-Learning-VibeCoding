package com.example.demo.validation;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

import java.util.regex.Pattern;

public class PasswordStrengthValidator implements ConstraintValidator<PasswordStrength, String> {

	private int min;
	private int max;

	@Override
	public void initialize(PasswordStrength constraintAnnotation) {
		this.min = constraintAnnotation.min();
		this.max = constraintAnnotation.max();
	}

	@Override
	public boolean isValid(String value, ConstraintValidatorContext context) {
		if (value == null) {
			return true;
		}

		if (value.length() < min || value.length() > max) {
			return false;
		}

		String pattern = "^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d).+$";
		return Pattern.matches(pattern, value);
	}
}
