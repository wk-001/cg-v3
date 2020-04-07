package com.wk.search.service.impl;

import com.alibaba.fastjson.JSON;
import com.wk.goods.entity.Sku;
import com.wk.goods.feign.SKUFeign;
import com.wk.search.mapper.SKUESMapper;
import com.wk.search.service.SKUService;
import org.apache.commons.lang.StringUtils;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.common.text.Text;
import org.elasticsearch.index.query.BoolQueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.search.SearchHit;
import org.elasticsearch.search.aggregations.AggregationBuilders;
import org.elasticsearch.search.aggregations.bucket.terms.StringTerms;
import org.elasticsearch.search.fetch.subphase.highlight.HighlightBuilder;
import org.elasticsearch.search.fetch.subphase.highlight.HighlightField;
import org.elasticsearch.search.sort.FieldSortBuilder;
import org.elasticsearch.search.sort.SortOrder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.elasticsearch.core.ElasticsearchTemplate;
import org.springframework.data.elasticsearch.core.SearchResultMapper;
import org.springframework.data.elasticsearch.core.aggregation.AggregatedPage;
import org.springframework.data.elasticsearch.core.aggregation.impl.AggregatedPageImpl;
import org.springframework.data.elasticsearch.core.query.NativeSearchQuery;
import org.springframework.data.elasticsearch.core.query.NativeSearchQueryBuilder;
import org.springframework.stereotype.Service;
import com.wk.search.entity.SKUInfo;

import java.util.*;

@Service
public class SKUServiceImpl implements SKUService {

    @Autowired
    private SKUFeign skuFeign;

    @Autowired
    private SKUESMapper skuesMapper;

    /**
     * 实现索引库的增删改查和高级搜索操作
     */
    @Autowired
    private ElasticsearchTemplate elasticsearchTemplate;

    /**
     * 多条件搜索
     * @param searchMap
     * @return
     */
    @Override
    public Map<String, Object> search(Map<String, String> searchMap) {
        //搜索条件封装
        NativeSearchQueryBuilder builder = buildBasicQuery(searchMap);

        //集合搜索
        Map<String, Object> resultMap = searchList(builder);

        //分组搜索实现
        Map<String, Object> groupMap = searchGroupList(builder, searchMap);

        resultMap.putAll(groupMap);

        return resultMap;
    }

