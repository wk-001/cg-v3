package com.wk.goods.mapper;

import com.wk.goods.entity.Brand;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Select;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface BrandMapper extends BaseMapper<Brand> {

    @Select("SELECT b.name from tb_brand b,tb_category_brand cb where b.id = cb.brand_id and cb.category_id =#{categoryId}")
    List<Brand> findByCategory(Integer categoryId);
}
