package com.wk.goods.feign;

import com.wk.goods.entity.Category;
import entity.Result;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

@FeignClient("goods")
@RequestMapping("category")
public interface CategoryFeign {

    /**
     * 根据分类ID获取分类的对象信息
     * @param categoryId
     * @return
     */
    @GetMapping("/{id}")
    Result<Category> findById(@PathVariable(name = "id") Integer categoryId);

}
