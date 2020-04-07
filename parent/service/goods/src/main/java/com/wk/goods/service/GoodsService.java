package com.wk.goods.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.wk.goods.entity.Goods;

public interface GoodsService extends IService<Goods> {

    void saveOrUpdateGoods(Goods goods);

    Goods findGoodsBySpuId(Long spuId);

    void audit(Long spuId);

    void pull(Long spuId);

    void put(Long spuId);

    void putMany(Long[] spuIds);
}
