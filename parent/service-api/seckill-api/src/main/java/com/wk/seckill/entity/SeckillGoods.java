package com.wk.seckill.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;

/**
 * <p>
 * 
 * </p>
 *
 * @author wk
 * @since 2020-03-28
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@TableName("tb_seckill_goods")
public class SeckillGoods implements Serializable {

    private static final long serialVersionUID=1L;

    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    /**
     * spu ID
     */
    private Long supId;

    /**
     * sku ID
     */
    private Long skuId;

    /**
     * 标题
     */
    private String name;

    /**
     * 商品图片
     */
    private String smallPic;

    /**
     * 原价格
     */
    private BigDecimal price;

    /**
     * 秒杀价格
     */
    private BigDecimal costPrice;

    /**
     * 添加日期
     */
    private Date createTime;

    /**
     * 审核日期
     */
    private Date checkTime;

    /**
     * 审核状态，0未审核，1审核通过，2审核不通过
     */
    private String status;

    /**
     * 开始时间
     */
    private Date startTime;

    /**
     * 结束时间
     */
    private Date endTime;

    /**
     * 秒杀商品数
     */
    private Integer num;

    /**
     * 剩余库存数
     */
    private Integer stockCount;

    /**
     * 描述
     */
    private String introduction;


}
