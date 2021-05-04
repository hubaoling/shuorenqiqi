package com.lagou.demo.controller;

import com.lagou.demo.service.IDemoService;
import com.lagou.edu.mvcframework.annotations.LagouAutowired;
import com.lagou.edu.mvcframework.annotations.LagouController;
import com.lagou.edu.mvcframework.annotations.LagouRequestMapping;
import com.lagou.edu.mvcframework.annotations.Security;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * 由于zhangsan配置加在类上，则可以访问所有方法
 * http://localhost:8080/demo/query?name=zhangsan
 * http://localhost:8080/demo/query1?name=zhangsan
 *
 * query上没有配置 @Security, 则李四不可以访问
 * http://localhost:8080/demo/query?name=lisi
 *
 * query1上面加上李四, 所有李四是可以访问的
 * http://localhost:8080/demo/query1?name=lisi
 */
@Security({"zhangsan"})
@LagouController
@LagouRequestMapping("/demo")
public class DemoController {


    @LagouAutowired
    private IDemoService demoService;


    /**
     * URL: /demo/query?name=lisi
     * @param request
     * @param response
     * @param name
     * @return
     */
    @LagouRequestMapping("/query")
    public String query(HttpServletRequest request, HttpServletResponse response,String name) {
        return demoService.get(name);
    }

    @Security({"lisi"})
    @LagouRequestMapping("/query1")
    public String query1(HttpServletRequest request, HttpServletResponse response,String name) {
        return demoService.get(name);
    }
}
