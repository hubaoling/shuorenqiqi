package com.lagou.dao;

import com.lagou.pojo.User;

import java.util.List;

public interface IUserDao {

    //查询所有用户
    public List<User> findAll() throws Exception;


    //根据条件进行用户查询
    public User findByCondition(User user) throws Exception;

    //增加一个用户
    public int insertUser(User user) throws Exception;

    //修改用户信息（修改某个用户的名称）
    public int updatetUser(User user) throws Exception;

    //删除用户信息
    public int delUser(User user) throws Exception;
}
