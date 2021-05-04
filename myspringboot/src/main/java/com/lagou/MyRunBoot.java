package com.lagou;

import com.lagou.config.SpringApplication;
import org.springframework.context.annotation.ComponentScan;

//启动类MyRunBoot，通过执行main方法启动服务
public class MyRunBoot {

    public static void main(String[] args) {
        SpringApplication.run(MyRunBoot.class, args);
    }

}
