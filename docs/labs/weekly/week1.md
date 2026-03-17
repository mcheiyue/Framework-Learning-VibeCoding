# Week1 周总结：DevTools 热部署与多环境配置（W1-3 里程碑）

## 基本信息

- 对应课程文档：`docs/第1周-DevTools与多环境配置.md`
- 工程信息：Spring Boot 3.5.11（见 `demo/pom.xml`），JDK 21+（本机执行使用 `JAVA_HOME=C:/Program Files/Java/jdk-22`）
- 本周提交列表（按计划 W1-1 ~ W1-3）：
  - W1-1：`feat(week1): 增加多环境 application-*.yaml 与 DevTools 配置`
  - W1-2：`feat(week1): 添加 HelloController/配置读取/Profile DataService`
  - W1-3：`test(week1): 增加 HelloController MockMvc 测试并补 week1 周总结`

## 本周落地范围

- 多环境配置：`application.yaml` + `application-dev.yaml` / `application-test.yaml` / `application-prod.yaml`
- DevTools：启用 restart / livereload（以配置方式验证）
- Controller：`HelloController`（`/`、`/hello`、`/hello/{name}`、`/devtools`、`/config`、`/env`、`/data`）
- Profile 条件化 Bean：`DataService` + `DevDataService(@Profile("dev"))` + `ProdDataService(@Profile("prod"))`
- Week1 单测：MockMvc 覆盖核心端点（见下方变更与验证）

## 变更清单（按文件）

### demo/

- 新增：`demo/src/test/java/com/example/demo/HelloControllerTests.java`

### docs/

- 新增：`docs/labs/weekly/week1.md`
- 新增：`docs/labs/weekly/assets/week1/01-home.png`
- 新增：`docs/labs/weekly/assets/week1/02-hello.txt`
- 新增：`docs/labs/weekly/assets/week1/03-hello-zhangsan.txt`
- 新增：`docs/labs/weekly/assets/week1/04-devtools.png`
- 新增：`docs/labs/weekly/assets/week1/05-config-dev.json`
- 新增：`docs/labs/weekly/assets/week1/06-env-dev.txt`
- 新增：`docs/labs/weekly/assets/week1/07-data-dev.txt`
- 新增：`docs/labs/weekly/assets/week1/08-data-test.txt`
- 新增：`docs/labs/weekly/assets/week1/09-data-prod.txt`
- 新增：`docs/labs/weekly/assets/week1/10-config-test.json`
- 新增：`docs/labs/weekly/assets/week1/11-config-prod.json`
- 新增：`docs/labs/weekly/assets/week1/12-devtools-restart.log`

## 接口与行为

- `GET /hello`：返回固定文本 `Hello, Spring Boot!`
- `GET /devtools`：返回固定文本 `DevTools 热部署测试成功！`
- `GET /config`：返回配置 JSON（包含 `activeProfile` 与 `serverPort` 等字段）
- `GET /env`：返回字符串，包含“当前环境/端口/调试模式”
- `GET /data`：根据 profile 返回不同数据（dev 返回开发环境数据；prod 返回生产环境数据；test 无 DataService 时返回兜底提示）

## 效果验证清单（来自 Week1 文档）

> 说明：课程文档以 `8080/8081/8082` 为例；但本机 Windows 环境存在 TCP 排除端口范围 **8000-8099**（包含 8080/8081/8082），导致这些端口无法被应用监听。
> 因此本周“证据采集”统一改用不在排除范围内的端口：**dev=18080、test=18081、prod=18082**；代码与配置本身不改动（仅运行时通过参数覆盖端口）。
> 证据文件均位于 `assets/week1/`。

### 1) 基础接口（文档：第1周 270-279）

- [ ] `GET http://localhost:18080/` 返回“欢迎使用 Spring Boot”且包含“当前时间”
  - 步骤：浏览器访问 `http://localhost:18080/`
  - 预期：页面显示欢迎语与时间
  - 证据：[`assets/week1/01-home.png`](./assets/week1/01-home.png)

- [ ] `GET http://localhost:18080/hello` 返回 `Hello, Spring Boot!`
  - 步骤：`curl --noproxy "*" http://localhost:18080/hello > assets/week1/02-hello.txt`
  - 预期：响应体为固定文本
  - 证据：[`assets/week1/02-hello.txt`](./assets/week1/02-hello.txt)

- [ ] `GET http://localhost:18080/hello/张三` 返回包含 `Hello, 张三!`
  - 步骤：`curl --noproxy "*" http://localhost:18080/hello/%E5%BC%A0%E4%B8%89 > assets/week1/03-hello-zhangsan.txt`
  - 预期：响应体包含用户名
  - 证据：[`assets/week1/03-hello-zhangsan.txt`](./assets/week1/03-hello-zhangsan.txt)

### 2) DevTools 接口（文档：第1周 358-372）

- [ ] `GET http://localhost:18080/devtools` 返回 `DevTools 热部署测试成功！`
  - 步骤：浏览器访问 `http://localhost:18080/devtools`
  - 预期：页面显示固定文本
  - 证据：[`assets/week1/04-devtools.png`](./assets/week1/04-devtools.png)

