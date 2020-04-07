package com.wk.order.service;

import com.wk.order.entity.OrderItem;

import java.util.List;

public interface CartService {

    void add(Integer num, Long goodsId, String username);

    List<OrderItem> list(String username);
}
