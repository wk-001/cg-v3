package com.wk.user.controller;

import com.wk.user.entity.Content;
import com.wk.user.service.ContentService;
import entity.Result;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/content")
public class ContentController {

    @Autowired
    private ContentService contentService;

    /**
     * 根据categoryId查询广告集合
     * @param categoryId
     * @return
     */
    @GetMapping(value = "/list/category/{id}")
    public Result<List<Content>> findByCategory(@PathVariable("id") Long categoryId){
        return new Result<>(contentService.findByCategory(categoryId));
    }

}

