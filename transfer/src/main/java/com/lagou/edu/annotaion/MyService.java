package com.lagou.edu.annotaion;
import java.lang.annotation.*;

/**
 * 自定义Service注解
 * 使用元注解修饰注解
 * @Target 表明该注解可以应用的Java元素类型，ElementType.TYPE应用于类、接口（包括注解类型）、枚举
 * @Retention 表明注解的生命周期，RetentionPolicy.RUNTIME，在运行时有效，可以通过反射获取该注解的属性值，
 * 从而做一些运行时的逻辑处理
 *
 */
@Target({ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
public @interface MyService {
    String value() default "";
}
