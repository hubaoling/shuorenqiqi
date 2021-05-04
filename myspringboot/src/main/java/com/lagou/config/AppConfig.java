package com.lagou.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.DispatcherServlet;

@Configuration
@ComponentScan("com.lagou")
public class AppConfig {

    //通过@Bean注解将DispatcherServlet注册到Ioc容器
    @Bean
    public DispatcherServlet getDispatcherServlet(){
        DispatcherServlet dispatcherServlet = new DispatcherServlet();
        return dispatcherServlet;
    }
}
