package com.wk.goods.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.wk.goods.entity.Category;
import com.wk.goods.mapper.CategoryMapper;
import com.wk.goods.service.CategoryService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class CategoryServiceImpl extends ServiceImpl<CategoryMapper, Category> implements CategoryService {

    @Override
    public List<Category> findByParentId(Integer pid) {
        Map<String,Object> paramMap = new HashMap<>();
        paramMap.put("parent_id",pid);
        return getBaseMapper().selectByMap(paramMap);
    }
}
