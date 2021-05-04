package com.lagou;

import com.lagou.bean.ConsumerComponent;
import com.lagou.service.HelloService;
import org.apache.dubbo.config.annotation.Reference;
import org.apache.dubbo.config.spring.context.annotation.EnableDubbo;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class AnnotationConsumerMain  {

    @Reference
    private HelloService helloService;

    public static void main(String[] args) throws InterruptedException {
        AnnotationConfigApplicationContext context = new AnnotationConfigApplicationContext(ConsumerConfiguration.class);
        context.start();

        //HelloService helloService = context.getBean(HelloService.class);
        ConsumerComponent helloService = context.getBean(ConsumerComponent.class);

        //创建线程池持续的调用服务
        ExecutorService executorService = Executors.newFixedThreadPool(8);
        int i = 0;
        while (i<10000){
            executorService.submit(new Runnable() {
                @Override
                public void run() {
                    helloService.methodA();
                    helloService.methodB();
                    helloService.methodC();
                }
            });
            i++;
        }
    }


    @Configuration
    @PropertySource("classpath:/dubbo-consumer.properties")
    //@EnableDubbo(scanBasePackages = "com.lagou.bean")
    @ComponentScan("com.lagou.bean")
    @EnableDubbo
    static class ConsumerConfiguration {

        @Reference
        private HelloService helloService;

    }

}
