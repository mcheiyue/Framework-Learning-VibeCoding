package com.example.demo.validation;

import jakarta.validation.groups.Default;

public interface ValidationGroup extends Default {

	interface Create {
	}

	interface Update {
	}

	interface Delete {
	}
}
