package com.example.demo.dto;

import lombok.Data;

import java.io.Serializable;
@Data
public class LogDTO implements Serializable {

	private static final long serialVersionUID = 1L;

	private String description;

	private String operator;

	private String method;

	private String params;

	private String result;

	private Long time;

	private String ip;

	private String url;

	private String httpMethod;

	private Boolean success;

	private String errorMsg;
}
