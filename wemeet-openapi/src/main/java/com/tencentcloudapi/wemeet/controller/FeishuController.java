package com.tencentcloudapi.wemeet.controller;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.tencentcloudapi.wemeet.service.FeishuService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/feishu")
public class FeishuController {

    private static final Logger logger = LoggerFactory.getLogger(FeishuController.class);
    private final FeishuService feishuService;

    @Autowired
    public FeishuController(FeishuService feishuService) {
        this.feishuService = feishuService;
    }

    @GetMapping("/users/{user_id}")
    public ResponseEntity<?> getUserInfo(@PathVariable("user_id") String userId, @RequestParam(value = "user_id_type", required = false) String userIdType) {
        try {
            logger.info("Received get user info request: userId={}, userIdType={}", userId, userIdType);
            
            JSONObject userInfo = feishuService.getUserInfo(userId, userIdType);
            
            logger.info("Get user info successfully: {}", JSON.toJSONString(userInfo));
            return ResponseEntity.ok(userInfo);
        } catch (Exception e) {
            logger.error("Error getting user info: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error getting user info: " + e.getMessage());
        }
    }
}