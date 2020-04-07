package com.wk.user.service.impl;

import com.alibaba.fastjson.JSON;
import com.wk.user.entity.User;
import com.wk.user.mapper.UserMapper;
import com.wk.user.service.UserService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import entity.BCrypt;
import entity.JwtUtil;
import entity.Result;
import entity.StatusCode;
import org.springframework.stereotype.Service;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletResponse;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@Service
public class UserServiceImpl extends ServiceImpl<UserMapper, User> implements UserService {

    //令牌名字
    private static final String AUTHORIZE_TOKEN = "Authorization";

    @Override
    public Result login(String username, String password, HttpServletResponse response) {
        //查询用户信息
        User user = getBaseMapper().selectById(username);

        //比较密码
        if(BCrypt.checkpw(password,user.getPassword())){
            //设置令牌信息
            Map<String,Object> info = new HashMap<String,Object>();
            info.put("role","USER");            //给用户设置角色
            info.put("success","SUCCESS");
            info.put("username",username);

            //生成令牌
            String token = JwtUtil.createJWT(UUID.randomUUID().toString(), JSON.toJSONString(info), null);

            //把令牌信息存入cookie
            Cookie cookie = new Cookie(AUTHORIZE_TOKEN,token);
            cookie.setDomain("localhost");      //所属域名
            cookie.setPath("/");        //存储到根路径下
            response.addCookie(cookie);

            //把令牌作为参数给用户
            return new Result("登录成功",token);
        }

        return new Result(false, StatusCode.LOGINERROR,"用户名或密码错误");
    }

    @Override
    public void addPoint(String username, Integer point) {
        getBaseMapper().addPoint(username, point);
    }
}
