package com.lagou.demo.controller;

import com.lagou.demo.service.IDemoService;
import com.lagou.edu.mvcframework.annotations.LagouAutowired;
import com.lagou.edu.mvcframework.annotations.LagouController;
import com.lagou.edu.mvcframework.annotations.LagouRequestMapping;
import com.lagou.edu.mvcframework.annotations.Security;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * 配置加在方法上
 * 1)任何用户都无权访问
 * http://localhost:8080/methodDemo/query1?name=zhangsan
 * http://localhost:8080/methodDemo/query1?name=lisi
 *
 * 2)注解上配置单个用户zhangsan
 * 只有zhangsan可以访问，wangwu不可以访问
 * http://localhost:8080/methodDemo/query2?name=zhangsan
 * http://localhost:8080/methodDemo/query2?name=wangwu
 *
 * 3)注解配置两个用户，zhangsan,lisi,则这两个用户都可以访问
 ** http://localhost:8080/methodDemo/query3?name=zhangsan
 *http://localhost:8080/methodDemo/query3?name=lisi
 *
 */
@LagouController
@LagouRequestMapping("/methodDemo")
public class SecurityDemoController {


    @LagouAutowired
    private IDemoService demoService;

    @LagouRequestMapping("/query1")
    public String query1(HttpServletRequest request, HttpServletResponse response,String name) {
        return demoService.get(name);
    }

    @Security({"zhangsan"})
    @LagouRequestMapping("/query2")
    public String query2(HttpServletRequest request, HttpServletResponse response,String name) {
        return demoService.get(name);
    }

    @Security({"zhangsan","lisi"})
    @LagouRequestMapping("/query3")
    public String query3(HttpServletRequest request, HttpServletResponse response,String name) {
        return demoService.get(name);
    }
}
