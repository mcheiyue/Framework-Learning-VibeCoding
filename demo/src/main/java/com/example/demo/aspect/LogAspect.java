package com.example.demo.aspect;

import com.example.demo.annotation.Log;
import com.example.demo.dto.LogDTO;
import com.example.demo.utils.IpUtils;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.springframework.stereotype.Component;
import org.springframework.validation.BindingResult;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;
import org.springframework.web.multipart.MultipartFile;

import java.util.ArrayList;
import java.util.List;

@Slf4j
@Aspect
@Component
public class LogAspect {

	private final ObjectMapper objectMapper;

	public LogAspect(ObjectMapper objectMapper) {
		this.objectMapper = objectMapper;
	}

	@Pointcut("@annotation(com.example.demo.annotation.Log)")
	public void logPointcut() {
	}

	@Around("logPointcut() && @annotation(logAnnotation)")
	public Object around(ProceedingJoinPoint joinPoint, Log logAnnotation) throws Throwable {
		long start = System.currentTimeMillis();

		LogDTO logDTO = new LogDTO();
		logDTO.setDescription(buildDescription(logAnnotation));
		logDTO.setOperator(getOperator());
		logDTO.setMethod(joinPoint.getSignature().toString());
		logDTO.setParams(toJson(maskSensitive(objectMapper.valueToTree(toSafeArgs(joinPoint.getArgs())))));

		HttpServletRequest request = getRequest();
		if (request != null) {
			logDTO.setIp(IpUtils.getIpAddr(request));
			logDTO.setUrl(request.getRequestURI());
			logDTO.setHttpMethod(request.getMethod());
		}

		Throwable error = null;
		try {
			Object result = joinPoint.proceed();
			logDTO.setSuccess(true);
			logDTO.setResult(toJson(maskSensitive(objectMapper.valueToTree(result))));
			return result;
		} catch (Throwable e) {
			error = e;
			logDTO.setSuccess(false);
			logDTO.setErrorMsg(e.getMessage());
			throw e;
		} finally {
			logDTO.setTime(System.currentTimeMillis() - start);
			if (error == null) {
				log.info("操作日志: {}", logDTO);
			} else {
				log.error("操作异常: {}", logDTO, error);
			}
		}
	}

	private String buildDescription(Log logAnnotation) {
		String module = logAnnotation.module();
		String value = logAnnotation.value();
		if (module == null || module.isBlank()) {
			return value;
		}
		if (value == null || value.isBlank()) {
			return module;
		}
		return module + ":" + value;
	}

	private HttpServletRequest getRequest() {
		if (RequestContextHolder.getRequestAttributes() instanceof ServletRequestAttributes attrs) {
			return attrs.getRequest();
		}
		return null;
	}

	private String getOperator() {
		HttpServletRequest request = getRequest();
		if (request == null) {
			return "";
		}
		String operator = request.getRemoteUser();
		return operator == null ? "" : operator;
	}

	private List<Object> toSafeArgs(Object[] args) {
		List<Object> safeArgs = new ArrayList<>();
		if (args == null) {
			return safeArgs;
		}
		for (Object arg : args) {
			if (arg == null) {
				safeArgs.add(null);
				continue;
			}
			if (arg instanceof HttpServletRequest) {
				safeArgs.add("<HttpServletRequest>");
				continue;
			}
			if (arg instanceof BindingResult) {
				safeArgs.add("<BindingResult>");
				continue;
			}
			if (arg instanceof MultipartFile) {
				safeArgs.add("<MultipartFile>");
				continue;
			}
			safeArgs.add(arg);
		}
		return safeArgs;
	}

	private JsonNode maskSensitive(JsonNode node) {
		if (node == null) {
			return null;
		}
		if (node.isObject()) {
			if (node.has("password")) {
				((com.fasterxml.jackson.databind.node.ObjectNode) node).put("password", "***");
			}
			if (node.has("confirmPassword")) {
				((com.fasterxml.jackson.databind.node.ObjectNode) node).put("confirmPassword", "***");
			}
			node.fields().forEachRemaining(entry -> maskSensitive(entry.getValue()));
			return node;
		}
		if (node.isArray()) {
			node.forEach(this::maskSensitive);
			return node;
		}
		return node;
	}

	private String toJson(JsonNode node) {
		if (node == null) {
			return "";
		}
		try {
			String json = objectMapper.writeValueAsString(node);
			return json.length() > 2000 ? json.substring(0, 2000) : json;
		} catch (Exception e) {
			return String.valueOf(node);
		}
	}
}