    //搜索条件封装
    private NativeSearchQueryBuilder buildBasicQuery(Map<String, String> searchMap) {
        //搜索条件封装
        NativeSearchQueryBuilder builder = new NativeSearchQueryBuilder();        //搜索条件构建对象，用于封装各种搜索条件

        //搜索条件过滤
        BoolQueryBuilder queryBuilder = QueryBuilders.boolQuery();

        if (searchMap != null && searchMap.size()>0) {
            String keywords = searchMap.get("keywords");        //获取搜索条件中的关键词
            if (StringUtils.isNotEmpty(keywords)) {             //如果关键词不为空，则搜索关键词数据
                //在name字段中查找关键词，must相当于&&
                queryBuilder.must(QueryBuilders.queryStringQuery(keywords).field("name"));
            }

            //如果用户选择了分类，就过滤分类条件
            String category = searchMap.get("category");
            if (StringUtils.isNotEmpty(category)) {
                //termQuery：分类不需要分词，参数1：域的名称；参数2：需要搜索的值
                queryBuilder.must(QueryBuilders.termQuery("categoryName",category));
            }

            //如果用户选择了品牌，就过滤品牌条件
            String brand = searchMap.get("brand");
            if (StringUtils.isNotEmpty(brand)) {
                //termQuery：品牌不需要分词，参数1：域的名称；参数2：需要搜索的值
                queryBuilder.must(QueryBuilders.termQuery("brandName",brand));
            }

            //规格过滤实现，规格前缀spec_
            for (Map.Entry<String, String> spec : searchMap.entrySet()) {
                String key = spec.getKey();
                if(key.startsWith("spec_")){        //如果key以spec_开头，表示查询规格
                    String value = spec.getValue().replace("\\","");   //规格条件的值
                    //搜索条件为specMap.xx.keyword,去掉前面的5个字符spec_ .keyword表示不分词
                    queryBuilder.must(QueryBuilders.termQuery("specMap."+key.substring(5)+".keyword",value));
                }
            }

            //价格区间 price 0-500元 500-1000元 ... 3000元以上
            String price = searchMap.get("price");
            if(StringUtils.isNotEmpty(price)){
                //如果有中文就去掉中文元和以上
                //price.replace("元","").replace("以上","");
                //根据"-"分割价格 [0,500],[500,1000]...[3000]
                String[] prices = price.split("-");
                //数组第一个元素一定不为空，第二个有可能为空，大于第一个值，小于等于第二个值
                if(prices.length > 0){
                    queryBuilder.must(QueryBuilders.rangeQuery("price").gt(Integer.parseInt(prices[0])));
                    if(prices.length==2){
                        queryBuilder.must(QueryBuilders.rangeQuery("price").lte(Integer.parseInt(prices[1])));
                    }
                }
            }

            //排序实现
            String sortField = searchMap.get("sortField");          //要排序的域
            String sortRule = searchMap.get("sortRule");            //排序方式：升序/降序
            if(StringUtils.isNotEmpty(sortField) && StringUtils.isNotEmpty(sortRule)){
                builder.withSort(
                        new FieldSortBuilder(sortField)         //指定排序域
                                .order(SortOrder.valueOf(sortRule)));   //指定排序规则
            }
        }

        //分页，默认第一页
        int pageNum = converterPage(searchMap);        //默认第一页
        int size = 30;           //默认查询的数据条数
        //1、当前页；2、每页显示多少条数据
        builder.withPageable(PageRequest.of(pageNum-1,size));

        //将过滤条件填充到搜索条件中
        builder.withQuery(queryBuilder);

        return builder;
    }

    /**
     * 接收前端传入的分页参数
     */
    public Integer converterPage(Map<String, String> searchMap){
        if (searchMap != null) {
            String pageNum = searchMap.get("pageNum");
            try {
                return Integer.parseInt(pageNum);
            } catch (NumberFormatException e) {
                System.err.println("默认第一页");
                //e.printStackTrace();
            }
        }
        return 1;
    }

    /**
     * 查询商品的分类、集合、品牌集合，并对结果进行分组
     * @param builder
     * @return
     */
    private Map<String, Object> searchGroupList(NativeSearchQueryBuilder builder, Map<String, String> searchMap) {
        /**
         * addAggregation()：添加一个聚合操作，如查询个数、分组
         * terms()：域的别名
         * field()：根据指定域进行分组
         */
        //当用户选择了分类后，将之作为搜索条件，则不需要对此进行分组搜索，因为分组搜索的数据是用于显示分类搜索条件的
        if(searchMap==null || StringUtils.isEmpty(searchMap.get("category"))) {
            builder.addAggregation(AggregationBuilders.terms("skuCategoryGroup").field("categoryName"));
        }

        //当用户选择了品牌类后，将之作为搜索条件，则不需要对此进行分组搜索，因为分组搜索的数据是用于显示品牌搜索条件的
        if(searchMap==null || StringUtils.isEmpty(searchMap.get("brand"))) {
            builder.addAggregation(AggregationBuilders.terms("skuBrandGroup").field("brandName"));
        }

        //spec.keyword：规格参数不分词
        builder.addAggregation(AggregationBuilders.terms("skuSpecGroup").field("spec.keyword").size(1000));

        //根据分组条件查询
        AggregatedPage<SKUInfo> aggregatedPage = elasticsearchTemplate.queryForPage(builder.build(), SKUInfo.class);

        Map<String,Object> groupResult = new HashMap<>();

        /**
         * 获取分组数据
         * aggregatedPage.getAggregations()：获取的是集合，可以根据多个域进行分组
         * get("skuCategory")：获取指定域的集合数据
         * StringTerms：把返回值转成字符串类型
         */

        if(searchMap==null || StringUtils.isEmpty(searchMap.get("category"))) {
            //获取分类集合数据，并分组
            StringTerms categoryTerms = aggregatedPage.getAggregations().get("skuCategoryGroup");
            groupResult.put("categoryList",getGroupList(categoryTerms));
        }

        if(searchMap==null || StringUtils.isEmpty(searchMap.get("brand"))) {
            //获取品牌分组集合数据
            StringTerms brandTerms = aggregatedPage.getAggregations().get("skuBrandGroup");
            groupResult.put("brandList",getGroupList(brandTerms));
        }

        //获取规格分组集合数据
        StringTerms specTerms = aggregatedPage.getAggregations().get("skuSpecGroup");
        //合并集合数据
        Map<String, Set<String>> allSpec = putAllSpec(getGroupList(specTerms));
        groupResult.put("specList",allSpec);

        return groupResult;
    }

