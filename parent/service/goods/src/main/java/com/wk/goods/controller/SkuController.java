package com.wk.goods.controller;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.wk.goods.entity.Sku;
import com.wk.goods.service.SkuService;
import entity.Result;
import entity.StatusCode;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/sku")
@CrossOrigin
public class SkuController {

    @Autowired
    private SkuService skuService;

    /**
     * 支付失败，商品库存回滚
     * @param paramMap key：要递减的商品ID，value：商品库存回滚的数量
     * @return
     */
    @GetMapping("/rollback/count")
    public Result rollbackCount(@RequestParam Map<String,Object> paramMap){
        skuService.rollbackCount(paramMap);
        return new Result("商品库存回滚成功");
    }

    /**
     * 商品库存递减
     * @param paramMap key：要递减的商品ID，value：递减商品的数量
     * @return
     */
    @GetMapping("/decr/count")
    public Result decrCount(@RequestParam Map<String,Integer> paramMap){
        skuService.decrCount(paramMap);
        return new Result("商品库存递减成功");
    }

    /**
     * 根据spuId查询对应的sku集合
     * @param sku
     * @return
     */
    @PostMapping(value = "/search" )
    public Result<List<Sku>> findList(@RequestBody(required = false) Sku sku){
        return new Result<>(skuService.list(new QueryWrapper<>(sku)));
    }

    /***
     * 查询Sku全部数据
     * @return
     */
    @GetMapping
    public Result<List<Sku>> findAll(){
        return new Result<>(skuService.list());
    }

    /**
     * 根据ID查询sku信息
     * @param id
     * @return
     */
    @GetMapping("{id}")
    public Result<Sku> findById(@PathVariable Long id){
        return new Result<>(skuService.getById(id));
    }
}

