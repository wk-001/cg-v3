package com.wk.goods.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.github.pagehelper.PageInfo;
import com.wk.goods.entity.Brand;
import com.wk.goods.service.BrandService;
import entity.Result;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/brand")
@CrossOrigin
public class BrandController {

    @Autowired
    private BrandService brandService;

    /**
     * 根据分类ID查询品牌集合
     * @param categoryId 分类ID
     * @return
     */
    @GetMapping("category/{id}")
    public Result<List<Brand>> findByCategory(@PathVariable("id") Integer categoryId){
        return new Result<>(brandService.findByCategory(categoryId));
    }

    /***
     * 条件搜索后分页
     * @param page:当前页
     * @param size:每页显示多少条
     * @return
     */
    @PostMapping(value = "/search/{page}/{size}" )
    public Result<PageInfo<Brand>> findPage(@RequestBody Brand brand,@PathVariable  int page, @PathVariable  int size){
        return new Result<>(brandService.findPage(brand,page,size));
    }

    /***
     * 分页搜索实现
     * @param page:当前页
     * @param size:每页显示多少条
     * @return
     */
    @GetMapping(value = "/search/{page}/{size}" )
    public Result<PageInfo<Brand>> findPage(@PathVariable  int page, @PathVariable  int size){
        return new Result<>(brandService.findPage(page,size));
    }

    /**
     * 条件查询
     * @param brand
     * @return
     */
    @PostMapping("search")
    public Result<List<Brand>> findList(@RequestBody Brand brand){
        return new Result<>(brandService.findList(brand));
    }

    /**
     * 查询所有
     * @return
     */
    @GetMapping
    public Result<List<Brand>> findAll(){
        return new Result<>(brandService.list());
    }

}

