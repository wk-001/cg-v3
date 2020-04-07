package com.wk.canal;

import com.alibaba.otter.canal.protocol.CanalEntry;
import com.wk.canal.mq.queue.TopicQueue;
import com.wk.canal.mq.send.TopicMessageSender;
import com.xpand.starter.canal.annotation.CanalEventListener;
import com.xpand.starter.canal.annotation.ListenPoint;
import entity.Message;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * 自定义监听spu数据的变化，并发送到rabbitmq中
 */
@CanalEventListener
public class CanalSPUDataListener {

    @Autowired
    private TopicMessageSender topicMessageSender;

    /***
     * 自定义监听spu数据的变化，并发送到rabbitmq中
     * @param eventType
     * @param rowData
     */
    @ListenPoint(
            destination = "example",    //canal实例
            schema = "changgou_goods",  //指定要监听的数据库
            table = {"tb_spu"},         //指定要监听的表
            eventType = {
                    CanalEntry.EventType.UPDATE,
                    CanalEntry.EventType.DELETE
                }
            )
    public void onEventCustomSpu(CanalEntry.EventType eventType, CanalEntry.RowData rowData) {
        //操作数据的类型
        int number = eventType.getNumber();
        //获取删除/修改操作后的spuId
        String id = getColumn(eventType,rowData,"id");
        //将修改操作后的spuId封装Message
        Message message = new Message(number, id, TopicQueue.TOPIC_QUEUE_SPU, TopicQueue.TOPIC_EXCHANGE_SPU);
        //发送消息
        topicMessageSender.sendMessage(message);
    }

    /***
     * 获取某个列的值
     * @param rowData
     * @param name
     * @return
     */
    public String getColumn(CanalEntry.EventType eventType, CanalEntry.RowData rowData , String name){

        //如果是删除操作，获取操作之前的数据
        if(eventType == CanalEntry.EventType.DELETE) {
            //操作前的数据
            for (CanalEntry.Column column : rowData.getBeforeColumnsList()) {
                String columnName = column.getName();       //列名
                if (columnName.equalsIgnoreCase(name)) {
                    return column.getValue();
                }
            }
        }else if(eventType == CanalEntry.EventType.INSERT || eventType == CanalEntry.EventType.UPDATE){
            //操作后的数据
            for (CanalEntry.Column column : rowData.getAfterColumnsList()) {
                String columnName = column.getName();       //列名
                if(columnName.equalsIgnoreCase(name)){
                    return column.getValue();
                }
            }
        }
        return null;
    }
}
