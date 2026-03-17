package com.example.demo.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
@RequestMapping("/pointcut-test")
public class PointcutTestController {

	@GetMapping("/no-params")
	public String noParams() {
		log.info("执行：noParams()。");
		return "OK";
	}

	@GetMapping("/one-param")
	public String oneParam(@RequestParam String name) {
		log.info("执行：oneParam(name={})。", name);
		return "Hello, " + name + "!";
	}

	@GetMapping("/two-params")
	public String twoParams(@RequestParam String name, @RequestParam int age) {
		log.info("执行：twoParams(name={}, age={})。", name, age);
		return "name=" + name + ", age=" + age;
	}

	@PostMapping("/post-method")
	public String postMethod(@RequestBody String body) {
		log.info("执行：postMethod(body={})。", body);
		return "POST: " + body;
	}

	@PutMapping("/put-method")
	public String putMethod(@RequestBody String body) {
		log.info("执行：putMethod(body={})。", body);
		return "PUT: " + body;
	}

	@GetMapping("/get-data")
	public String getData() {
		log.info("执行：getData()。");
		return "data";
	}

	@DeleteMapping("/delete-data")
	public String deleteData() {
		log.info("执行：deleteData()。");
		return "deleted";
	}

	@GetMapping("/count")
	public int count() {
		log.info("执行：count()。");
		return 1;
	}
}
