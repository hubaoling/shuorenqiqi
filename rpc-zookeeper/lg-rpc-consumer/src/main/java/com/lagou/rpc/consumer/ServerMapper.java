package com.lagou.rpc.consumer;

import com.lagou.rpc.api.IUserService;

public class ServerMapper {
    private Long lastTime;
    private IUserService userService;

    public Long getLastTime() {
        return lastTime;
    }

    public void setLastTime(Long lastTime) {
        this.lastTime = lastTime;
    }

    public IUserService getUserService() {
        return userService;
    }

    public void setUserService(IUserService userService) {
        this.userService = userService;
    }
}
