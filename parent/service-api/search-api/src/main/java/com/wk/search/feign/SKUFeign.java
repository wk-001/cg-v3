package com.wk.search.feign;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.Map;

@FeignClient("search")
@RequestMapping("search")
public interface SKUFeign {

    /**
     * 调用搜索实现，搜索条件允许为空
     */
    @GetMapping
    Map<String,Object> search(@RequestParam(required = false) Map<String, String> searchMap);


}
