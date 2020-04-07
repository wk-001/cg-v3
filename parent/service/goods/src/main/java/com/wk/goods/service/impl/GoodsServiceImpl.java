package com.wk.goods.service.impl;

import com.alibaba.fastjson.JSON;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.wk.goods.entity.*;
import com.wk.goods.mapper.*;
import com.wk.goods.service.GoodsService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import entity.IdWorker;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.*;

@Service
public class GoodsServiceImpl extends ServiceImpl<GoodsMapper, Goods> implements GoodsService {

    @Autowired
    private SpuMapper spuMapper;

    @Autowired
    private SkuMapper skuMapper;

    @Autowired
    private IdWorker idWorker;

    @Autowired
    private BrandMapper brandMapper;

    @Autowired
    private CategoryMapper categoryMapper;


    @Override
    public void saveOrUpdateGoods(Goods goods) {
        Spu spu = goods.getSpu();

        //ID为空做增加操作
        if (spu.getId() == null) {
            spu.setId(idWorker.nextId());
            spuMapper.insert(spu);
        }else {
            //修改spu
            spuMapper.updateById(spu);
            //删除spu对应的所有sku，再重新添加sku
            Sku sku = new Sku();
            sku.setSpuId(spu.getId());
            skuMapper.delete(new QueryWrapper<>(sku));
        }

        //三级分类信息
        Category category = categoryMapper.selectById(spu.getCategory3Id());

        //品牌信息
        Brand brand = brandMapper.selectById(spu.getBrandId());

        Date date = new Date();
        for (Sku sku : goods.getSkuList()) {
            sku.setId(idWorker.nextId());

            //预防传入的规格参数为空
            if(StringUtils.isEmpty(sku.getSpec())){
                sku.setSpec("{}");
            }

            ////获取sku规格信息，将json格式的数据转成map类型
            Map<String,String> map = JSON.parseObject(sku.getSpec(), Map.class);
            StringBuilder builder = new StringBuilder(spu.getName());
            for (String value : map.values()) {
                builder.append(" ").append(value);
            }

            sku.setName(builder.toString());            //sku的名字是spu+规格信息spec
            sku.setCreateTime(date);
            sku.setUpdateTime(date);
            sku.setSpuId(spu.getId());
            sku.setCategoryId(spu.getCategory3Id());        //三级分类ID
            sku.setCategoryName(category.getName());        //三级分类名称
            sku.setBrandName(brand.getName());
            skuMapper.insert(sku);
        }
    }

    @Override
    public Goods findGoodsBySpuId(Long spuId) {
        Spu spu = spuMapper.selectById(spuId);
        Sku sku = new Sku();
        sku.setSpuId(spuId);
        List<Sku> skuList = skuMapper.selectList(new QueryWrapper<>(sku));
        return new Goods(spu,skuList);
    }

    @Override
    public void audit(Long spuId) {
        Spu spu = spuMapper.selectById(spuId);
        //判断商品是否被删除
        if ("1".equals(spu.getIsDelete())) {
            throw new RuntimeException("该商品已删除!");
        }
        spu.setStatus("1");         //审核通过
        spu.setIsMarketable("1");   //上架
        spuMapper.updateById(spu);
    }

    @Override
    public void pull(Long spuId) {
        Spu spu = spuMapper.selectById(spuId);
        //判断商品是否被删除
        if ("1".equals(spu.getIsDelete())) {
            throw new RuntimeException("该商品已删除!");
        }
        spu.setIsMarketable("0");   //下架
        spuMapper.updateById(spu);
    }

    @Override
    public void put(Long spuId) {
        Spu spu = spuMapper.selectById(spuId);
        //判断商品是否被删除
        if ("1".equals(spu.getIsDelete())) {
            throw new RuntimeException("该商品已删除!");
        }
        if ("1".equals(spu.getStatus())) {
            throw new RuntimeException("未通过审核的商品不能上架！");
        }
        spu.setIsMarketable("1");   //上架
        spuMapper.updateById(spu);
    }

    @Override
    public void putMany(Long[] spuIds) {
        //id in(spuids) and is_delete=0 and status =1  未删除，审核通过的
        QueryWrapper<Spu> wrapper = new QueryWrapper<Spu>()
                .in("id", Arrays.asList(spuIds))
                .eq("is_delete",0)
                .eq("status",1);
        Spu spu = new Spu();
        spu.setIsMarketable("1");
        spuMapper.update(spu,wrapper);
    }
}
