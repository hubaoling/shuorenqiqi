package com.lagou.config;

import org.springframework.beans.BeansException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.web.servlet.DispatcherServlet;

import javax.servlet.ServletContainerInitializer;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.ServletRegistration;
import java.util.Set;


public class MySpringServletContainerInitializer extends MyServlet implements ServletContainerInitializer {

    public void onStartup(Set<Class<?>> set, ServletContext servletContext) throws ServletException {
        MyWebApplicationInitializer myWebApplicationInitializer = new MyWebApplicationInitializer();
        myWebApplicationInitializer.onStartup(servletContext);

    }

}
