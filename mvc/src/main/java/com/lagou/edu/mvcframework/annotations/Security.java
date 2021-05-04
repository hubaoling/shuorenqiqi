package com.lagou.edu.mvcframework.annotations;

import java.lang.annotation.*;
import java.security.Policy;

//该注解用于添加在Controller类或者Handler方法上
@Target({ElementType.TYPE,ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface Security {
    //有value属性，接收String数组
    String[] value() default {};
}