### 3) 配置读取（文档：第1周 785-813）

- [ ] `GET http://localhost:18080/config` 返回 JSON 且包含 `serverPort`/`activeProfile`
  - 步骤：`curl --noproxy "*" http://localhost:18080/config > assets/week1/05-config-dev.json`
  - 预期：JSON 中至少包含 `serverPort` 与 `activeProfile` 字段
  - 证据：[`assets/week1/05-config-dev.json`](./assets/week1/05-config-dev.json)

- [ ] `GET http://localhost:18080/env` 返回字符串包含“当前环境/端口/调试模式”
  - 步骤：`curl --noproxy "*" http://localhost:18080/env > assets/week1/06-env-dev.txt`
  - 预期：文本中包含“当前环境：开发环境”“端口：18080”“调试模式：true”等信息
  - 证据：[`assets/week1/06-env-dev.txt`](./assets/week1/06-env-dev.txt)

### 4) Profile + 条件化 Bean（文档：第1周 1092-1156）

- [ ] dev：`GET http://localhost:18080/data` 返回“开发环境数据...”
  - 步骤：`curl --noproxy "*" http://localhost:18080/data > assets/week1/07-data-dev.txt`
  - 预期：返回开发环境数据描述
  - 证据：[`assets/week1/07-data-dev.txt`](./assets/week1/07-data-dev.txt)

- [ ] test：以 test profile 启动后 `GET http://localhost:18081/data` 返回“当前环境未配置数据服务”
  - 步骤：以 `--spring.profiles.active=test --server.port=18081` 启动后执行：`curl --noproxy "*" http://localhost:18081/data > assets/week1/08-data-test.txt`
  - 预期：返回兜底提示
  - 证据：[`assets/week1/08-data-test.txt`](./assets/week1/08-data-test.txt)

- [ ] prod：以 prod profile 启动后 `GET http://localhost:18082/data` 返回“生产环境数据...”
  - 步骤：以 `--spring.profiles.active=prod --server.port=18082` 启动后执行：`curl --noproxy "*" http://localhost:18082/data > assets/week1/09-data-prod.txt`
  - 预期：返回生产环境数据描述
  - 证据：[`assets/week1/09-data-prod.txt`](./assets/week1/09-data-prod.txt)

### 5) Profile 切换验证（文档：第1周 820-839）

- [ ] test：`GET http://localhost:18081/config` 的 `activeProfile=test`
  - 步骤：`curl --noproxy "*" http://localhost:18081/config > assets/week1/10-config-test.json`
  - 预期：JSON 字段 `activeProfile` 为 `test` 且 `serverPort` 为 `18081`
  - 证据：[`assets/week1/10-config-test.json`](./assets/week1/10-config-test.json)

- [ ] prod：`GET http://localhost:18082/config` 的 `activeProfile=prod`
  - 步骤：`curl --noproxy "*" http://localhost:18082/config > assets/week1/11-config-prod.json`
  - 预期：JSON 字段 `activeProfile` 为 `prod` 且 `serverPort` 为 `18082`
  - 证据：[`assets/week1/11-config-prod.json`](./assets/week1/11-config-prod.json)

### 6) DevTools 热部署效果（文档：第1周 326-350）

- [ ] 运行中修改 `demo/src/main/resources/application.yaml`（仅增删一行注释）后，控制台出现 `Restarting due to` 类似日志
  - 步骤：应用运行中，给 `application.yaml` 增加/删除一行注释；从控制台截取包含 `Restarting due to` 的片段
  - 预期：出现热重启相关日志
  - 证据：[`assets/week1/12-devtools-restart.log`](./assets/week1/12-devtools-restart.log)

## 验证与证据

- 单测（必跑）：
  - 命令：`cd demo && ./mvnw test`
  - 说明：本机默认 `JAVA_HOME` 可能指向 JDK11；执行前需临时设置 `JAVA_HOME` 到 JDK21+（例如 `C:/Program Files/Java/jdk-22`）。
  - 过程输出（不提交，仅追溯）：`.sisyphus/evidence/task-3-week1-precommit-check.txt`

## 与文档差异/适配说明

- 课程文档示例多使用 `application-*.yml`；本工程统一使用 `application-*.yaml`。
  - 说明：`.yml` 与 `.yaml` 仅为扩展名差异，语义等价；为避免同名多格式导致加载歧义，本仓库约束只保留 `.yaml`。

- 课程文档以 `8080/8081/8082` 端口演示；本机 Windows 环境存在 TCP 排除端口范围 **8000-8099**（可通过 `netsh interface ipv4 show excludedportrange protocol=tcp` 验证），覆盖了 `8080/8081/8082`。
  - 适配：证据采集改用 `18080/18081/18082`（运行时参数覆盖端口，不改动/不提交 YAML）。

## 遗留/风险

- Windows 存在端口排除/保留范围时，会导致常见示例端口不可绑定；采集证据前需要先确认端口可用性，必要时选择不冲突端口并在周总结中说明。
