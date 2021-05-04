package com.lagou.sqlSession;

import com.lagou.pojo.Configuration;
import com.lagou.pojo.MappedStatement;

import java.util.List;

public interface Executor {

    public <E> List<E> query(Configuration configuration,MappedStatement mappedStatement,Object... params) throws Exception;

    //更新接口（包括新增，更新，删除，因为底层调用的都是PreparedStatement.executeUpdate())
    //所以这里只需要一个接口处理上层的增删改的请求
    public int update(Configuration configuration,MappedStatement mappedStatement,Object... params) throws Exception;
}
