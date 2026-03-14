package com.tencentcloudapi.wemeet.service;

import com.tencentcloudapi.wemeet.Client;
import com.tencentcloudapi.wemeet.config.WeMeetConfig;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class WeMeetService {
    private final Client client;

    @Autowired
    public WeMeetService(WeMeetConfig config) {
        this.client = new Client.Builder()
                .withAppId(config.getAppId())
                .withSdkId(config.getSdkId())
                .withSecret(config.getSecretId(), config.getSecretKey())
                .build();
    }

    public Client getClient() {
        return client;
    }
}