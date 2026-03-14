package com.tencentcloudapi.wemeet.controller;

import com.alibaba.fastjson.JSON;
import com.tencentcloudapi.wemeet.core.authenticator.AuthenticatorBuilder;
import com.tencentcloudapi.wemeet.core.authenticator.JWTAuthenticator;
import com.tencentcloudapi.wemeet.core.exception.ClientException;
import com.tencentcloudapi.wemeet.core.exception.ServiceException;
import com.tencentcloudapi.wemeet.service.WeMeetService;
import com.tencentcloudapi.wemeet.service.meetings.api.MeetingsApi;
import com.tencentcloudapi.wemeet.service.meetings.model.V1MeetingsPostRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.security.SecureRandom;
import java.math.BigInteger;

@RestController
@RequestMapping("/api/meetings")
public class MeetingController {

    private static final Logger logger = LoggerFactory.getLogger(MeetingController.class);
    private final WeMeetService weMeetService;

    @Autowired
    public MeetingController(WeMeetService weMeetService) {
        this.weMeetService = weMeetService;
    }

    @PostMapping("/create")
    public ResponseEntity<?> createMeeting(@RequestBody MeetingRequest request) {
        MeetingsApi.ApiV1MeetingsPostResponse response = null;
        try {
            logger.info("Received create meeting request: {}", JSON.toJSONString(request));
            
            // 构造请求体
            String endTime = request.getEndTime();
            Long instanceid = 1L;
            String startTime = request.getStartTime();
            String subject = request.getSubject();
            Long type = 0L;
            String userid = request.getUserId();
            
            V1MeetingsPostRequest body = new V1MeetingsPostRequest(endTime, instanceid, startTime, subject, type, userid);
            MeetingsApi.ApiV1MeetingsPostRequest apiRequest = 
                    new MeetingsApi.ApiV1MeetingsPostRequest.Builder().body(body).build();

            // 构造 JWT 鉴权器
            BigInteger nonce = BigInteger.valueOf(Math.abs((new SecureRandom()).nextInt()));
            String timestamp = String.valueOf(System.currentTimeMillis() / 1000L);
            AuthenticatorBuilder<JWTAuthenticator> authenticatorBuilder = 
                    new JWTAuthenticator.Builder().nonce(nonce).timestamp(timestamp);

            // 发送请求
            response = weMeetService.getClient().meetings().v1MeetingsPost(apiRequest, authenticatorBuilder);

            logger.info("Meeting created successfully: {}", JSON.toJSONString(response.getData()));
            return ResponseEntity.ok(response.getData());
        } catch (ServiceException e) {
            logger.error("Error creating meeting: {}", e.getMessage());
            // 构造错误响应，参考腾讯会议 API 错误返回格式
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(e.getApiResp().getRawBody());
        } catch (ClientException e) {
            logger.error("Error creating meeting: {}", e.getMessage());
            // 构造错误响应，参考腾讯会议 API 错误返回格式
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("{\"error_info\": {\"error_code\": 400, \"message\": \"" + e.getMessage() + "\"}}");
        }
    }

    // 请求参数类
    public static class MeetingRequest {
        private String userId;
        private String subject;
        private String startTime;
        private String endTime;

        public String getUserId() {
            return userId;
        }

        public void setUserId(String userId) {
            this.userId = userId;
        }

        public String getSubject() {
            return subject;
        }

        public void setSubject(String subject) {
            this.subject = subject;
        }

        public String getStartTime() {
            return startTime;
        }

        public void setStartTime(String startTime) {
            this.startTime = startTime;
        }

        public String getEndTime() {
            return endTime;
        }

        public void setEndTime(String endTime) {
            this.endTime = endTime;
        }
    }
}