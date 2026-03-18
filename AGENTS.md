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

## 8. 工作流（课程推进闭环）

> 目标：让每次课程推进都能形成“实现 → 验证 → 取证 → 沉淀”，并在用户指定的实验范围结束后交付 `expNN.md`（先审阅）与 `expNN-filled.docx`（独立 Word），同时将新增内容滚动补充到累计总 `exp-total-filled.docx` 中。

### 8.1 核心原则（必须遵守）
- **不修改课程原文档**：`docs/第N周-*.md` 只读引用；所有落地变更在 `demo/`（以及必要的 `docs/labs/**` 产物）完成。
- **小步提交**：每个 weekN/里程碑建议拆成 2~4 个提交；最后一个提交必须包含：周总结 + 证据资产。
- **先验证后留证**：任何对外宣称“完成”的功能，都必须有可复现的步骤/命令 + 落盘证据文件。

### 8.2 周次推进闭环（每个 weekN / 里程碑）
1) **对齐范围**：阅读对应课程文档 `docs/第N周-*.md`，在周总结开头写清：本周落地范围（IN）/明确不做什么（OUT）。
2) **实现**：在 `demo/` 落地（遵循本仓库代码风格与目录约定；修改已有文件保持 Tab 缩进一致）。
3) **必跑单测（门槛）**：
   - 类 Unix：`cd demo && ./mvnw test`
   - Windows：`cd demo && mvnw.cmd test`
4) **启动与手工验证（如需要取证）**：
   - 若课程示例默认端口（8080/8081/8082）在本机不可用（Windows 可能存在 TCP 排除端口范围 8000-8099），取证端口优先用 `18080/18081/18082`。
   - **只用运行参数覆盖端口**，不要为了取证去提交 YAML 里的端口改动。
5) **取证并落盘（强制）**：
   - curl 取证（建议统一加代理规避参数）：
     - `curl --noproxy "*" http://localhost:${PORT}/... > docs/labs/weekly/assets/weekN/NN-xxx.json`
   - 浏览器截图（Swagger UI / 页面 / Network headers 等）：
     - 保存为：`docs/labs/weekly/assets/weekN/NN-xxx.png`
   - 日志/控制台关键片段：
     - 保存为：`docs/labs/weekly/assets/weekN/NN-xxx.log`
6) **沉淀周总结（强制）**：
   - 新增/更新：`docs/labs/weekly/weekN.md`
   - 必须包含“效果验证清单（checkbox）”，且每条都写清：**步骤** / **预期** / **证据链接**。

### 8.3 实验报告交付闭环（每次“规定范围”结束后触发）
> 关键点：实验范围不固定，必须由用户当次指定（例如：Week5~Week8，或 Week6+Week8 等）。

0) **累计总报告初始化（仅第一次需要）**：
   - 若 `docs/labs/report/exp-total-filled.docx` 不存在，则从 `docs/labs/report/exp01-filled.docx` 复制生成。

1) **确定本次实验范围**（用户提供）：
   - 覆盖 week 列表/范围：`[weekA, weekB, ...]` 或 `weekX~weekY`
   - 实验编号：`expNN`
2) **范围内证据齐套检查**：确保范围内每个 week 都已存在：
   - 周总结：`docs/labs/weekly/weekK.md`
   - 证据目录：`docs/labs/weekly/assets/weekK/`
3) **先产出 Markdown（强制先审阅）**：
   - 新增/更新：`docs/labs/report/expNN.md`
   - 按 `docs/实验报告模板_markdown.md` 的结构撰写，并优先引用 `../weekly/assets/weekK/...` 的证据（避免重复拷贝）。
4) **用户审阅门禁**：仅当用户确认 `expNN.md` 通过后，才进入 Word。
5) **再产出独立 Word**：
   - 生成：`docs/labs/report/expNN-filled.docx`
   - 以 `docs/附件5：广州商学院实验报告（模板）.docx` 为模板；**不依赖 pandoc**；必要时可用 `python-docx`（仓库当前无现成脚本）。
6) **滚动补充累计总 docx**：
   - 在 `docs/labs/report/exp-total-filled.docx` 中追加本次实验内容，形成新的累计总报告版本。
   - 约束：历史内容不随意改写；新增内容追加到末尾；必要时更新目录/页码（以模板约束为准）。

### 8.4 证据资产规范（强约束）
- 目录固定：`docs/labs/weekly/assets/weekN/`
- 命名固定：`NN-描述.ext`（允许 `02a-...` 作为补充证据）；ext 仅用 `.png/.json/.txt/.log`
- 周总结中优先使用可点击的相对链接引用证据文件。

### 8.5 环境/取证注意事项（遇到则按此处理）
- **JDK**：执行 `./mvnw -v` / `mvnw.cmd -v` 确认 Maven Wrapper 实际使用 Java 21+，避免被 JDK11 驱动导致构建/测试异常。
- **端口**：若 8080 不可绑定，统一改 18080/18081/18082 取证，并在周总结/报告写明适配原因。
- **curl**：若受 `http_proxy` 影响访问 localhost，统一加 `--noproxy "*"`。
- **CORS 真跨域**：需要浏览器侧证据时，可用 `python -m http.server 5173` 启静态页面配合截图与预检 headers。
- **SpringDoc 取证**：接口文档可通过 `/v3/api-docs` 与 `/swagger-ui.html` 访问，取证时遵循上方端口覆盖策略。

### 8.6 提交节奏（不含 push/PR）
- 建议提交类型（与现有仓库提交风格保持一致）：
  - `feat(weekN): ...`（功能/配置落地）
  - `test(weekN): ...`（补齐/修复测试）
  - `docs(weekN): 增加 weekN 周总结与证据资产`（周末收口提交）
  - `docs(expNN): 增加 expNN 实验报告（md/docx）`
  - `docs(report): 滚动补充累计总实验报告（docx）`

### 8.7 OhMyOpenCode 代理工作模式（短版）
- Prometheus：只产出计划/草稿（`.sisyphus/plans/*`、`.sisyphus/drafts/*`），不直接改业务代码。
- 计划确认后：用 `/start-work` 让 Sisyphus 按计划执行实现与验证。
- 过程证据：可落在 `.sisyphus/evidence/`（通常不提交，用于追溯）。
