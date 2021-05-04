package com.lagou.config;

import org.apache.catalina.Context;
import org.apache.catalina.LifecycleException;
import org.apache.catalina.startup.Tomcat;
import org.springframework.stereotype.Component;

import javax.servlet.ServletException;

@Component
public class MyTomcat implements WebServerFactory{
    @Override
    public void createServer() {
        //创建一个tomcat服务器
        Tomcat tomcat = new Tomcat();
        tomcat.setPort(8088);
        try {
            Context context = tomcat.addWebapp("/", "D:/IdeaProjects");
            // 禁止重新载入
            context.setReloadable(false);
            tomcat.start();
            //因为 tomcat.start()是非阻塞型的，所以要阻塞一下，不能让服务停止。
            tomcat.getServer().await();
        } catch (ServletException e) {
            e.printStackTrace();
        } catch (LifecycleException e) {
            e.printStackTrace();
        }


    }
}
