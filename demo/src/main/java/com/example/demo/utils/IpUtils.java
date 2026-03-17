package com.example.demo.utils;

import jakarta.servlet.http.HttpServletRequest;

public final class IpUtils {

	private IpUtils() {
	}

	public static String getIpAddr(HttpServletRequest request) {
		if (request == null) {
			return "";
		}

		String ip = getHeaderIp(request, "x-forwarded-for");
		if (ip.isBlank()) {
			ip = getHeaderIp(request, "Proxy-Client-IP");
		}
		if (ip.isBlank()) {
			ip = getHeaderIp(request, "WL-Proxy-Client-IP");
		}
		if (ip.isBlank()) {
			ip = getHeaderIp(request, "HTTP_CLIENT_IP");
		}
		if (ip.isBlank()) {
			ip = getHeaderIp(request, "HTTP_X_FORWARDED_FOR");
		}
		if (ip.isBlank()) {
			ip = getHeaderIp(request, "X-Real-IP");
		}
		if (ip.isBlank()) {
			ip = request.getRemoteAddr();
		}

		int commaIndex = ip.indexOf(',');
		if (commaIndex > 0) {
			ip = ip.substring(0, commaIndex);
		}
		ip = ip.trim();

		if ("0:0:0:0:0:0:0:1".equals(ip)) {
			return "127.0.0.1";
		}
		return ip;
	}

	private static String getHeaderIp(HttpServletRequest request, String header) {
		String ip = request.getHeader(header);
		if (ip == null) {
			return "";
		}
		ip = ip.trim();
		if (ip.isEmpty() || "unknown".equalsIgnoreCase(ip)) {
			return "";
		}
		return ip;
	}
}
