package com.wk.order.controller;

import com.wk.order.entity.Order;
import com.wk.order.service.OrderService;
import entity.Result;
import entity.StatusCode;
import entity.TokenDecode;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/order")
@CrossOrigin
public class OrderController {

    @Autowired
    private OrderService orderService;

    /**
     * 购物车结算生成订单
     * @param order
     * @return
     */
    @PostMapping
    public Result add(@RequestBody Order order){
        //获取当前用户名，并赋值给order
        String username = TokenDecode.getUserInfo().get("username");
        order.setUsername(username);
        //调用OrderService实现添加Order
        orderService.add(order);
        return new Result("订单创建成功");
    }

}

