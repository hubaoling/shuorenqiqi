package com.lagou.rpc.consumer.controller;

import com.lagou.rpc.api.IUserService;
import com.lagou.rpc.consumer.config.RequestCount;
import com.lagou.rpc.consumer.proxy.RpcClientProxy;
import com.lagou.rpc.pojo.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Map;

@RestController
public class UserControlller {
    @Autowired
   private Map<Integer, IUserService> serviceMap;

    @RequestMapping("/user/{id}")
    public String getUserInfo(@PathVariable int id){
        String returnStr = "";
        ++RequestCount.count;//请求次数加1
        //负载均衡轮询算法：请求次数 %  服务端数(2个) = 服务器端口号的奇数偶数
        int num = RequestCount.count % 2;

        for(Map.Entry<Integer, IUserService> enrty: serviceMap.entrySet()){
            int port = enrty.getKey();
            if(port%2 == num){
                IUserService userService = enrty.getValue();
                User user = userService.getById(id);
                System.out.println(user);
                returnStr = "请求的服务器端口号："+port+"==="+user.toString();
            }
        }

        return returnStr;
    }

}
