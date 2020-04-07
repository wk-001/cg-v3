package com.wk.order.controller;

import com.wk.order.entity.OrderItem;
import com.wk.order.service.CartService;
import entity.Result;
import entity.TokenDecode;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("cart")
@CrossOrigin
public class CartController {

    @Autowired
    private CartService cartService;

    /**
     * 商品加入购物车
     * @param num 加入购物车的数量
     * @param goodsId 加入购物车的商品ID
     * @return
     */
    @RequestMapping(value = "/add")
    public Result addCart(Integer num, Long goodsId){
        String username = TokenDecode.getUserInfo().get("username");
        cartService.add(num,goodsId,username);
        return new Result();
    }

    /**
     * 购物车列表
     */
    @GetMapping("list")
    public Result<List<OrderItem>> list(){
        String username = TokenDecode.getUserInfo().get("username");
        return new Result<>(cartService.list(username));
    }

}
