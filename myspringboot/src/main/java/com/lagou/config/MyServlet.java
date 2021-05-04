package com.lagou.config;

import org.springframework.beans.BeansException;
import org.springframework.beans.factory.BeanFactory;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.DispatcherServlet;

//该类主要用于从ApplicationContext获取DispatcherServlet
@Component
public class MyServlet implements ApplicationContextAware {
    private static ApplicationContext applicationContext;

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        this.applicationContext = applicationContext;
    }

    //从applicationContext获取DispatcherServlet
    public static DispatcherServlet getDispatcherServlet(){
        return applicationContext.getBean(DispatcherServlet.class);
    }
}
