# OpenClaw HTTP API 接口定义

## 1. 接口概述

本文档定义了提供给 OpenClaw 调用的 HTTP API 接口，包括腾讯会议 SDK 接口和飞书 API 接口，核心功能包括创建会议和获取用户信息等。

## 2. 基础信息

- **服务地址**: `http://localhost:8080`
- **请求方式**: POST（创建会议）、GET（获取用户信息）
- **内容类型**: `application/json`（创建会议）

## 3. 接口列表

### 3.1 创建会议

#### 接口地址
`/api/meetings/create`

#### 请求参数

| 参数名 | 类型 | 必填 | 描述 |
| :--- | :--- | :--- | :--- |
| userId | String | 是 | 创建会议的用户 ID |
| subject | String | 是 | 会议主题 |
| startTime | String | 是 | 会议开始时间（秒级时间戳） |
| endTime | String | 是 | 会议结束时间（秒级时间戳） |

#### 请求示例

```json
{
  "userId": "user123",
  "subject": "测试会议",
  "startTime": "1678345678",
  "endTime": "1678349278"
}
```

#### 响应参数

| 参数名 | 类型 | 描述 |
| :--- | :--- | :--- |
| meetingId | String | 会议 ID |
| meetingCode | String | 会议号 |
| joinUrl | String | 加入会议的 URL |
| startTime | String | 会议开始时间（秒级时间戳） |
| endTime | String | 会议结束时间（秒级时间戳） |
| subject | String | 会议主题 |
| type | Long | 会议类型 |
| instanceid | Long | 实例 ID |

#### 响应示例

```json
{
  "meetingId": "123456789",
  "meetingCode": "987654321",
  "joinUrl": "https://meeting.tencent.com/dm/123456789",
  "startTime": "1678345678",
  "endTime": "1678349278",
  "subject": "测试会议",
  "type": 1,
  "instanceid": 1
}
```

#### 错误响应

| 状态码 | 描述 |
| :--- | :--- |
| 500 | 内部服务器错误，例如："Error creating meeting: [具体错误信息]" |

### 3.2 获取单个用户信息

#### 接口地址
`/api/feishu/users/{user_id}`

#### 请求参数

| 参数名 | 类型 | 必填 | 描述 |
| :--- | :--- | :--- | :--- |
| user_id | String | 是 | 用户 ID，路径参数 |
| user_id_type | String | 否 | 用户 ID 类型，可选值：open_id、union_id、user_id，默认值：open_id |

#### 请求示例

```
GET /api/feishu/users/7be5fg9a?user_id_type=open_id
```

#### 响应参数

| 参数名 | 类型 | 描述 |
| :--- | :--- | :--- |
| user_id | String | 用户 ID |
| name | String | 用户名称 |
| email | String | 用户邮箱 |
| mobile | String | 用户手机号 |
| status | Integer | 用户状态 |
| departments | Array | 用户所属部门 |
| department_path | Array | 部门路径（仅使用 user_access_token 时返回） |

#### 响应示例

```json
{
  "user_id": "7be5fg9a",
  "name": "张三",
  "email": "zhangsan@example.com",
  "mobile": "13800138000",
  "status": 1,
  "departments": ["od-123456"],
  "department_path": [["od-123456", "技术部"]]
}
```

#### 错误响应

| 状态码 | 描述 |
| :--- | :--- |
| 500 | 内部服务器错误，例如："Error getting user info: [具体错误信息]" |

## 4. 配置说明

在使用前，需要在 `application.yml` 文件中配置腾讯会议 SDK 和飞书 API 的相关参数：

### 4.1 腾讯会议 SDK 配置

```yaml
wemeet:
  appId: "your_app_id"  # 替换为实际的企业 ID
  sdkId: "your_sdk_id"  # 替换为实际的应用 ID
  secretId: "your_secret_id"  # 替换为实际的 Secret ID
  secretKey: "your_secret_key"  # 替换为实际的 Secret Key
```

### 4.2 飞书 API 配置

```yaml
feishu:
  appId: "your_feishu_app_id"  # 替换为实际的飞书应用 ID
  appSecret: "your_feishu_app_secret"  # 替换为实际的飞书应用密钥
  baseUrl: "https://open.feishu.cn"  # 飞书 API 基础 URL
```

## 5. 启动服务

### 5.1 方式一：使用 Maven 命令启动

1. 确保已安装 JDK 8 或更高版本
2. 执行 Maven 构建命令：`mvn clean install`
3. 执行启动命令（在项目根目录）：`mvn spring-boot:run -pl wemeet-openapi`
   或（在 wemeet-openapi 目录）：`cd wemeet-openapi && mvn spring-boot:run`
4. 服务默认运行在 `8080` 端口

### 5.2 方式二：使用 JAR 文件启动

1. 确保已安装 JDK 8 或更高版本
2. 执行 Maven 构建命令：`mvn clean package`
3. 运行 JAR 文件：`java -jar wemeet-openapi/target/wemeet-openapi-sdk-v1.0.12.jar`
4. 服务默认运行在 `8080` 端口

### 5.3 方式三：直接运行主类

1. 在 IDE 中找到 `OpenClawApplication.java` 类
2. 右键点击并选择 "Run" 或 "Debug" 运行主方法
3. 服务默认运行在 `8080` 端口

## 6. 调用流程

### 6.1 创建会议流程

1. OpenClaw 发送 POST 请求到 `/api/meetings/create` 接口，包含用户 ID、会议主题、开始时间和结束时间等参数
2. 服务端验证请求参数并调用腾讯会议 SDK 创建会议
3. 服务端返回创建会议的结果，包含会议 ID、会议号、加入链接等信息
4. OpenClaw 解析响应结果并进行后续处理

### 6.2 获取用户信息流程

1. OpenClaw 发送 GET 请求到 `/api/feishu/users/{user_id}` 接口，可选择指定用户 ID 类型
2. 服务端调用飞书 API 获取访问令牌
3. 服务端使用访问令牌调用飞书 API 获取用户信息
4. 服务端返回用户信息，包含用户名称、邮箱、手机号等信息
5. OpenClaw 解析响应结果并进行后续处理

## 7. 注意事项

- 确保配置文件中的腾讯会议 SDK 参数正确无误
- 确保配置文件中的飞书 API 参数正确无误，包括应用 ID 和应用密钥
- 确保飞书应用已获得相应的权限，如获取用户信息的权限
- 确保网络连接正常，能够访问腾讯会议 API 和飞书 API
- 如有错误，请查看服务端日志获取详细信息
- 飞书 API 调用频率有限制，请合理使用接口