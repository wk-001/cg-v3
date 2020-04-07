package com.wk.goods.mapper;

import com.wk.goods.entity.Sku;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Update;
import org.springframework.stereotype.Repository;

@Repository
public interface SkuMapper extends BaseMapper<Sku> {

    /**
     * 库存递减
     * @param id    商品ID
     * @param num   递减数量
     * @return
     */
    @Update("update tb_sku set num=num-#{num} where id=#{id} and num>=#{num}")
    int decrCount(@Param("id") Long id, @Param("num") Integer num);

    /**
     * 订单商品库存回滚
     * @param id    商品ID
     * @param num   回滚数量
     */
    @Update("update tb_sku set num=num+#{num} where id=#{id}")
    void rollbackCount(@Param("id") Long id, @Param("num") Integer num);
}
