package com.example.demo.validation;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

public class EnumValueValidator implements ConstraintValidator<EnumValue, String> {

	private Class<? extends Enum<?>> enumClass;

	@Override
	public void initialize(EnumValue constraintAnnotation) {
		this.enumClass = constraintAnnotation.enumClass();
	}

	@Override
	public boolean isValid(String value, ConstraintValidatorContext context) {
		if (value == null || value.isEmpty()) {
			return true;
		}

		for (Enum<?> enumConstant : enumClass.getEnumConstants()) {
			if (enumConstant.name().equals(value)) {
				return true;
			}
		}

		return false;
	}
}
