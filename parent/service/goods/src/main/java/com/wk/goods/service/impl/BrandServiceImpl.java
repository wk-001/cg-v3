package com.wk.goods.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.wk.goods.entity.Brand;
import com.wk.goods.mapper.BrandMapper;
import com.wk.goods.service.BrandService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class BrandServiceImpl extends ServiceImpl<BrandMapper, Brand> implements BrandService {

    @Autowired
    private BrandMapper brandMapper;

    @Override
    public List<Brand> findList(Brand brand) {
        QueryWrapper<Brand> wrapper = new QueryWrapper<Brand>()
                .like(StringUtils.isNotBlank(brand.getName()),"name",brand.getName())
                .eq(StringUtils.isNotBlank(brand.getLetter()),"letter",brand.getLetter());

        return brandMapper.selectList(wrapper);
    }

    @Override
    public PageInfo<Brand> findPage(Brand brand, Integer page, Integer size) {
        PageHelper.startPage(page,size);
        QueryWrapper<Brand> wrapper = new QueryWrapper<Brand>()
                .like(StringUtils.isNotBlank(brand.getName()),"name",brand.getName())
                .eq(StringUtils.isNotBlank(brand.getLetter()),"letter",brand.getLetter());
        return new PageInfo<Brand>(brandMapper.selectList(wrapper));
    }

    @Override
    public PageInfo<Brand> findPage(Integer page, Integer size) {
        PageHelper.startPage(page,size);
        return new PageInfo<Brand>(brandMapper.selectList(null));
    }

    @Override
    public List<Brand> findByCategory(Integer categoryId) {
        return brandMapper.findByCategory(categoryId);
    }
}
