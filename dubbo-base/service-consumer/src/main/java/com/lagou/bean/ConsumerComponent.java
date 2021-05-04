package com.lagou.bean;

import com.lagou.service.HelloService;
import org.apache.dubbo.config.annotation.Reference;
import org.springframework.stereotype.Component;

@Component
public class ConsumerComponent {

    //@Reference(mock = "force:return 1234")
    @Reference
    private HelloService helloService;

    public String methodA() {
        return helloService.methodA();
    }

    public String methodB() {
        return helloService.methodB();
    }

    public String methodC() {
        return helloService.methodC();
    }

}
