package com.wk.canal;

import com.alibaba.fastjson.JSON;
import com.alibaba.otter.canal.protocol.CanalEntry;
import com.wk.user.entity.Content;
import com.wk.user.feign.ContentFeign;
import com.xpand.starter.canal.annotation.CanalEventListener;
import com.xpand.starter.canal.annotation.ListenPoint;
import constant.RedisKeyConstant;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;

import java.util.List;

/**
 * 自定义监听广告数据的变化，并同步到Redis中
 */
@CanalEventListener
public class CanalContentDataListener {

    @Autowired
    private ContentFeign contentFeign;

    @Autowired
    private StringRedisTemplate redisTemplate;

    /**
     * 自定义监听数据库的操作
     */
    @ListenPoint(
            eventType = {CanalEntry.EventType.INSERT,
                        CanalEntry.EventType.DELETE,
                        CanalEntry.EventType.UPDATE},
            schema = {"changgou_content"},
            table = {"tb_content"},
            destination = "example"
    )
    public void onEventCustom(CanalEntry.EventType eventType, CanalEntry.RowData rowData){
        //获取列名为category_id的值
        String categoryId = getColumnValue(eventType, rowData);
        //调用feign获取该分类下的所有广告集合
        List<Content> contentList;
        if (categoryId != null) {
            contentList = contentFeign.findByCategory(Long.parseLong(categoryId)).getData();

            if(contentList.size() > 0){
                //如果集合数据不为空 使用redisTemplate存储到redis中
                redisTemplate.boundValueOps(RedisKeyConstant.Content+categoryId).set(JSON.toJSONString(contentList));
            }else {
                //如果集合为空 删除Redis中对应的key
                redisTemplate.delete(RedisKeyConstant.Content+categoryId);
            }
        }

    }

    private String getColumnValue(CanalEntry.EventType eventType, CanalEntry.RowData rowData){
        //如果是删除操作，获取操作之前的数据
        if(eventType == CanalEntry.EventType.DELETE){
            for (CanalEntry.Column column : rowData.getBeforeColumnsList()) {
                if("category_id".equalsIgnoreCase(column.getName())){
                    return column.getValue();
                }
            }
        }else if(eventType == CanalEntry.EventType.INSERT || eventType == CanalEntry.EventType.UPDATE){
            //添加、修订操作获取操作之后的数据
            for (CanalEntry.Column column : rowData.getAfterColumnsList()) {
                if("category_id".equalsIgnoreCase(column.getName())){
                    return column.getValue();
                }
            }
        }
        return null;
    }
}
