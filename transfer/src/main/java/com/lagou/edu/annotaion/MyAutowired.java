package com.lagou.edu.annotaion;
import java.lang.annotation.*;

/**
 * 自定义Autowired注解
 * 使用元注解修饰注解
 * @Target 表明该注解可以应用的Java元素类型，
 *ElementType.FIELD应用于属性（包括枚举中的常量）
 * ElementType.METHOD应用于方法
 * ElementType.CONSTRUCTOR应用于构造函数
 *ElementType.PARAMETER应用于方法的形参
 * ElementType.ANNOTATION_TYPE应用于注解类型
 *
 * @Retention 表明注解的生命周期，RetentionPolicy.RUNTIME，在运行时有效，可以通过反射获取该注解的属性值，
 * 从而做一些运行时的逻辑处理
 *
 */
@Target({ElementType.FIELD, ElementType.METHOD, ElementType.CONSTRUCTOR, ElementType.PARAMETER, ElementType.ANNOTATION_TYPE})
@Retention(RetentionPolicy.RUNTIME)
public @interface MyAutowired {
    boolean required() default true;
}
