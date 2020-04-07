package com.wk.goods.mapper;

import com.wk.goods.entity.Para;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Select;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ParaMapper extends BaseMapper<Para> {

    @Select("SELECT s.name,s.options from tb_para s,tb_category c where s.template_id = c.template_id and c.id = #{categoryId}")
    List<Para> findByCategory(Integer categoryId);
}
