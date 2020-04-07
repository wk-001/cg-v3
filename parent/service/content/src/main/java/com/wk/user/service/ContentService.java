package com.wk.user.service;

import com.wk.user.entity.Content;
import com.baomidou.mybatisplus.extension.service.IService;

import java.util.List;

public interface ContentService extends IService<Content> {

    /***
     * 根据categoryId查询广告集合
     * @param id
     * @return
     */
    List<Content> findByCategory(Long categoryId);

}
