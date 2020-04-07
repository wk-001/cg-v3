package com.wk.goods.service.impl;

import com.wk.goods.entity.Para;
import com.wk.goods.mapper.ParaMapper;
import com.wk.goods.service.ParaService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ParaServiceImpl extends ServiceImpl<ParaMapper, Para> implements ParaService {

    @Override
    public List<Para> findByCategory(Integer categoryId) {
        return getBaseMapper().findByCategory(categoryId);
    }
}
