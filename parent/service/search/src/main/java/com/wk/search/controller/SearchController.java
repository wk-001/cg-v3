package com.wk.search.controller;

import com.wk.search.service.SKUService;
import entity.Result;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("search")
@CrossOrigin
public class SearchController {

    @Autowired
    private SKUService skuService;

    /**
     * 调用搜索实现，搜索条件允许为空
     */
    @GetMapping
    public Map<String,Object> search(@RequestParam(required = false) Map<String,String> searchMap){
        return skuService.search(searchMap);
    }

    /**
     * sku数据导入到ES
     * @return
     */
    @GetMapping("import")
    public Result importSKUData(){
        skuService.importSKUData();
        return new Result();
    }
}
