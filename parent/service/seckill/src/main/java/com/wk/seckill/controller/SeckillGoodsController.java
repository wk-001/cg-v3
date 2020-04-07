package com.wk.seckill.controller;

import com.wk.seckill.entity.SeckillGoods;
import com.wk.seckill.service.SeckillGoodsService;
import entity.DateUtil;
import entity.Result;
import entity.StatusCode;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import org.springframework.web.bind.annotation.RestController;

import java.util.Date;
import java.util.List;

@RestController
@RequestMapping("/seckillGoods")
public class SeckillGoodsController {

    @Autowired
    private SeckillGoodsService seckillGoodsService;

    /**
     * 根据时间区间和秒杀商品ID查询单个商品详情
     * @return
     */
    @GetMapping("/one")
    public Result<SeckillGoods> getOne(String time,Long goodsId){
        return new Result<>(seckillGoodsService.selectOne(time,goodsId));
    }

    /**
     * 查询时间区间
     * @return
     */
    @GetMapping("/menus")
    public Result<List<Date>> menus(){
        return new Result<>(DateUtil.getDateMenus());
    }

    /**
     * 根据时间区间获取秒杀频道商品列表
     * @param time 时间区间
     * @return
     */
    @GetMapping("/list")
    public Result<List<SeckillGoods>> list(String time){
        return new Result<>(seckillGoodsService.seckillGoodsList(time));
    }

}

