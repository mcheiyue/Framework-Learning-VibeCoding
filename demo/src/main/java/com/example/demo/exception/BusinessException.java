package com.example.demo.exception;

import com.example.demo.common.ResultCode;

public class BusinessException extends RuntimeException {

	private final int code;

	public BusinessException(ResultCode resultCode) {
		super(resultCode.getMessage());
		this.code = resultCode.getCode();
	}

	public BusinessException(String message) {
		super(message);
		this.code = ResultCode.BAD_REQUEST.getCode();
	}

	public BusinessException(int code, String message) {
		super(message);
		this.code = code;
	}

	public int getCode() {
		return code;
	}
}
