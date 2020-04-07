package com.wk.search.mapper;

import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;
import com.wk.search.entity.SKUInfo;

/**
 * 导入对象是SKUInfo，SKUInfo的主键ID是Long类型
 */
public interface SKUESMapper extends ElasticsearchRepository<SKUInfo,Long> {
}