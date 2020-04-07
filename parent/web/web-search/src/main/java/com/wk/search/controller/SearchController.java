package com.wk.search.controller;

import com.wk.search.entity.SKUInfo;
import com.wk.search.feign.SKUFeign;
import entity.Page;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.Map;

@Controller
@RequestMapping("search")
public class SearchController {

    @Autowired
    private SKUFeign skuFeign;

    @GetMapping("list")
    public String search(@RequestParam(required = false)Map<String,String> searchMap, Model model) {

        //替换特殊字符
        handlerSearchMap(searchMap);

        //调用搜索微服务
        Map<String,Object> resultMap = skuFeign.search(searchMap);
        model.addAttribute("result",resultMap);

        //存储搜索条件，用于页面数据回显
        model.addAttribute("searchMap",searchMap);

        //获取上次请求带有搜索条件的地址
        String[] url = getUrl(searchMap);
        model.addAttribute("url",url[0]);
        model.addAttribute("sortUrl",url[1]);

        //计算分页，total、pageNum和pageSize是从ES中获取的
        Page<SKUInfo> pageInfo = new Page<>(
                Long.parseLong(resultMap.get("total").toString()),              //总记录数
                Integer.parseInt(resultMap.get("pageNumber").toString())+1,       //当前页，首页从0开始，所以+1
                Integer.parseInt(resultMap.get("pageSize").toString())          //每页显示几条数据
        );
        model.addAttribute("pageInfo",pageInfo);

        return "search";
    }

    /**
     * 拼接组装用户请求的URL地址
     * 获取用户每次请求的地址，点击页面的搜索条件会在地址后面添加额外的条件
     * 只能对一个字段排序，如果第一次销量排序，第二次价格排序，需要去掉第一次销量排序的参数，
     * 重新以价格排序，同时保留之前除排序的搜索条件
     * @return
     */
    public String[] getUrl(Map<String,String> searchMap){
        StringBuilder defaultUrl = new StringBuilder("/search/list?");       //商品搜索的初始化URL
        String urls = "";
        StringBuilder sortUrl = new StringBuilder("/search/list?");          //排序的初始化URL
        String sortUrls = "";
        if (searchMap != null && searchMap.size()>0) {
            //defaultUrl.append("?");
            for (Map.Entry<String, String> entry : searchMap.entrySet()) {
                String key = entry.getKey();
                String value = entry.getValue();

                //分页操作在前台拼接完成，后台不需要分页参数，否则会追加到URL，显示多个分页参数。影响第二次分页的操作
                if(key.equalsIgnoreCase("pageNum")){
                    continue;
                }

                //key是搜索的条件 value是需要搜索的值
                defaultUrl.append(key+"="+value+"&");

                //如果请求中包含排序参数，直接跳过，只保留搜索条件。排序操作在前台完成
                if(key.equalsIgnoreCase("sortField")||key.equalsIgnoreCase("sortRule")){
                    continue;
                }
                sortUrl.append(key+"="+value+"&");
            }
            //去掉最后一个&
            urls = defaultUrl.substring(0, defaultUrl.length() - 1);
            sortUrls = sortUrl.substring(0, sortUrl.length() - 1);
        }
        return new String[]{urls,sortUrls};
    }


    /****
     * 替换特殊字符
     * @param searchMap
     */
    public void handlerSearchMap(Map<String,String> searchMap){
        if(searchMap!=null){
            for (Map.Entry<String, String> entry : searchMap.entrySet()) {
                if(entry.getKey().startsWith("spec_")){
                    entry.setValue(entry.getValue().replace("+","%2B"));
                }
            }
        }
    }

}
