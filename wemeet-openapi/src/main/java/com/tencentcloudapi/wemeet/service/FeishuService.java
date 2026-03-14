package com.tencentcloudapi.wemeet.service;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.tencentcloudapi.wemeet.config.FeishuConfig;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.RequestEntity;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.net.URI;
import java.util.HashMap;
import java.util.Map;

@Service
public class FeishuService {
    private static final Logger logger = LoggerFactory.getLogger(FeishuService.class);
    private final FeishuConfig feishuConfig;
    private final RestTemplate restTemplate;
    private String accessToken;

    @Autowired
    public FeishuService(FeishuConfig feishuConfig) {
        this.feishuConfig = feishuConfig;
        this.restTemplate = new RestTemplate();
    }

    // 获取访问令牌
    public String getAccessToken() throws Exception {
        if (accessToken == null) {
            String url = feishuConfig.getBaseUrl() + "/open-apis/auth/v3/tenant_access_token/internal/";
            Map<String, String> params = new HashMap<>();
            params.put("app_id", feishuConfig.getAppId());
            params.put("app_secret", feishuConfig.getAppSecret());

            HttpHeaders headers = new HttpHeaders();
            headers.add("Content-Type", "application/json; charset=utf-8");

            logger.info("Feishu API Request: POST {}", url);
            logger.info("Request Body: {}", JSON.toJSONString(params));

            RequestEntity<Map<String, String>> requestEntity = new RequestEntity<>(params, headers, HttpMethod.POST, URI.create(url));
            ResponseEntity<String> responseEntity = restTemplate.exchange(requestEntity, String.class);

            logger.info("Feishu API Response: {}", responseEntity.getBody());

            JSONObject response = JSON.parseObject(responseEntity.getBody());
            if (response.getInteger("code") == 0) {
                accessToken = response.getString("tenant_access_token");
            } else {
                throw new Exception("Failed to get access token: " + response.getString("msg"));
            }
        }
        return accessToken;
    }

    // 获取单个用户信息
    public JSONObject getUserInfo(String userId, String userIdType) throws Exception {
        String url = feishuConfig.getBaseUrl() + "/open-apis/contact/v3/users/" + userId;
        if (userIdType != null) {
            url += "?user_id_type=" + userIdType;
        }

        HttpHeaders headers = new HttpHeaders();
        headers.add("Content-Type", "application/json; charset=utf-8");
        headers.add("Authorization", "Bearer " + getAccessToken());

        logger.info("Feishu API Request: GET {}", url);
        logger.info("Request Headers: {}", headers);

        RequestEntity<Void> requestEntity = new RequestEntity<>(headers, HttpMethod.GET, URI.create(url));
        ResponseEntity<String> responseEntity = restTemplate.exchange(requestEntity, String.class);

        logger.info("Feishu API Response: {}", responseEntity.getBody());

        JSONObject response = JSON.parseObject(responseEntity.getBody());
        logger.info("Response Body: {}", response);
        if (response.getInteger("code") == 0) {
            return response.getJSONObject("data");
        } else {
            throw new Exception("Failed to get user info: " + response.getString("msg"));
        }
    }
}