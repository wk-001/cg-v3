package com.wk.user.controller;

import com.wk.user.entity.User;
import com.wk.user.service.UserService;
import entity.Result;
import entity.TokenDecode;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletResponse;
import java.util.List;

@RestController
@RequestMapping("/user")
@CrossOrigin
public class UserController {

    @Autowired
    private UserService userService;

    /**
     * 用户下单后添加积分
     * @param point
     * @return
     */
    @GetMapping(value = "/points/add")
    public Result addPoint(@RequestParam Integer point){
        String username = TokenDecode.getUserInfo().get("username");
        userService.addPoint(username,point);
        return new Result();
    }

    /**
     * 用户登录
     */
    @GetMapping("login")
    public Result login(String username, String password, HttpServletResponse response) {
        return userService.login(username,password,response);
    }

    /**
     * @PreAuthorize 方法执行前进行拦截，限制只允许角色是admin的用户访问该方法
     */
    @PreAuthorize("hasAnyRole('admin')")
    @GetMapping
    public Result<List<User>> findAll(){
        return new Result<>(userService.list());
    }

    @GetMapping({"/{id}","/load/{id}"})
    public Result<User> getById(@PathVariable String id){
        return new Result<>(userService.getById(id));
    }
}

