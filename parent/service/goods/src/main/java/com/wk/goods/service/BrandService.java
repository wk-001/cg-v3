package com.wk.goods.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.github.pagehelper.PageInfo;
import com.wk.goods.entity.Brand;
import com.baomidou.mybatisplus.extension.service.IService;

import java.util.List;

public interface BrandService extends IService<Brand> {

    List<Brand> findList(Brand brand);

    /**
     * 条件搜索后分页
     * @param brand 搜索条件对象
     * @param page 当前页
     * @param size  每页显示多少条数据
     * @return
     */
    PageInfo<Brand> findPage(Brand brand,Integer page, Integer size);

    /**
     * 分页
     * @param page 当前页
     * @param size  每页显示多少条数据
     * @return
     */
    PageInfo<Brand> findPage(Integer page, Integer size);

    /**
     * 根据分类ID查询品牌集合
     * @param categoryId 分类ID
     * @return
     */
    List<Brand> findByCategory(Integer categoryId);
}
