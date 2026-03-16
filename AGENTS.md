# AGENTS.md（本仓库代理协作指南）
> 目标：让自动化编码代理在此仓库内“按事实行事”，可直接运行命令、遵循现有风格、避免凭空假设。

## 0. 仓库结构与范围
- 仓库根目录不是 Maven 工程；**可运行的 Spring Boot 工程在 `demo/`**。
- 课程/规范示例主要在 `docs/`（Markdown）。
关键路径：
- `demo/pom.xml`：Maven 工程入口（Spring Boot 3.5.11，Java 21）
- `demo/src/main/java/...`：业务源码（当前示例极简）
- `demo/src/test/java/...`：测试源码（JUnit 5）

## 1. 构建 / 运行 / 测试命令（重点：单测）
### 1.1 进入工程目录（从仓库根目录执行）
```bash
cd demo
```
### 1.2 Maven Wrapper（优先使用，避免依赖本机 Maven 安装）
类 Unix（Git Bash/WSL 等）：
```bash
./mvnw -v
./mvnw clean verify
./mvnw clean package
./mvnw clean package -DskipTests
./mvnw spring-boot:run
```
Windows（cmd / PowerShell）：
```bat
mvnw.cmd -v
mvnw.cmd clean verify
mvnw.cmd clean package
mvnw.cmd clean package -DskipTests
mvnw.cmd spring-boot:run
```
### 1.3 运行全部测试
```bash
./mvnw test
```
### 1.4 只跑单个测试类 / 单个测试方法（最常用）
测试类示例：`demo/src/test/java/com/example/demo/DemoApplicationTests.java`
只跑单个测试类：
```bash
./mvnw -Dtest=DemoApplicationTests test
```
只跑单个测试方法：
```bash
./mvnw -Dtest=DemoApplicationTests#contextLoads test
```
按类名通配（测试类增多时常用）：
```bash
./mvnw -Dtest=*Tests test
```
Windows 同理（把 `./mvnw` 换成 `mvnw.cmd`）：
```bat
mvnw.cmd -Dtest=DemoApplicationTests test
mvnw.cmd -Dtest=DemoApplicationTests#contextLoads test
mvnw.cmd -Dtest=*Tests test
```
### 1.5 Lint / Format / 静态检查（现状说明）
- 未发现 Checkstyle/Spotless/PMD/SpotBugs 等 Java 静态检查入口，也无 Node/TS 的 eslint/prettier（仓库无 `package.json`）。
- 不要假设存在相关命令；如需引入，请先新增配置并在变更说明中写清楚。

## 2. 代码风格（以仓库“现有源码/配置”为准）
### 2.1 语言与版本
- Java 版本：**21**（见 `demo/pom.xml` 的 `<java.version>21</java.version>`）
- Spring Boot：`spring-boot-starter-parent` **3.5.11**（见 `demo/pom.xml`）
### 2.2 包结构与放置位置
- 根包：`com.example.demo`（见 `DemoApplication.java` / `DemoApplicationTests.java`）
- 新增代码建议按职责拆分子包，例如：
  - `controller/`、`service/`、`dto/`、`entity/`、`common/`、`exception/`、`config/`、`utils/`、`aspect/`
### 2.3 文件布局与缩进
- Java 文件结构：`package` → 空行 → `import` → 空行 → 注解/类定义（见 `DemoApplication.java`）
- 当前示例源码缩进使用 **Tab**（见 `DemoApplication.java`、`DemoApplicationTests.java`）。
  - 修改已有文件时保持一致，避免 Tab/空格混用导致噪声 diff。
### 2.4 行尾（EOL）
`demo/.gitattributes` 规定：
- `/mvnw` 使用 LF
- `*.cmd` 使用 CRLF
不要改动 wrapper 脚本的行尾规则。
### 2.5 配置文件
- 现有配置文件：`demo/src/main/resources/application.yaml`（YAML；当前缩进为 2 空格）。
- 多环境示例约定：`application-dev.yml` / `application-test.yml` / `application-prod.yml`（见 `docs/第1周-DevTools与多环境配置.md`）。

