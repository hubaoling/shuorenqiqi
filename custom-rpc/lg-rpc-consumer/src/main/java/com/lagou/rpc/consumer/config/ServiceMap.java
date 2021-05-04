package com.lagou.rpc.consumer.config;

import com.lagou.rpc.api.IUserService;
import com.lagou.rpc.consumer.proxy.RpcClientProxy;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Configuration
public class ServiceMap {

    //将服务器端口号和对应的接口（代理类）关系注册到IOC容器
    @Bean
   public Map<Integer, IUserService> getSeviceMap(){
        Map<Integer, IUserService> serviceMap = new HashMap<>();
        String ip = "127.0.0.1";
        IUserService userService = (IUserService) RpcClientProxy.createProxy(IUserService.class,ip,8888);
        serviceMap.put(8888,userService);
        IUserService userService2 = (IUserService) RpcClientProxy.createProxy(IUserService.class,ip,9999);
        serviceMap.put(9999,userService2);
        return serviceMap;
   }
}
