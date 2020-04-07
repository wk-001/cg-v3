package com.wk.user.controller;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.wk.user.entity.Address;
import com.wk.user.service.AddressService;
import entity.Result;
import entity.TokenDecode;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/address")
@CrossOrigin
public class AddressController {

    @Autowired
    private AddressService addressService;

    /**
     * 根据用户名查询用户收件地址列表信息
     * @return
     */
    @GetMapping("user/list")
    public Result<List<Address>> list() {
        //获取用户登录信息
        String username = TokenDecode.getUserInfo().get("username");
        Address address = new Address();
        address.setUsername(username);
        return new Result<>(addressService.list(new QueryWrapper<>(address)));
    }

}