    /**
     * 获取分组集合数据
     * @param stringTerms
     * @return
     */
    private List<String> getGroupList(StringTerms stringTerms) {
        List<String> groupList = new ArrayList<>();

        for (StringTerms.Bucket bucket : stringTerms.getBuckets()) {
            String keyAsString = bucket.getKeyAsString();       //其中一个分类的名字
            groupList.add(keyAsString);
        }
        return groupList;
    }

    /**
     * 规格汇总合并，规格只需要一种，所以用set存储
     * 将list类型的{"网络":"联通2G","颜色":"黑","存储":"16G","像素":"300万像素"}",
     *           {"网络":"联通3G","颜色":"白","存储":"32G","像素":"500万像素"}
     * 转换为Map<String, Set<String>>类型的：
     *  {"手机屏幕尺寸":["5寸","5.5寸"],
     *  "网络":["移动4G","联通4G","电信4G"],
     *  "颜色":["红","紫","白","蓝"]}
     * @param specList
     * @return
     */
    private Map<String, Set<String>> putAllSpec(List<String> specList) {
        //将集合中的字符串对象转成Map类型，将Map对象合成一个Map<String,Set<String>>对象,合并后的Map对象
        Map<String, Set<String>> allSpec = new HashMap<>();

        //遍历字符串集合,此时spec="{"手机屏幕尺寸":"5.5寸","网络":"移动4G","颜色":"红","机身内存":"32G","存储":"16G","像素":"800万像素"}"
        for (String spec : specList) {
            //将每个JSON字符串转成Map
            Map<String,String> map = JSON.parseObject(spec, Map.class);

            //4、合并流程,循环所有Map
            for (Map.Entry<String, String> entry : map.entrySet()) {
                //4.2、取出当前Map，获取对应的Key和Value
                String key = entry.getKey();        //规格名称
                String value = entry.getValue();    //规格参数

                //4.3、将Key和Value合成一个Map<String,Set<String>>对象
                Set<String> specSet = allSpec.get(key);     //在之前数据的基础上进行增加，所以要从allSpec中获取当前规格对应的set集合数据
                if (specSet == null) {              //如果为空，代表之前没有对应set集合
                    specSet = new HashSet<>();
                }
                specSet.add(value);
                allSpec.put(key,specSet);
            }
        }
        return allSpec;
    }

