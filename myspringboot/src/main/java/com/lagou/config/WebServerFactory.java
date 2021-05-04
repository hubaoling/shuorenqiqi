package com.lagou.config;

import org.springframework.stereotype.Component;

//应用服务器工厂
@Component
public interface WebServerFactory {
    public void createServer();
}
