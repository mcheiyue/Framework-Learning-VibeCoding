# Week3 周总结：日志切面与跨域配置（W3-3 里程碑）

## 基本信息

- 对应课程文档：`docs/第3周-日志管理与跨域配置.md`
- 工程信息：Spring Boot 3.5.11（见 `demo/pom.xml`），JDK 21+（本机执行使用 `JAVA_HOME=C:/Program Files/Java/jdk-22`）
- 本周提交列表（按计划 W3-1 ~ W3-3）：
  - W3-1：`feat(week3): 引入 AOP 依赖并落地 @Log/LogAspect/IpUtils/LogDTO`
  - W3-2：`feat(week3): 添加 Pointcut 练习 Controller/Aspect`
  - W3-3：`feat(week3): 添加全局 CORS 配置并补 week3 周总结`

## 本周落地范围

- AOP 操作日志：`@Log` 注解 + `LogAspect`（成功/异常日志）
- Pointcut 练习：`PointcutTestController` + `PointcutPracticeAspect`（“✅ 练习X”日志）
- 跨域（CORS）：全局配置 `WebMvcConfig` + 前端测试页（5173）

## 变更清单（按文件/目录）

### demo/

- `demo/pom.xml`（AOP 依赖）
- `demo/src/main/java/com/example/demo/annotation/Log.java`
- `demo/src/main/java/com/example/demo/dto/LogDTO.java`
- `demo/src/main/java/com/example/demo/utils/IpUtils.java`
- `demo/src/main/java/com/example/demo/aspect/LogAspect.java`
- `demo/src/main/java/com/example/demo/controller/UserController.java`（增加 @Log + /user/exception）
- `demo/src/main/java/com/example/demo/controller/PointcutTestController.java`
- `demo/src/main/java/com/example/demo/aspect/PointcutPracticeAspect.java`
- `demo/src/main/java/com/example/demo/config/WebMvcConfig.java`
- `demo/src/main/resources/static/test.html`

### docs/

- `docs/labs/weekly/week3.md`
- `docs/labs/weekly/assets/week3/**`

## 效果验证清单（来自 Week3 文档）

> 说明：
> 1) 课程文档多以 `8080` 为例；但本机 Windows 环境存在 TCP 排除端口范围 **8000-8099**（包含 8080），因此证据采集统一使用 `18080`（运行时参数覆盖端口，不改动/不提交 YAML）。
> 2) 本机 `curl` 可能受 `http_proxy` 影响访问 `localhost`；证据采集命令统一加 `--noproxy "*"`。

### 1) AOP 日志切面（@Log + LogAspect）

- [ ] 正常请求：`GET http://localhost:18080/user/get`
  - 预期：返回统一响应 JSON；控制台出现 `操作日志: LogDTO(...)`，其中包含 url=/user/get、httpMethod=GET、success=true
  - 证据：`assets/week3/01-logaspect-user-get.log`

- [ ] 带参请求：`POST http://localhost:18080/user/register`
  - 预期：返回注册成功；控制台出现 `操作日志: LogDTO(...)`（参数中密码字段已脱敏）
  - 证据：`assets/week3/02-logaspect-user-register.log`

- [ ] 异常请求：`GET http://localhost:18080/user/exception`
  - 预期：控制台出现 `操作异常: LogDTO(...)`（success=false），并打印异常栈
  - 证据：`assets/week3/03-logaspect-user-exception.log`

### 2) Pointcut 练习（观察“✅ 练习X”日志）

- [ ] 无参：`GET http://localhost:18080/pointcut-test/no-params`
  - 预期：控制台出现多条 `✅ 练习...`，并出现 controller 自身日志“执行：noParams()。”
  - 证据：`assets/week3/04-pointcut-no-params.log`

- [ ] 单参：`GET http://localhost:18080/pointcut-test/one-param?name=张三`
  - 预期：包含 `✅ 练习4...`
  - 证据：`assets/week3/05-pointcut-one-param.log`

- [ ] 双参：`GET http://localhost:18080/pointcut-test/two-params?name=张三&age=20`
  - 预期：包含 `✅ 练习5...`
  - 证据：`assets/week3/06-pointcut-two-params.log`

### 3) 跨域（CORS）验证（5173 真跨域）

- [ ] 预检（OPTIONS）：模拟浏览器预检，返回包含 CORS 相关响应头
  - 证据：`assets/week3/07-cors-preflight.txt`

- [ ] 实际跨域请求：在 `http://localhost:5173/test.html` 点击按钮后成功获取后端数据
  - 预期：页面显示成功状态码与响应体；Network/Response Headers 存在 `Access-Control-Allow-Origin: http://localhost:5173`
  - 证据：`assets/week3/08-cors-page-success.png`

## 验证与证据

- 单测（必跑）：
  - 命令：`cd demo && ./mvnw test`
  - 过程输出（不提交，仅追溯）：`.sisyphus/evidence/task-9-week3-cors-precommit-check.txt`
