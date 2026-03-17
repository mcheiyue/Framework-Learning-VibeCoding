package com.example.demo.common;

public enum ResultCode {

	SUCCESS(200, "操作成功"),

	BAD_REQUEST(400, "请求参数错误"),
	UNAUTHORIZED(401, "未授权"),
	FORBIDDEN(403, "禁止访问"),
	NOT_FOUND(404, "资源不存在"),
	METHOD_NOT_ALLOWED(405, "请求方法不支持"),

	INTERNAL_SERVER_ERROR(500, "服务器内部错误"),
	SERVICE_UNAVAILABLE(503, "服务暂不可用"),

	USER_NOT_FOUND(1001, "用户不存在"),
	USER_ALREADY_EXISTS(1002, "用户已存在"),
	INVALID_PASSWORD(1003, "密码错误"),
	INVALID_EMAIL(1004, "邮箱格式不正确"),
	INVALID_PHONE(1005, "手机号格式不正确");

	private final int code;
	private final String message;

	ResultCode(int code, String message) {
		this.code = code;
		this.message = message;
	}

	public int getCode() {
		return code;
	}

	public String getMessage() {
		return message;
	}
}
