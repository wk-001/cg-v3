package com.wk.goods.service.impl;

import com.wk.goods.entity.Sku;
import com.wk.goods.mapper.SkuMapper;
import com.wk.goods.service.SkuService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Map;

@Service
public class SkuServiceImpl extends ServiceImpl<SkuMapper, Sku> implements SkuService {

    @Autowired
    private SkuMapper skuMapper;

    /**
     * 支付失败，订单商品库存回滚
     * @param paramMap
     */
    @Override
    public void rollbackCount(Map<String, Object> paramMap) {
        for (Map.Entry<String, Object> entry : paramMap.entrySet()) {
            //商品ID
            Long id = Long.valueOf(entry.getKey());
            //商品回滚数量
            Integer num = Integer.valueOf(entry.getValue().toString());
            skuMapper.rollbackCount(id,num);
        }
    }

    /**
     * 支付成功，订单商品库存减少，注意库存超卖
     * @param paramMap
     */
    //@Transactional
    @Override
    public void decrCount(Map<String, Integer> paramMap) {
        //int i = 10/0; 订单直接创建成功，无法回滚，原因不明
        for (Map.Entry<String, Integer> entry : paramMap.entrySet()) {
            //商品ID
            Long id = Long.valueOf(entry.getKey());
            //要递减的数量 Map类型put字符串，强转Integer类型会报错
            Object value = entry.getValue();
            Integer num = Integer.parseInt(value.toString());

            //库存数量>=递减数量才能操作，多线程情况下无法保证数据的原子性，以下方式会出现超卖的问题
            /*Sku sku = skuMapper.selectById(id);
            if(sku.getNum()>=num){
                sku.setNum(sku.getNum()-num);
                skuMapper.updateById(sku);
            }*/

            //int i = 10/0; 无断点订单直接创建成功，无法回滚，有断点可以回滚，原因不明

            //解决方法：使用MySQL InnoDB引擎的行级锁机制控制超卖现象
            int count = skuMapper.decrCount(id,num);

            //根据返回值判断SQL是否执行成功
            if(count<=0){
                throw new RuntimeException("库存不足！");
            }
        }
    }

}
