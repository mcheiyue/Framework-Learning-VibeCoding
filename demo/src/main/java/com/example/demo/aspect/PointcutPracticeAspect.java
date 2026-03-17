package com.example.demo.aspect;

import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.aspectj.lang.annotation.Pointcut;
import org.springframework.stereotype.Component;

@Slf4j
@Aspect
@Component
public class PointcutPracticeAspect {

	@Pointcut("within(com.example.demo.controller..*)")
	public void controllerPackage() {
	}

	@Pointcut("execution(* com.example.demo.controller.PointcutTestController.*(..))")
	public void pointcutTestControllerMethods() {
	}

	@Pointcut("execution(* *..noParams(..))")
	public void noParamsMethod() {
	}

	@Pointcut("execution(* *..oneParam(..)) && args(name)")
	public void oneParamMethod(String name) {
	}

	@Pointcut("execution(* *..twoParams(..)) && args(name, age)")
	public void twoParamsMethod(String name, int age) {
	}

	@Pointcut("execution(* *..postMethod(..))")
	public void postMethod() {
	}

	@Pointcut("execution(* *..putMethod(..))")
	public void putMethod() {
	}

	@Pointcut("execution(String *..*(..))")
	public void stringReturnType() {
	}

	@Pointcut("execution(* *..get*(..))")
	public void getPrefixMethods() {
	}

	@Before("controllerPackage()")
	public void exercise1() {
		log.info("✅ 练习1：匹配到了 controller 包下的方法");
	}

	@Before("pointcutTestControllerMethods()")
	public void exercise2() {
		log.info("✅ 练习2：匹配到了 PointcutTestController 的方法");
	}

	@Before("noParamsMethod()")
	public void exercise3() {
		log.info("✅ 练习3：匹配到了无参方法 noParams");
	}

	@Before("oneParamMethod(name)")
	public void exercise4(String name) {
		log.info("✅ 练习4：匹配到单参方法 oneParam(name={})", name);
	}

	@Before("twoParamsMethod(name, age)")
	public void exercise5(String name, int age) {
		log.info("✅ 练习5：匹配到双参方法 twoParams(name={}, age={})", name, age);
	}

	@Before("postMethod()")
	public void exercise6() {
		log.info("✅ 练习6：匹配到了 POST 方法 postMethod");
	}

	@Before("putMethod()")
	public void exercise7() {
		log.info("✅ 练习7：匹配到了 PUT 方法 putMethod");
	}

	@Before("stringReturnType() && pointcutTestControllerMethods()")
	public void exercise8() {
		log.info("✅ 练习8：匹配到返回值为 String 的方法");
	}

	@Before("getPrefixMethods() && pointcutTestControllerMethods()")
	public void exercise9() {
		log.info("✅ 练习9：匹配到方法名以 get 开头的方法");
	}
}
