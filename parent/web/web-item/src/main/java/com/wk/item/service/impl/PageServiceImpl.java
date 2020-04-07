package com.wk.item.service.impl;

import com.alibaba.fastjson.JSON;
import com.wk.goods.feign.CategoryFeign;
import com.wk.goods.feign.SKUFeign;
import com.wk.goods.feign.SPUFeign;
import com.wk.goods.entity.Category;
import com.wk.goods.entity.Sku;
import com.wk.goods.entity.Spu;
import com.wk.item.service.PageService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


@Service
public class PageServiceImpl implements PageService {

    //thymeleaf模板引擎 用来生成静态页面
    @Autowired
    private TemplateEngine templateEngine;

    //根据spuId查询spu
    @Autowired
    private SPUFeign spuFeign;

    //根据spuId查询对应的sku集合
    @Autowired
    private SKUFeign skuFeign;

    //查询sku对应的1、2、3、级分类对象
    @Autowired
    private CategoryFeign categoryFeign;

    //生成静态页存储路径
    @Value("${pagepath}")
    private String pagepath;


    @Override
    public void createHtml(Long spuId) {
        try {
            //创建一个容器对象，用于存储页面所需的变量信息 Context：存储数据并显示到页面
            Context context = new Context();

            //获取页面所需要的数据
            Map<String, Object> dataMap = buildDataModel(spuId);
            context.setVariables(dataMap);

            //获取项目编译后的路径，需要在EnableMvcConfig.addResourceHandlers设置资源放行
            String path = PageServiceImpl.class.getResource("/").getPath()+"templates/items/";

            //判断要生成静态页面的路径是否存在，如不存在则创建
            File dirFile = new File(path);
            if(!dirFile.exists()){
                dirFile.mkdirs();
            }

            //创建一个writer对象，并指定生成静态文件的全路径
            FileWriter fileWriter = new FileWriter(path+spuId+".html");

            /**
             * 执行生成操作
             * 参数：1、指定模板，根据item.html生成；
             * 2、模板所需的数据模型；
             * 3、输出文件对象（文件生成到哪里去）
             */
            templateEngine.process("item",context,fileWriter);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * 查询spu、spu对应的sku集合、三级分类的信息，构建生成静态页需要的数据模型
     * @param spuId
     * @return
     */
    public Map<String,Object> buildDataModel(Long spuId){
        //查询spu
        Spu spu = spuFeign.findById(spuId).getData();

        //根据spu对象中的三个分类ID查询三个级别的分类信息
        Category category1 = categoryFeign.findById(spu.getCategory1Id()).getData();
        Category category2 = categoryFeign.findById(spu.getCategory2Id()).getData();
        Category category3 = categoryFeign.findById(spu.getCategory3Id()).getData();

        //查询sku集合
        Sku sku = new Sku();
        sku.setSpuId(spuId);
        List<Sku> skuList = skuFeign.findList(sku).getData();

        //创建map存储所有数据
        Map<String,Object> dataMap = new HashMap<>();
        dataMap.put("spu",spu);
        dataMap.put("category1",category1);
        dataMap.put("category2",category2);
        dataMap.put("category3",category3);
        dataMap.put("skuList",skuList);

        if (spu.getImages() != null) {
            //spu的图片在数据库中以字符串的形式存储，用逗号隔开，在前台遍历
            dataMap.put("images",spu.getImages().split(","));
        }

        //spec_items，将数据库中的规格json串转成map类型
        dataMap.put("specificationList", JSON.parseObject(spu.getSpecItems(),Map.class));

        return dataMap;
    }
}
