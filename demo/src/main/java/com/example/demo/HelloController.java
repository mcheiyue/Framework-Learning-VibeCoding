package com.example.demo;

import com.example.demo.config.AppConfig;
import com.example.demo.config.CustomConfig;
import com.example.demo.service.DataService;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.core.env.Environment;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.LinkedHashMap;
import java.util.Map;

@RestController
public class HelloController {

	private final AppConfig appConfig;
	private final CustomConfig customConfig;
	private final Environment environment;
	private final ObjectProvider<DataService> dataServiceProvider;

	public HelloController(
			AppConfig appConfig,
			CustomConfig customConfig,
			Environment environment,
			ObjectProvider<DataService> dataServiceProvider
	) {
		this.appConfig = appConfig;
		this.customConfig = customConfig;
		this.environment = environment;
		this.dataServiceProvider = dataServiceProvider;
	}

	@GetMapping("/")
	public String home() {
		return "欢迎使用 Spring Boot！当前时间：" + getCurrentTime();
	}

	@GetMapping("/hello")
	public String hello() {
		return "Hello, Spring Boot!";
	}

	@GetMapping("/hello/{name}")
	public String helloName(@PathVariable String name) {
		return "Hello, " + name + "!";
	}

	@GetMapping("/devtools")
	public String devtools() {
		return "DevTools 热部署测试成功！";
	}

	@GetMapping("/config")
	public Map<String, Object> getConfig() {
		Map<String, Object> config = new LinkedHashMap<>();
		config.put("appName", appConfig.getName());
		config.put("version", appConfig.getVersion());
		config.put("author", appConfig.getAuthor());
		config.put("environment", appConfig.getEnvironment());
		config.put("debug", appConfig.isDebug());
		config.put("uploadPath", customConfig.getUploadPath());
		config.put("maxFileSize", customConfig.getMaxFileSize());
		config.put("serverPort", environment.getProperty("server.port"));
		config.put("activeProfile", getActiveProfile());
		return config;
	}

	@GetMapping("/env")
	public String getEnv() {
		return String.format("当前环境：%s，端口：%s，调试模式：%s",
				appConfig.getEnvironment(),
				environment.getProperty("server.port"),
				appConfig.isDebug());
	}

	@GetMapping("/data")
	public String getData() {
		DataService dataService = dataServiceProvider.getIfAvailable();
		if (dataService == null) {
			return "当前环境未配置数据服务";
		}
		return dataService.getData();
	}

	private String getCurrentTime() {
		return LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
	}

	private String getActiveProfile() {
		String activeProfile = environment.getProperty("spring.profiles.active");
		if (activeProfile != null && !activeProfile.isBlank()) {
			return activeProfile;
		}
		String[] activeProfiles = environment.getActiveProfiles();
		if (activeProfiles == null || activeProfiles.length == 0) {
			return null;
		}
		return String.join(",", activeProfiles);
	}
}
