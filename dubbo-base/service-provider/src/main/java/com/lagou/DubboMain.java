package com.lagou;


import org.apache.dubbo.config.RegistryConfig;
import org.apache.dubbo.config.spring.context.annotation.EnableDubbo;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;

@Configuration
@EnableDubbo(scanBasePackages = "com.lagou.service.impl")
@PropertySource("classpath:/dubbo-provider.properties")
public class DubboMain {
    public static void main(String[] args) throws  Exception{
        AnnotationConfigApplicationContext context = new AnnotationConfigApplicationContext(DubboMain.class);
        context.start();
        System.in.read();
    }

}
