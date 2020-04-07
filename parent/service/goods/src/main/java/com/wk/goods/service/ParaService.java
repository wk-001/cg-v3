package com.wk.goods.service;

import com.wk.goods.entity.Para;
import com.baomidou.mybatisplus.extension.service.IService;

import java.util.List;

public interface ParaService extends IService<Para> {

    List<Para> findByCategory(Integer categoryId);
}
