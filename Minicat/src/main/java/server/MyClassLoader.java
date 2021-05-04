package server;

import java.io.*;

/**
 * 自定义类加载器
 */
public class MyClassLoader extends ClassLoader {
    private String basePath;

    public MyClassLoader(String basePath) {
        this.basePath = basePath;
    }

    @Override
    public Class<?> findClass(String packageName) {
        //获取.class文件全路径
        String classPath = basePath + packageName.replace(".", "/") + ".class";
        File file = new File(classPath);
        if (!file.exists()) {
            System.out.println("目录为" + classPath + "的文件不存在。。。");
            return null;
        }
        byte[] classBytes = null;
        FileInputStream fileInputStream = null;
        try {
            fileInputStream = new FileInputStream(file);
            classBytes = fileInputStream.readAllBytes();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return super.defineClass(packageName, classBytes, 0, classBytes.length);
    }
}
