package com.wk.goods.mapper;

import com.wk.goods.entity.Spec;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Select;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface SpecMapper extends BaseMapper<Spec> {

    @Select("SELECT s.name,s.options from tb_spec s,tb_category c where s.template_id = c.template_id and c.id = #{categoryId}")
    List<Spec> findByCategory(Integer categoryId);
}