### 2.6 导入 / 命名 / 类型（通用 Java 约定，新增代码请遵守）
- import：不使用通配符（`import xxx.*`）；删除未使用 import；保持 IDE 自动排序即可。
- 命名：包名全小写；类/枚举 UpperCamelCase；方法/字段 lowerCamelCase；常量 UPPER_SNAKE_CASE。
- 类型：避免原始类型（raw type），集合/Result 必须带泛型参数（例如 `Result<List<User>>`）。

### 2.7 错误处理（落地规则）
- 不要写空的 `catch`；不要吞异常。
- 在业务边界（Controller/Service）用 `BusinessException` 表达可预期错误；其余异常交由全局异常处理统一返回 `Result`。
- 日志优先使用参数化占位符：`log.info("xxx: {}", value)`，避免字符串拼接。

## 3. 代码规范（来自仓库 `docs/` 的“课程约定/推荐实现”）
> 说明：以下模式来自仓库内的教学文档示例代码；当前 `demo/src/main/java` 可能尚未完整实现。
> 当你在本仓库实现相应能力时，优先与这些示例保持一致。

### 3.1 统一响应 / 异常处理 / 校验

- 统一响应：`Result<T>`（包含 `code/message/data/timestamp/traceId`），响应码集中 `ResultCode`（含业务码 1xxx）。
- 异常：业务异常 `BusinessException`；全局异常 `@RestControllerAdvice` + `@ExceptionHandler` 返回统一 `Result`；业务异常 `warn`，系统异常 `error`。
- 校验：DTO 使用 Jakarta Validation；`@Valid @RequestBody` 触发；Query 校验在类上加 `@Validated`；多字段错误用 `"; "` 拼接。

证据：`docs/第2周-统一响应异常处理与参数校验.md`

### 3.2 日志 / AOP / API 文档

- 日志：`@Slf4j`。
- AOP：`@Aspect` + `@Around` 统一记录请求/操作日志；IP 获取参考 `IpUtils` 示例。
- API 文档：SpringDoc + Swagger UI；注解 `@Tag/@Operation/@Parameter/@Schema`。

证据：`docs/第3周-日志管理与跨域配置.md`、`docs/第4周-API文档生成.md`

## 4. 测试约定
- 测试框架：JUnit 5（Jupiter），Spring Boot 集成测试用 `@SpringBootTest`。
- 新增测试放在 `demo/src/test/java`，并尽量与被测类保持相同包路径。
- 优先让测试可被 Surefire 用 `-Dtest=类名#方法名` 精确定位。
证据：`demo/src/test/java/com/example/demo/DemoApplicationTests.java`

## 5. 变更纪律（给代理的硬约束）
- **不要凭空假设**：不存在的脚本/命令/配置不要写进操作步骤。
- **最小必要变更**：避免为了“顺手”重排 import、改缩进、改格式导致无关 diff。
- 引入新依赖必须修改 `demo/pom.xml`，并说明引入目的与替代方案。
- 发生异常时不要吞掉：要么让全局异常处理统一返回，要么抛出并记录必要上下文。

## 6. Cursor / Copilot 规则（仓库扫描结论）
本次扫描未发现以下文件/目录：
- `.cursorrules`
- `.cursor/rules/`
- `.github/copilot-instructions.md`
若后续新增，请以这些规则为最高优先级补充到本文件。

## 7. 证据索引（快速定位）
- 构建入口：`demo/pom.xml`
- Wrapper 行尾：`demo/.gitattributes`
- 主入口：`demo/src/main/java/com/example/demo/DemoApplication.java`
- 测试入口：`demo/src/test/java/com/example/demo/DemoApplicationTests.java`
- YAML 配置：`demo/src/main/resources/application.yaml`
- 课程规范：`docs/第1周-DevTools与多环境配置.md`、`docs/第2周-统一响应异常处理与参数校验.md`、`docs/第3周-日志管理与跨域配置.md`、`docs/第4周-API文档生成.md`
