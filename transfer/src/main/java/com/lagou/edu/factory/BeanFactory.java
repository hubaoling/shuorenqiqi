package com.lagou.edu.factory;

import com.alibaba.druid.util.StringUtils;
import com.lagou.edu.annotaion.MyAutowired;
import com.lagou.edu.annotaion.MyService;
import com.lagou.edu.annotaion.MyTransactional;
import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.Element;
import org.dom4j.io.SAXReader;
import org.reflections.Reflections;

import java.io.InputStream;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * 工厂类，生产对象（使用反射技术）
 */
public class BeanFactory {

    /**
     * 任务一：扫描包，通过反射技术实例化对象并且存储待用（map集合）
     * 任务二：对外提供获取实例对象的接口（根据id获取）
     */

    private static Map<String, Object> map = new HashMap<>();  // 存储对象


    static {
        try {
            // 任务一：扫描包，通过反射技术实例化对象并且存储待用（map集合）

            //扫描包含com.lagou.edu的url，包括'com.lagou.edu'开头的包路径，使用默认扫描器
            Reflections reflections = new Reflections("com.lagou.edu");
            //获取标记注解MyService对应的类
            Set<Class<?>> typeClass = reflections.getTypesAnnotatedWith(MyService.class);
            //遍历集合，实例化对象
            for (Class<?> aClass : typeClass) {
                //获取实例化对象
                Object o = aClass.newInstance();
                MyService myService = aClass.getAnnotation(MyService.class);

                //判断MyService注解上是否有自定义对象ID
                if (StringUtils.isEmpty(myService.value())) {
                    //无自定义对象id，默认使用类名作为id
                    //获取全限定类名
                    String className = aClass.getName();
                    //分割去掉前面包名部分
                    String[] names = className.split("\\.");
                    map.put(names[names.length - 1], o);
                } else {
                    map.put(myService.value(), o);
                }
            }
            //维护对象之间的依赖关系
            for (Map.Entry<String, Object> entrySet : map.entrySet()) {
                Object object = entrySet.getValue();
                Class clazz = object.getClass();
                //获取每个对象的属性
                Field[] fields = object.getClass().getDeclaredFields();
                //遍历属性，判断是否有Autowired注解，有的话将属性注入
                for (Field field : fields) {
                    if (field.isAnnotationPresent(MyAutowired.class) && field.getAnnotation(MyAutowired.class).required()) {
                        //获取属性名
                        String[] names = field.getType().getName().split("\\.");
                        String name = names[names.length - 1];
                        //获取对象中的方法
                        Method[] methods = clazz.getMethods();
                        // 遍历对象中的所有方法，找到"set" + name
                        for (int j = 0; j < methods.length; j++) {
                            Method method = methods[j];
                            if (method.getName().equalsIgnoreCase("set" + name)) {
                                method.invoke(object, map.get(name));
                            }
                        }
                    }
                }

                //判断当前类是否有Transactional注解，若有则使用代理对象
                if (clazz.isAnnotationPresent(MyTransactional.class)) {
                    //获取代理工厂
                    ProxyFactory proxyFactory = (ProxyFactory)map.get("proxyFactory");
                    //获取类实现的所有接口
                    Class[] face = clazz.getInterfaces();
                    //判断对象是否实现接口
                    if (face != null && face.length > 0) {
                        //实现使用JDK
                        object = proxyFactory.getJdkProxy(object);
                    } else {
                        //没实现使用CGLIB
                        object = proxyFactory.getCglibProxy(object);
                    }
                }
                // 把处理之后的object重新放到map中
                map.put(entrySet.getKey(), object);
            }

        } catch (InstantiationException e) {
            e.printStackTrace();
        } catch (IllegalAccessException | InvocationTargetException e) {
            e.printStackTrace();
        }

    }


    // 任务二：对外提供获取实例对象的接口（根据id获取）
    public static Object getBean(String id) {
        return map.get(id);
    }

}
