package com.wk.user.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.wk.user.entity.Content;
import com.wk.user.mapper.ContentMapper;
import com.wk.user.service.ContentService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ContentServiceImpl extends ServiceImpl<ContentMapper, Content> implements ContentService {

    @Override
    public List<Content> findByCategory(Long categoryId) {
        Content content = new Content();
        content.setCategoryId(categoryId);
        content.setStatus("1");
        return getBaseMapper().selectList(new QueryWrapper<>(content));
    }
}
