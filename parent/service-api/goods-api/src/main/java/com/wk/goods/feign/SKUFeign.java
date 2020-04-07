package com.wk.goods.feign;

import com.wk.goods.entity.Sku;
import entity.Result;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@FeignClient("goods")
@RequestMapping("sku")
public interface SKUFeign {

    /**
     * 支付失败，商品库存回滚
     * @param paramMap key：要递减的商品ID，value：商品库存回滚的数量
     * @return
     */
    @GetMapping("/rollback/count")
    Result rollbackCount(@RequestParam Map<String,Object> paramMap);

    /**
     * 商品库存递减
     * @param paramMap key：要递减的商品ID，value：递减商品的数量
     * @return
     */
    @GetMapping("/decr/count")
    Result decrCount(@RequestParam Map<String,Integer> paramMap);

    /**
     * 根据ID查询sku信息
     * @param id
     * @return
     */
    @GetMapping("{id}")
    Result<Sku> findById(@PathVariable Long id);

    /**
     * 根据spuId查询对应的sku集合
     * @param sku
     * @return
     */
    @PostMapping(value = "/search" )
    Result<List<Sku>> findList(@RequestBody(required = false) Sku sku);

    /***
     * 查询Sku全部数据
     * @return
     */
    @GetMapping
    Result<List<Sku>> findAll();
}
