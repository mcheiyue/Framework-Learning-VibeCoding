# Week2 周总结：统一响应、全局异常处理与参数校验（W2-3 里程碑）

## 基本信息

- 对应课程文档：`docs/第2周-统一响应异常处理与参数校验.md`
- 工程信息：Spring Boot 3.5.11（见 `demo/pom.xml`），JDK 21+（本机执行使用 `JAVA_HOME=C:/Program Files/Java/jdk-22`）
- 本周提交列表（按计划 W2-1 ~ W2-3）：
  - W2-1：`feat(week2): 引入 validation 并新增 ResultCode/Result/User 基础设施`
  - W2-2：`feat(week2): 添加 UserController 主线接口与全局异常处理`
  - W2-3：`feat(week2): 完成参数校验/自定义校验/分组校验并补 week2 周总结`

## 本周落地范围

- 统一响应：`ResultCode` + `Result<T>`（包含 `success` 字段、timestamp、traceId）
- 全局异常处理：业务异常 `BusinessException` + `GlobalExceptionHandler`
- 参数校验：基础校验注解（@NotBlank/@Email/@Min/@Max/...）+ 自定义校验（@PasswordStrength/@EnumValue）+ 分组校验（Create/Update）+ 快速失败
- 统一验证产物：
  - 单测（MockMvc + Jackson）
  - 证据资产（截图/响应输出/日志）

## 变更清单（按文件/目录）

### demo/

- 新增：`demo/src/main/java/com/example/demo/common/ResultCode.java`
- 新增：`demo/src/main/java/com/example/demo/common/Result.java`
- 新增：`demo/src/main/java/com/example/demo/entity/User.java`
- 新增：`demo/src/main/java/com/example/demo/controller/UserController.java`
- 新增：`demo/src/main/java/com/example/demo/exception/BusinessException.java`
- 新增：`demo/src/main/java/com/example/demo/exception/GlobalExceptionHandler.java`
- 新增：`demo/src/main/java/com/example/demo/config/ValidationConfig.java`
- 新增：`demo/src/main/java/com/example/demo/dto/UserRegisterDTO.java`
- 新增：`demo/src/main/java/com/example/demo/dto/UserUpdateDTO.java`
- 新增：`demo/src/main/java/com/example/demo/enums/Gender.java`
- 新增：`demo/src/main/java/com/example/demo/validation/PasswordStrength.java`
- 新增：`demo/src/main/java/com/example/demo/validation/PasswordStrengthValidator.java`
- 新增：`demo/src/main/java/com/example/demo/validation/EnumValue.java`
- 新增：`demo/src/main/java/com/example/demo/validation/EnumValueValidator.java`
- 新增：`demo/src/main/java/com/example/demo/validation/ValidationGroup.java`
- 新增：`demo/src/test/java/com/example/demo/common/ResultTests.java`
- 新增：`demo/src/test/java/com/example/demo/controller/UserControllerTests.java`

### docs/

- 新增：`docs/labs/weekly/week2.md`
- 新增：`docs/labs/weekly/assets/week2/01-user-get.png`
- 新增：`docs/labs/weekly/assets/week2/02a-user-get.json`
- 新增：`docs/labs/weekly/assets/week2/02-user-list.json`
- 新增：`docs/labs/weekly/assets/week2/03-user-empty.json`
- 新增：`docs/labs/weekly/assets/week2/04-user-fail.json`
- 新增：`docs/labs/weekly/assets/week2/05-business-exception.json`
- 新增：`docs/labs/weekly/assets/week2/06-arithmetic-exception.json`
- 新增：`docs/labs/weekly/assets/week2/07-arithmetic-exception.log`
- 新增：`docs/labs/weekly/assets/week2/08-register-ok.json`
- 新增：`docs/labs/weekly/assets/week2/09-register-invalid.json`
- 新增：`docs/labs/weekly/assets/week2/10-group-create-missing-username.json`
- 新增：`docs/labs/weekly/assets/week2/11-group-update-missing-id.json`
- 新增：`docs/labs/weekly/assets/week2/12-enum-ok.json`
- 新增：`docs/labs/weekly/assets/week2/13-enum-invalid.json`

## 效果验证清单（来自 Week2 文档）

> 说明：
> 1) 课程文档多以 `8080` 为例；但本机 Windows 环境存在 TCP 排除端口范围 **8000-8099**（包含 8080），因此证据采集统一使用 `18080`（运行时参数覆盖端口，不改动/不提交 YAML）。
> 2) 本机 `curl` 可能受 `http_proxy` 影响访问 `localhost`；证据采集命令统一加 `--noproxy "*"`。
> 3) 证据文件均位于 `assets/week2/`。

### 1) 统一响应结构（文档：步骤1~5，约 101-440）

- [ ] `GET http://localhost:18080/user/get` 返回统一结构（含 `code/message/data/success/timestamp/traceId`）
  - 步骤（浏览器截图）：访问 `http://localhost:18080/user/get`
  - 预期：页面为 JSON；存在 `success` 字段且为 `true`
  - 证据：[`assets/week2/01-user-get.png`](./assets/week2/01-user-get.png)

- [ ] `GET http://localhost:18080/user/get` 保存响应 JSON
  - 步骤：`curl --noproxy "*" http://localhost:18080/user/get > assets/week2/02a-user-get.json`
  - 预期：JSON 中 `code=200`、`message=操作成功`
  - 证据：[`assets/week2/02a-user-get.json`](./assets/week2/02a-user-get.json)

