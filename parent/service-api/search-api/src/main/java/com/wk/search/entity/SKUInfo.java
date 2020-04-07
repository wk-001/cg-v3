package com.wk.search.entity;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;
import org.springframework.data.annotation.Id;
import org.springframework.data.elasticsearch.annotations.Document;
import org.springframework.data.elasticsearch.annotations.Field;
import org.springframework.data.elasticsearch.annotations.FieldType;

import java.io.Serializable;
import java.util.Date;
import java.util.Map;
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@Document(indexName = "skuinfo",type = "docs")      //索引库映射配置
public class SKUInfo implements Serializable {
    //商品id，同时也是商品编号
    @Id
    private Long id;

    /**
     * SKU名称
     * type = FieldType.Text：类型，Text支持分词
     * analyzer = "ik_smart"：创建索引的分词器
     * index = true：添加数据时是否分词
     * store = false：是否存储，默认不存
     * searchAnalyzer = "ik_smart"：搜索时使用的分词器
     */
    @Field(type = FieldType.Text, analyzer = "ik_smart",index = true,store = false,searchAnalyzer = "ik_smart")
    private String name;

    //商品价格，单位为：元
    @Field(type = FieldType.Double)
    private Long price;

    //库存数量
    private Integer num;

    //商品图片
    private String image;

    //商品状态，1-正常，2-下架，3-删除
    private String status;

    //创建时间
    private Date createTime;

    //更新时间
    private Date updateTime;

    //是否默认
    private String isDefault;

    //SPUID
    private Long spuId;

    //类目ID
    private Long categoryId;

    /**
     * 类目名称
     * type = FieldType.Keyword：不分词
     * 小米手机 小米路由器 这是两个类目
     */
    @Field(type = FieldType.Keyword)
    private String categoryName;

    //品牌名称
    @Field(type = FieldType.Keyword)
    private String brandName;

    //规格
    private String spec;

    /**
     * 规格参数JSON字符串转成Map类型，key是规格，value是规格参数
     *  {"网络":"联通2G","颜色":"黑","存储":"16G","像素":"300万像素"}",
     *  {"网络":"联通3G","颜色":"白","存储":"32G","像素":"500万像素"}
     *  变更为Map<String, Set<String>>类型的：
     *  {"手机屏幕尺寸":["5寸","5.5寸"],"网络":["移动4G","联通4G","电信4G"],"颜色":["红","紫","白","蓝"]}
     */
    private Map<String,Object> specMap;

}
