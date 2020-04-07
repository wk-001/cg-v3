package com.wk.seckill.service.impl;

import com.wk.seckill.entity.SeckillGoods;
import com.wk.seckill.mapper.SeckillGoodsMapper;
import com.wk.seckill.service.SeckillGoodsService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import constant.RedisKeyConstant;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class SeckillGoodsServiceImpl extends ServiceImpl<SeckillGoodsMapper, SeckillGoods> implements SeckillGoodsService {

    @Autowired
    private RedisTemplate redisTemplate;

    @Override
    public List<SeckillGoods> seckillGoodsList(String time) {
        return redisTemplate.boundHashOps(RedisKeyConstant.SeckillGoods+time).values();
    }

    @Override
    public SeckillGoods selectOne(String time, Long goodsId) {
        return (SeckillGoods) redisTemplate.boundHashOps(RedisKeyConstant.SeckillGoods+time).get(goodsId);
    }
}
