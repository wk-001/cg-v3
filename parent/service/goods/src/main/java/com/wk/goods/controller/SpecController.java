package com.wk.goods.controller;

import com.wk.goods.entity.Spec;
import com.wk.goods.service.SpecService;
import entity.Result;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/spec")
public class SpecController {

    @Autowired
    private SpecService specService;

    /**
     * 根据分类ID查询规格信息，分类对象的template_id关联规格集合
     * @param categoryId 分类ID
     * @return
     */
    @GetMapping("category/{id}")
    public Result<List<Spec>> findByCategory(@PathVariable("id") Integer categoryId){
        return new Result<>(specService.findByCategory(categoryId));
    }

}

