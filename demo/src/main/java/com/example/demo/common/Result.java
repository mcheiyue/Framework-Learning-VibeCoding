package com.example.demo.common;

import com.fasterxml.jackson.annotation.JsonProperty;
import org.slf4j.MDC;

import java.io.Serializable;

public class Result<T> implements Serializable {

	private static final long serialVersionUID = 1L;
	private static final String TRACE_ID_KEY = "traceId";

	private int code;
	private String message;
	private T data;
	private long timestamp;
	private String traceId;

	public Result() {
		this.timestamp = System.currentTimeMillis();
		this.traceId = resolveTraceId();
	}

	public Result(int code, String message, T data) {
		this();
		this.code = code;
		this.message = message;
		this.data = data;
	}

	public Result(ResultCode resultCode, T data) {
		this();
		this.code = resultCode.getCode();
		this.message = resultCode.getMessage();
		this.data = data;
	}

	public static <T> Result<T> success() {
		return new Result<>(ResultCode.SUCCESS, null);
	}

	public static <T> Result<T> success(T data) {
		return new Result<>(ResultCode.SUCCESS, data);
	}

	public static <T> Result<T> success(String message, T data) {
		Result<T> result = new Result<>(ResultCode.SUCCESS, data);
		result.setMessage(message);
		return result;
	}

	public static <T> Result<T> error(ResultCode resultCode) {
		return new Result<>(resultCode, null);
	}

	public static <T> Result<T> error(int code, String message) {
		return new Result<>(code, message, null);
	}

	public static <T> Result<T> error(ResultCode resultCode, T data) {
		return new Result<>(resultCode, data);
	}

	public static <T> Result<T> error(String message) {
		return new Result<>(ResultCode.BAD_REQUEST.getCode(), message, null);
	}

	@JsonProperty("success")
	public boolean isSuccess() {
		return this.code == ResultCode.SUCCESS.getCode();
	}

	public int getCode() {
		return code;
	}

	public void setCode(int code) {
		this.code = code;
	}

	public String getMessage() {
		return message;
	}

	public void setMessage(String message) {
		this.message = message;
	}

	public T getData() {
		return data;
	}

	public void setData(T data) {
		this.data = data;
	}

	public long getTimestamp() {
		return timestamp;
	}

	public void setTimestamp(long timestamp) {
		this.timestamp = timestamp;
	}

	public String getTraceId() {
		return traceId == null ? "" : traceId;
	}

	public void setTraceId(String traceId) {
		this.traceId = traceId;
	}

	private static String resolveTraceId() {
		String traceId = MDC.get(TRACE_ID_KEY);
		return traceId == null ? "" : traceId;
	}
}
