package com.wk.goods.service.impl;

import com.wk.goods.entity.Spec;
import com.wk.goods.mapper.SpecMapper;
import com.wk.goods.service.SpecService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class SpecServiceImpl extends ServiceImpl<SpecMapper, Spec> implements SpecService {

    @Override
    public List<Spec> findByCategory(Integer categoryId) {
        return getBaseMapper().findByCategory(categoryId);
    }
}
