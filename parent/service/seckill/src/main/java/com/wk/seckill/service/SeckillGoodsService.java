package com.wk.seckill.service;

import com.wk.seckill.entity.SeckillGoods;
import com.baomidou.mybatisplus.extension.service.IService;

import java.util.List;

public interface SeckillGoodsService extends IService<SeckillGoods> {

    List<SeckillGoods> seckillGoodsList(String time);

    SeckillGoods selectOne(String time, Long goodsId);
}
