package com.wk.item.controller;

import com.wk.item.service.PageService;
import entity.Result;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("page")
public class PageController {

    @Autowired
    private PageService pageService;

    /**
     * 通过spuId查询出的数据生成静态页
     * @param spuId
     * @return
     */
    @RequestMapping("createHtml/{id}")
    public Result createHtml(@PathVariable("id") Long spuId){
        pageService.createHtml(spuId);
        return new Result();
    }
}
