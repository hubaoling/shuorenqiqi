package com.lagou.service.impl;

import com.lagou.service.HelloService;
import org.apache.dubbo.config.annotation.Service;

import java.util.Random;

@Service
public class HelloServiceImpl implements HelloService {

    @Override
    public String methodA() {
        Random random = new Random();
        //随机休眠0-100ms
        int num = random.nextInt(100);
        try {
            Thread.sleep(num);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        return "methodA被调用";
    }

    @Override
    public String methodB() {
        Random random = new Random();
        int num = random.nextInt(100);
        try {
            Thread.sleep(num);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        return "methodB被调用";
    }

    @Override
    public String methodC() {
        Random random = new Random();
        int num = random.nextInt(100);
        try {
            Thread.sleep(num);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        return "methodC被调用";
    }
}