- [ ] `GET http://localhost:18080/user/list` 返回列表数据（文档：约 471）
  - 步骤：`curl --noproxy "*" http://localhost:18080/user/list > assets/week2/02-user-list.json`
  - 预期：`data` 为数组
  - 证据：[`assets/week2/02-user-list.json`](./assets/week2/02-user-list.json)

- [ ] `GET http://localhost:18080/user/empty` 返回成功但无 data
  - 步骤：`curl --noproxy "*" http://localhost:18080/user/empty > assets/week2/03-user-empty.json`
  - 预期：`code=200`，且 `data` 字段不存在或为 null（与实现一致即可）
  - 证据：[`assets/week2/03-user-empty.json`](./assets/week2/03-user-empty.json)

- [ ] `GET http://localhost:18080/user/fail` 返回失败码（文档：统一响应测试）
  - 步骤：`curl --noproxy "*" http://localhost:18080/user/fail > assets/week2/04-user-fail.json`
  - 预期：`code=1001`、`success=false`
  - 证据：[`assets/week2/04-user-fail.json`](./assets/week2/04-user-fail.json)

### 2) 全局异常处理（文档：第三部分，约 528-726）

- [ ] 业务异常：`GET http://localhost:18080/user/business-exception` 返回统一 Result（HTTP 200）
  - 文档位置：约 734/776
  - 步骤：`curl --noproxy "*" http://localhost:18080/user/business-exception > assets/week2/05-business-exception.json`
  - 预期：`code=1001`、`success=false`
  - 证据：[`assets/week2/05-business-exception.json`](./assets/week2/05-business-exception.json)

- [ ] 系统异常：`GET http://localhost:18080/user/arithmetic-exception` 返回 HTTP 500 + 统一 Result
  - 文档位置：约 810（异常栈示例）
  - 步骤：`curl --noproxy "*" http://localhost:18080/user/arithmetic-exception > assets/week2/06-arithmetic-exception.json`
  - 预期：HTTP 状态码 500，且响应体为统一结构（`code=500`、`success=false`）
  - 证据：[`assets/week2/06-arithmetic-exception.json`](./assets/week2/06-arithmetic-exception.json)

- [ ] 异常日志证据（算术异常）
  - 步骤：启动应用后触发 `/user/arithmetic-exception`，从运行日志中截取异常信息
  - 预期：日志中包含异常堆栈（或关键异常信息）
  - 证据：[`assets/week2/07-arithmetic-exception.log`](./assets/week2/07-arithmetic-exception.log)

### 3) 参数校验（文档：第四部分，约 816-1007）

- [ ] 注册成功：`POST http://localhost:18080/user/register`
  - 文档位置：步骤3~5（约 896/973/1007）
  - 步骤：按文档示例构造 JSON，调用注册接口
  - 预期：`code=200`、`success=true`、`data=注册成功`
  - 证据：[`assets/week2/08-register-ok.json`](./assets/week2/08-register-ok.json)

- [ ] 注册失败（基础校验）：`POST http://localhost:18080/user/register`（用户名/邮箱/年龄等不合法）
  - 步骤：构造非法参数（如 username 为空、email 非法、age 超范围）
  - 预期：HTTP 400，响应体为统一结构（`code=400`、`success=false`），message 为校验错误信息
  - 证据：[`assets/week2/09-register-invalid.json`](./assets/week2/09-register-invalid.json)

### 4) 自定义校验注解（文档：第五部分，约 1334-1476；枚举示例约 1626-1778）

- [ ] 枚举校验通过：`POST http://localhost:18080/user/test-enum`
  - 文档位置：枚举校验示例（约 1626-1778）
  - 步骤：提交 `gender=MALE|FEMALE|OTHER`
  - 预期：HTTP 200，`success=true`
  - 证据：[`assets/week2/12-enum-ok.json`](./assets/week2/12-enum-ok.json)

- [ ] 枚举校验失败：`POST http://localhost:18080/user/test-enum`（gender=UNKNOWN）
  - 步骤：提交非法枚举值
  - 预期：HTTP 400，message 为自定义错误信息
  - 证据：[`assets/week2/13-enum-invalid.json`](./assets/week2/13-enum-invalid.json)

### 5) 分组校验（文档：第六部分，约 2059-2184）

- [ ] Create 分组：`POST http://localhost:18080/user/create` 缺少 username
  - 文档位置：约 2071/2111/2160/2184
  - 步骤：提交缺少 username 的 JSON
  - 预期：HTTP 400，message 为“用户名不能为空”
  - 证据：[`assets/week2/10-group-create-missing-username.json`](./assets/week2/10-group-create-missing-username.json)

- [ ] Update 分组：`POST http://localhost:18080/user/update` 缺少 id
  - 步骤：提交缺少 id 的 JSON
  - 预期：HTTP 400，message 为“用户ID不能为空”
  - 证据：[`assets/week2/11-group-update-missing-id.json`](./assets/week2/11-group-update-missing-id.json)

## 验证与证据

- 单测（必跑）：
  - 命令：`cd demo && ./mvnw test`
  - 说明：本机默认 `JAVA_HOME` 可能指向 JDK11；执行前需临时设置 `JAVA_HOME` 到 JDK21+（例如 `C:/Program Files/Java/jdk-22`）。
  - 过程输出（不提交，仅追溯）：`.sisyphus/evidence/task-6-week2-validation-tests.txt`

## 与文档差异/适配说明

- 文档示例端口为 8080；本机端口 8080 落在 Windows 排除端口范围 8000-8099 内。
  - 适配：证据采集统一使用 `18080`（运行时参数覆盖端口，不改动/不提交 YAML）。
