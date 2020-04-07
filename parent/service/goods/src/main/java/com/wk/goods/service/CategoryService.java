package com.wk.goods.service;

import com.wk.goods.entity.Category;
import com.baomidou.mybatisplus.extension.service.IService;

import java.util.List;

public interface CategoryService extends IService<Category> {

    /**
     * 根据父节点ID查询所有子节点分类集合
     * @param pid 父节点ID
     * @return
     */
    List<Category> findByParentId(Integer pid);
}
