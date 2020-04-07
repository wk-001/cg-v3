package com.wk.goods.service;

import com.wk.goods.entity.Spec;
import com.baomidou.mybatisplus.extension.service.IService;

import java.util.List;

public interface SpecService extends IService<Spec> {

    List<Spec> findByCategory(Integer categoryId);
}
