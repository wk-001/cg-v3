package com.wk.user.feign;

import com.wk.user.entity.Content;
import entity.Result;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.List;

@FeignClient("content")
@RequestMapping("content")
public interface ContentFeign {

    /**
     * 根据categoryId查询广告集合
     * @param categoryId
     * @return
     */
    @GetMapping(value = "/list/category/{id}")
    Result<List<Content>> findByCategory(@PathVariable("id") Long categoryId);

}
