package com.wk.goods.controller;

import com.wk.goods.entity.Category;
import com.wk.goods.service.CategoryService;
import entity.Result;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/category")
@CrossOrigin
public class CategoryController {

    @Autowired
    private CategoryService categoryService;

    /**
     * 根据分类ID获取分类的对象信息
     * @param categoryId
     * @return
     */
    @GetMapping("/{id}")
    public Result<Category> findById(@PathVariable(name = "id") Integer categoryId){
        return new Result<>(categoryService.getById(categoryId));
    }

    /**
     * 根据父节点ID查询所有子节点分类集合
     * @param pid 父节点ID 1级分类：0；2级分类取决于选中的1级分类
     * @return
     */
    @GetMapping(value = "/list/{pid}")
    public Result<List<Category>> findByParentId(@PathVariable(value = "pid")Integer pid){
        return new Result<>(categoryService.findByParentId(pid));
    }
}

