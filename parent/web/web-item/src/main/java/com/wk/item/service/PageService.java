package com.wk.item.service;

public interface PageService {

    /**
     * 通过spuId查询出的数据生成静态页
     * @param spuId
     */
    void createHtml(Long spuId);

}
