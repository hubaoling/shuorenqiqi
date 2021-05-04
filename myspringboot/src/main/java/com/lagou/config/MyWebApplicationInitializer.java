package com.lagou.config;


import org.springframework.web.WebApplicationInitializer;
import org.springframework.web.servlet.DispatcherServlet;

import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.ServletRegistration;

public class MyWebApplicationInitializer extends MyServlet implements WebApplicationInitializer {
    @Override
    public void onStartup(ServletContext servletContext) throws ServletException {
        // 将DispatcherServlet添加到tomcat上下文中去
        DispatcherServlet dispatcherServlet = super.getDispatcherServlet();
        ServletRegistration.Dynamic registration = servletContext.addServlet("MyDispatcherServlet",
                dispatcherServlet);
        if(registration != null){
            registration.setLoadOnStartup(1);
            registration.addMapping("/");
        }

    }
}