    //集合搜索
    private Map<String, Object> searchList(NativeSearchQueryBuilder builder) {
        //高亮配置
        HighlightBuilder.Field field = new HighlightBuilder.Field("name");      //指定高亮域
        //高亮前缀
        field.preTags("<em style=\"color:red;\">");
        //高亮后缀
        field.postTags("</em>");
        //碎片长度。关键词前后数据显示的长度，有默认值
        field.fragmentSize(200);

        //添加高亮
        builder.withHighlightFields(field);


        /**
         * 执行搜索，响应结果
         * 参数：1、搜索条件的封装；2、搜索的结果集需要转换的类型
         * 返回值AggregatedPage是搜索结果集的封装
         AggregatedPage<SKUInfo> page = elasticsearchTemplate.queryForPage(builder.build(), SKUInfo.class);
         */
        AggregatedPage<SKUInfo> page = elasticsearchTemplate
                .queryForPage(
                        builder.build(),        //搜索条件封装
                        SKUInfo.class,         //结果集要转换的类型   执行搜索后将结果集封装到该对象中
                        new SearchResultMapper() {
                            @Override       //searchResponse：搜索后响应的数据/搜索后的结果集
                            public <T> AggregatedPage<T> mapResults(SearchResponse searchResponse, Class<T> aClass, Pageable pageable) {
                                //存储作用转换为高亮数据的对象
                                List<T> list = new ArrayList<>();

                                //执行查询，获取所有数据，结果集包含高亮数据和非高亮数据
                                for (SearchHit hit : searchResponse.getHits()) {
                                    //分析结果集数据，获取非高亮数据
                                    SKUInfo skuInfo = JSON.parseObject(hit.getSourceAsString(), SKUInfo.class);

                                    //分析结果集数据，获取指定域的高亮数据
                                    HighlightField highlightField = hit.getHighlightFields().get("name");

                                    if (highlightField != null && highlightField.getFragments()!=null) {
                                        //读取高亮数据
                                        Text[] fragments = highlightField.getFragments();
                                        StringBuffer buffer = new StringBuffer();
                                        for (Text fragment : fragments) {
                                            buffer.append(fragment.toString());
                                        }

                                        //非高亮数据中指定域替换为高亮数据
                                        skuInfo.setName(buffer.toString());
                                    }
                                    //将高亮数据添加到集合中
                                    list.add((T) skuInfo);
                                }

                                /**
                                 * 返回带有高亮的数据
                                 *  搜索的集合数据，带有高亮
                                 *  分页对象信息
                                 *  搜索记录的总条数
                                 */
                                return new AggregatedPageImpl<>(list,pageable,searchResponse.getHits().getTotalHits());
                            }
                        }
                );

        List<SKUInfo> content = page.getContent();      //搜索结果

        long totalElements = page.getTotalElements();   //总记录数

        int totalPages = page.getTotalPages();          //总页数

        //封装一个Map存储所有数据，并返回
        Map<String,Object> resultMap = new HashMap<>();
        resultMap.put("rows",content);
        resultMap.put("total",totalElements);
        resultMap.put("totalPages",totalPages);

        //获取搜索封装信息
        NativeSearchQuery build = builder.build();
        Pageable pageable = build.getPageable();        //获取分页信息
        int pageNumber = pageable.getPageNumber();//当前页码
        int pageSize = pageable.getPageSize();          //每页显示的条数

        //分页数据
        resultMap.put("pageNumber",pageNumber);
        resultMap.put("pageSize",pageSize);

        return resultMap;
    }

    @Override
    public void importSKUData() {
        //feign调用，查询所有sku信息
        List<Sku> skuList = skuFeign.findAll().getData();

        /**
         * 将List<Sku>转成List<SkuInfo>
         * skuList是一个sku对象的集合，转成JSON后是一个集合里面装着json字符串集合[{skuJSON1},{skuJSON2}]
         * JSON格式只是字符串，不存在类型引用，可以转成任意对象
         */
        List<SKUInfo> skuInfoList = JSON.parseArray(JSON.toJSONString(skuList), SKUInfo.class);

        for (SKUInfo skuInfo : skuInfoList) {
            //spec是json字符串，转成map类型
            Map<String,Object> map = JSON.parseObject(skuInfo.getSpec(), Map.class);
            /**
             * 如果需要生成动态的域，只需要将该域存入一个Map<String,Object>对象，该Map<String,Object>的key会生成一个域，
             * 域的名字就是map的key,Map的value会作为sku对象对应域(key)的值
             */
            skuInfo.setSpecMap(map);
        }

        //调用dao实现数据批量导入
        skuesMapper.saveAll(skuInfoList);
    }

}
