package com.wk.search.service;

import java.util.Map;

public interface SKUService {

    /**
     * 导入sku数据到ES索引库
     */
    void importSKUData();

    Map<String,Object> search(Map<String, String> searchMap);
}
