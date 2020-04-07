package com.wk.order.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.wk.goods.entity.Sku;
import com.wk.goods.entity.Spu;
import com.wk.goods.feign.SKUFeign;
import com.wk.goods.feign.SPUFeign;
import com.wk.order.entity.OrderItem;
import com.wk.order.service.CartService;
import constant.RedisKeyConstant;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class CartServiceImpl implements CartService {

    @Autowired
    private RedisTemplate redisTemplate;

    @Autowired
    private SKUFeign skuFeign;

    @Autowired
    private SPUFeign spuFeign;

    @Override
    public void add(Integer num, Long goodsId, String username) {

        //当添加购物车商品的数量<=0，则删除购物车中该商品
        if (num <= 0) {
            redisTemplate.boundHashOps(RedisKeyConstant.Cart+username).delete(goodsId);

            //如果购物车中商品总数量为0，则连购物车一起删除
            Long size = redisTemplate.boundHashOps(RedisKeyConstant.Cart + username).size();
            if (size == null || size<=0) {
                redisTemplate.delete(RedisKeyConstant.Cart + username);
            }
            return ;
        }

        //查询商品详情
        Sku sku = skuFeign.findById(goodsId).getData();

        Spu spu = spuFeign.findById(sku.getSpuId()).getData();

        //将加入购物车的商品信息封装成orderItem对象
        OrderItem orderItem = createOrderItem(num, sku, spu);

        /*将购物车的数据存入到Redis，一个用户只能有一个购物车
        数据类型是hash，namespace是username，key是商品ID，value是orderItem对象*/
        redisTemplate.boundHashOps(RedisKeyConstant.Cart+username).put(goodsId,orderItem);
    }

    @Override
    public List<OrderItem> list(String username) {
        //values：获取指定命名空间下所有的数据
        return redisTemplate.boundHashOps(RedisKeyConstant.Cart+username).values();
    }

    /**
     * 创建一个orderitem对象
     * @param num
     * @param sku
     * @param spu
     * @return
     */
    private OrderItem createOrderItem(Integer num, Sku sku, Spu spu) {
        OrderItem orderItem = new OrderItem();
        orderItem.setCategoryId1(spu.getCategory1Id());
        orderItem.setCategoryId2(spu.getCategory2Id());
        orderItem.setCategoryId3(spu.getCategory3Id());
        orderItem.setSpuId(spu.getId());
        orderItem.setSkuId(sku.getId());
        orderItem.setName(sku.getName());
        orderItem.setPrice(sku.getPrice());
        orderItem.setNum(num);
        orderItem.setMoney(num*sku.getPrice());
        orderItem.setImage(spu.getImage());
        return orderItem;
    }
}
