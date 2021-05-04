package com.lagou.test;

import com.lagou.dao.IUserDao;
import com.lagou.io.Resources;
import com.lagou.pojo.User;
import com.lagou.sqlSession.SqlSession;
import com.lagou.sqlSession.SqlSessionFactory;
import com.lagou.sqlSession.SqlSessionFactoryBuilder;
import org.junit.Test;

import java.io.InputStream;
import java.util.List;

public class IPersistenceTest {

    @Test
    public void test() throws Exception {
        InputStream resourceAsSteam = Resources.getResourceAsSteam("sqlMapConfig.xml");
        SqlSessionFactory sqlSessionFactory = new SqlSessionFactoryBuilder().build(resourceAsSteam);
        SqlSession sqlSession = sqlSessionFactory.openSession();

        //调用
       /* User user = new User();
        user.setId(1);
        user.setUsername("张三");
        User user2 = sqlSession.selectOne("user.selectOne", user);

        System.out.println(user2);*/

       /* List<User> users = sqlSession.selectList("user.selectList");
        for (User user1 : users) {
            System.out.println(user1);
        }*/

       IUserDao userDao = sqlSession.getMapper(IUserDao.class);

        List<User> all = userDao.findAll();
        for (User user1 : all) {
            System.out.println(user1);
        }


    }

    @Test//测试新增用户
    public void testInsertUser() throws Exception {
        InputStream resourceAsSteam = Resources.getResourceAsSteam("sqlMapConfig.xml");
        SqlSessionFactory sqlSessionFactory = new SqlSessionFactoryBuilder().build(resourceAsSteam);
        SqlSession sqlSession = sqlSessionFactory.openSession();

        //创建用户对象
        User user = new User();
        user.setId(3);
        user.setUsername("张三");

        //获取IUserDao的代理实现类对象
        IUserDao userDao = sqlSession.getMapper(IUserDao.class);

        //执行新增操作
        int count = userDao.insertUser(user);
        System.out.println(count);

    }

    @Test//测试更新用户
    public void testUpdateUser() throws Exception {
        InputStream resourceAsSteam = Resources.getResourceAsSteam("sqlMapConfig.xml");
        SqlSessionFactory sqlSessionFactory = new SqlSessionFactoryBuilder().build(resourceAsSteam);
        SqlSession sqlSession = sqlSessionFactory.openSession();

        //创建用户对象
        User user = new User();
        user.setId(3);
        user.setUsername("李四");

        //获取IUserDao的代理实现类对象
        IUserDao userDao = sqlSession.getMapper(IUserDao.class);

        //执行更新操作
        int count = userDao.updatetUser(user);
        System.out.println(count);

    }

    @Test//测试删除用户
    public void testDeleteUser() throws Exception {
        InputStream resourceAsSteam = Resources.getResourceAsSteam("sqlMapConfig.xml");
        SqlSessionFactory sqlSessionFactory = new SqlSessionFactoryBuilder().build(resourceAsSteam);
        SqlSession sqlSession = sqlSessionFactory.openSession();

        //创建用户对象
        User user = new User();
        user.setId(3);

        //获取IUserDao的代理实现类对象
        IUserDao userDao = sqlSession.getMapper(IUserDao.class);

        //执行删除操作
        int count = userDao.delUser(user);
        System.out.println(count);

    }

}
