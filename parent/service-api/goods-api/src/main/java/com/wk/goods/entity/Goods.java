package com.wk.goods.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;

import java.io.Serializable;
import java.util.List;

/**
 * 商品信息组合对象，方便添加商品
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@AllArgsConstructor
@NoArgsConstructor
public class Goods implements Serializable {

    //spu信息
    private Spu spu;

    //sku集合信息
    private List<Sku> skuList;

}
