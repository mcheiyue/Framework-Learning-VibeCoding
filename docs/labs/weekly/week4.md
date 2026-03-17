# Week4 周总结：API 文档生成（SpringDoc + Swagger UI）（W4-2 里程碑）

## 基本信息

- 对应课程文档：`docs/第4周-API文档生成.md`
- 工程信息：Spring Boot 3.5.11（见 `demo/pom.xml`），JDK 21+（本机执行使用 `JAVA_HOME=C:/Program Files/Java/jdk-22`）
- 本周提交列表（按计划 W4-1 ~ W4-2）：
  - W4-1：`feat(week4): 集成 SpringDoc 并新增 OpenApiConfig`
  - W4-2：`feat(week4): 引入内存 UserService 并完善 OpenAPI 注解并补 week4 周总结`

## 本周落地范围

- SpringDoc 集成：生成 OpenAPI JSON（`/v3/api-docs`）与 Swagger UI（`/swagger-ui.html`）
- OpenAPI 基本信息：`OpenApiConfig`（标题/描述/版本/联系信息等）
- Swagger 注解：对 `UserController` 及相关 DTO/枚举增加 `@Tag/@Operation/@Parameter/@Schema` 等
- 运行与取证：通过实际启动应用 + curl/浏览器访问生成证据（截图/JSON）

## 变更清单（按文件/目录）

### demo/

- `demo/pom.xml`（新增 SpringDoc 依赖）
- `demo/src/main/resources/application.yaml`（新增 `springdoc` 配置块）
- `demo/src/main/java/com/example/demo/config/OpenApiConfig.java`
- `demo/src/test/java/com/example/demo/OpenApiDocsTests.java`（MockMvc 验证 `/v3/api-docs`）

> 注：W4-2 里程碑还会引入 `UserService`（内存版）并补齐 Controller/DTO/枚举的 OpenAPI 注解；最终以 W4-2 提交内容为准。

### docs/

- 新增：`docs/labs/weekly/week4.md`
- （W4-2 里程碑提交中新增）`docs/labs/weekly/assets/week4/**`

## 效果验证清单（来自 Week4 文档）

> 说明：
> 1) 课程文档多以 `8080` 为例；但本机 Windows 环境存在 TCP 排除端口范围 **8000-8099**（包含 8080），因此证据采集统一使用 `18080`（运行时参数覆盖端口，不改动/不提交 YAML）。
> 2) 本机 `curl` 可能受 `http_proxy` 影响访问 `localhost`；证据采集命令统一加 `--noproxy "*"`。
> 3) 证据统一存放在 `assets/week4/`。

### 1) 依赖与配置生效（文档：第二部分“添加依赖和配置”）

- [ ] 访问 OpenAPI JSON：`GET http://localhost:18080/v3/api-docs` 返回 200 且为 JSON
  - 步骤：`curl --noproxy "*" http://localhost:18080/v3/api-docs > assets/week4/02-v3-api-docs.json`
  - 预期：响应体包含 `openapi` 字段
  - 证据：`assets/week4/02-v3-api-docs.json`

- [ ] 访问 Swagger UI：`GET http://localhost:18080/swagger-ui.html`
  - 步骤：浏览器访问 `http://localhost:18080/swagger-ui.html`（若 302 跳转到 `/swagger-ui/index.html` 属正常）
  - 预期：Swagger UI 页面可正常加载，能看到 API 列表
  - 证据：`assets/week4/01-swagger-ui.png`

### 2) OpenApiConfig 基本信息展示（文档：步骤3“配置基本信息”）

- [ ] Swagger UI 页面显示标题/描述/版本等（以实际渲染为准）
  - 步骤：打开 Swagger UI，查看页面信息区域
  - 预期：能看到 OpenAPI 基本信息（title/description/version）
  - 证据：`assets/week4/01-swagger-ui.png`

### 3) Swagger 注解生效（文档：第三部分“使用 Swagger 注解”）

- [ ] `UserController` 在 Swagger UI 中按 `@Tag` 分组展示
  - 步骤：Swagger UI 左侧/标签区域检查分组
  - 预期：存在“用户管理”等标签
  - 证据：`assets/week4/03-swagger-ui-tag.png`

- [ ] 关键接口显示 `@Operation` 摘要/描述（至少覆盖 get/list/register）
  - 步骤：展开对应接口，查看 summary/description
  - 预期：摘要/描述与代码注解一致
  - 证据：`assets/week4/04-swagger-ui-operation.png`

### 4) Swagger UI 在线调试（文档：步骤4“测试文档访问” + 后续在线调试）

- [ ] 在 Swagger UI 中 Try it out 调用 `GET /user/get` 返回 200
  - 步骤：Swagger UI → 选择接口 → Try it out → Execute
  - 预期：响应码 200，响应体为统一结构
  - 证据：`assets/week4/05-tryout-user-get.png`

## 验证与证据

- 单测（必跑）：
  - 命令：`cd demo && ./mvnw test`
  - 说明：本机默认 `JAVA_HOME` 可能指向 JDK11；执行前需临时设置 `JAVA_HOME` 到 JDK21+（例如 `C:/Program Files/Java/jdk-22`）。
  - 过程输出（不提交，仅追溯）：`.sisyphus/evidence/task-10-week4-springdoc-tests.txt`

- 启动应用（用于取证）：
  - 建议命令（端口适配）：
    - `cd demo && ./mvnw spring-boot:run -Dspring-boot.run.arguments="--server.port=18080 --spring.profiles.active=dev"`

## 与文档差异/适配说明

- 文档以 `application.yml` 为例；本工程使用 `application.yaml`。
  - 说明：`.yml` 与 `.yaml` 仅扩展名差异，语义等价；本仓库约束只保留 `.yaml`，避免同名多格式导致加载歧义。

- 文档示例多以 `http://localhost:8080/swagger-ui.html` 为例；本机需适配到 `18080`。

- Swagger UI 路径：`/swagger-ui.html` 在部分环境下会 302 到 `/swagger-ui/index.html`，属于正常行为。

## 遗留/风险

- Windows 端口排除范围可能导致文档示例端口不可用；取证前需确认端口可绑定，并在周总结中说明适配。
