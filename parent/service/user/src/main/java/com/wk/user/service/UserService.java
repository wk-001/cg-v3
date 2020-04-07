package com.wk.user.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.wk.user.entity.User;
import entity.Result;

import javax.servlet.http.HttpServletResponse;

public interface UserService extends IService<User> {

    Result login(String username, String password, HttpServletResponse response);

    void addPoint(String username, Integer point);
}
