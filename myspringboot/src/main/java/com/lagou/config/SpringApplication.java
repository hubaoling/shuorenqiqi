package com.lagou.config;

import org.springframework.web.context.support.AnnotationConfigWebApplicationContext;

public class SpringApplication {

    public static void run(Class<?> primarySource, String... args){

        //1.初始化ioc
        //通过注解的方式初始化Spring的上下文
        AnnotationConfigWebApplicationContext ac = new AnnotationConfigWebApplicationContext();
        //注册spring的配置类（替代传统项目中xml的configuration）
        ac.register(AppConfig.class);
        ac.refresh();
        //2.获取并启动tomcat
        WebServerFactory webServerFactory = ac.getBean(WebServerFactory.class);
        webServerFactory.createServer();
        //3.将DispatcherServlet添加到tomcat上下文ServletContext中去
        //实例化MySpringServletContainerInitializer，并回调类中的onStartup方法。
    }
}
