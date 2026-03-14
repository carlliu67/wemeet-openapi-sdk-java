---
name: tencent-meeting
description: 调用本地腾讯会议服务 API 创建会议。用于当用户要求创建视频会议、会议房间时使用。接口：POST http://localhost:8080/api/meetings/create，参数：userId(用户ID)、subject(会议主题)、startTime(开始时间戳秒)、endTime(结束时间戳秒)
---

# 腾讯会议 - 本地 API 调用

## 基础信息

- **服务地址**: `http://localhost:8080`
- **请求方式**: POST
- **Content-Type**: `application/json`

## 接口：创建会议

### 地址

`/api/meetings/create`

### 请求参数

| 参数名 | 类型 | 必填 | 描述 |
|--------|------|------|------|
| userId | String | 是 | 创建会议的用户 ID（需使用 user_id，可通过 open_id 转换，见下方说明） |
| subject | String | 是 | 会议主题 |
| startTime | String | 是 | 会议开始时间（秒级时间戳） |
| endTime | String | 是 | 会议结束时间（秒级时间戳） |

## 用户 ID 说明

**userId 需要使用飞书 user_id（非 open_id）。**

调用流程：
1. 先调用获取用户信息接口 `/api/feishu/users/{open_id}?user_id_type=open_id`
2. 从响应中获取 `user_id`
3. 用 `user_id` 调用创建会议接口

### 转换步骤

```bash
# 1. 获取用户信息（将 open_id 转换为 user_id）
curl -X GET "http://localhost:8080/api/feishu/users/ou_5dfbde98850844154c91372c96308407?user_id_type=open_id"

# 响应示例：
# {"user_id": "uid_xxxxx", "name": "刘启", ...}

# 2. 使用返回的 user_id 创建会议
curl -X POST http://localhost:8080/api/meetings/create \
  -H "Content-Type: application/json" \
  -d '{
    "userId": "uid_xxxxx",
    "subject": "项目评审",
    "startTime": "1234567890",
    "endTime": "1234571490"
  }'
```

### 当前用户信息

- open_id: `ou_5dfbde98850844154c91372c96308407`（刘启）
- user_id: 通过上述接口获取

### 调用方式

使用 `exec` 工具执行 curl 命令：

```bash
curl -X POST http://localhost:8080/api/meetings/create \
  -H "Content-Type: application/json" \
  -d '{
    "userId": "<当前用户飞书ID>",
    "subject": "<会议主题>",
    "startTime": "<开始时间戳>",
    "endTime": "<结束时间戳>"
  }'
```

### 响应参数

| 参数名 | 类型 | 描述 |
|--------|------|------|
| meetingId | String | 会议 ID |
| meetingCode | String | 会议号 |
| joinUrl | String | 加入会议的 URL |
| startTime | String | 会议开始时间 |
| endTime | String | 会议结束时间 |
| subject | String | 会议主题 |
| type | Long | 会议类型 |
| instanceid | Long | 实例 ID |

### 成功响应示例

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

### 错误响应

| 状态码 | 描述 |
|--------|------|
| 500 | 内部服务器错误，如："Error creating meeting: [具体错误信息]" |

## 时间戳转换

如果用户提供了日期时间，需要转换为秒级时间戳：

```bash
# 示例：2026-03-15 14:00:00 转换为时间戳
date -d "2026-03-15 14:00:00" +%s
```

## 使用示例

**用户说**: "创建一个主题为'项目评审'的会议，今天下午3点开始，4点结束"

**处理步骤**:
1. 计算开始和结束时间戳
2. 询问或确定 userId
3. 执行 curl 请求
4. 解析返回结果，提取 meetingId、meetingCode、joinUrl 告知用户

**示例命令**:

```bash
# 获取时间戳
START_TS=$(date -d "2026-03-10 15:00:00" +%s)
END_TS=$(date -d "2026-03-10 16:00:00" +%s)
# 使用飞书 open_id
USER_ID="ou_5dfbde98850844154c91372c96308407"

curl -X POST http://localhost:8080/api/meetings/create \
  -H "Content-Type: application/json" \
  -d "{
    \"userId\": \"$USER_ID\",
    \"subject\": \"项目评审\",
    \"startTime\": \"$START_TS\",
    \"endTime\": \"$END_TS\"
  }"
```

## 注意事项

1. 确保本地服务 `http://localhost:8080` 已启动
2. startTime 必须大于当前时间
3. endTime 必须大于 startTime
4. 时间戳为**字符串类型**（如 "1678345678"）
5. userId 需要从用户处获取或使用默认值
