package com.wk.goods.controller;

import com.wk.goods.entity.Goods;
import com.wk.goods.service.GoodsService;
import entity.Result;
import entity.StatusCode;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/goods")
@CrossOrigin
public class GoodsController {

    @Autowired
    private GoodsService goodsService;

    /**
     * 商品批量上架
     * @param spuIds
     */
    @PutMapping("put/many")
    public Result put(@RequestBody Long[] spuIds){
        goodsService.putMany(spuIds);
        return new Result();
    }

    /**
     * 商品上架
     * @param spuId
     */
    @PutMapping("put/{id}")
    public Result put(@PathVariable("id") Long spuId){
        goodsService.put(spuId);
        return new Result();
    }

    /**
     * 商品下架
     * @param spuId
     */
    @PutMapping("pull/{id}")
    public Result pull(@PathVariable("id") Long spuId){
        goodsService.pull(spuId);
        return new Result();
    }

    /**
     * 商品审核
     * @param spuId
     */
    @PutMapping("audit/{id}")
    public Result audit(@PathVariable("id") Long spuId){
        goodsService.audit(spuId);
        return new Result();
    }

    /**
     * 根据spuid查询goods信息
     * @param spuId
     * @return
     */
    @GetMapping("{id}")
    public Result<Goods> findGoodsBySpuId(@PathVariable("id") Long spuId){
        return new Result<>(goodsService.findGoodsBySpuId(spuId));
    }

    /**
     * 商品增加/修改
     * @param goods 一个spu和多个sku
     */
    @PostMapping("saveOrUpdate")
    public Result addGoods(@RequestBody Goods goods){
        goodsService.saveOrUpdateGoods(goods);
        return new Result();
    }

}

