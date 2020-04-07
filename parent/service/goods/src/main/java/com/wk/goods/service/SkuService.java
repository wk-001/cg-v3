package com.wk.goods.service;

import com.wk.goods.entity.Sku;
import com.baomidou.mybatisplus.extension.service.IService;

import java.util.Map;

public interface SkuService extends IService<Sku> {

    void decrCount(Map<String, Integer> paramMap);

    void rollbackCount(Map<String, Object> paramMap);
}
