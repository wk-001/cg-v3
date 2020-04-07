package com.wk.goods.controller;

import com.wk.goods.entity.Para;
import com.wk.goods.service.ParaService;
import entity.Result;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/para")
@CrossOrigin
public class ParaController {

    @Autowired
    private ParaService paraService;

    /**
     * 根据分类ID查询商品参数集合
     * @param categoryId 分类ID
     * @return
     */
    @GetMapping("category/{id}")
    public Result<List<Para>> findByCategory(@PathVariable("id") Integer categoryId){
        return new Result<>(paraService.findByCategory(categoryId));
    }

}

