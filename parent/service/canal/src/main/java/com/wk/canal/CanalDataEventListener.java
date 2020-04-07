package com.wk.canal;

import com.alibaba.otter.canal.protocol.CanalEntry;
import com.xpand.starter.canal.annotation.DeleteListenPoint;
import com.xpand.starter.canal.annotation.InsertListenPoint;
import com.xpand.starter.canal.annotation.ListenPoint;
import com.xpand.starter.canal.annotation.UpdateListenPoint;

/**
 * 使用canal实现MySQL数据的监听
 */
//@CanalEventListener
public class CanalDataEventListener {

    /**
     * ListenPoint 自定义监听
     * rowData.getAfterColumnsList();  获取操作后的数据，适用于获得增加、修改操作后的数据
     * rowData.getBeforeColumnsList();  获取操作前的数据，适用于获得删除、修改操作后的数据
     * @param eventType 当前操作的类型，例如增加数据
     * @param rowData   发生变更的一行数据
     */
    @ListenPoint(
            eventType = {CanalEntry.EventType.DELETE, CanalEntry.EventType.UPDATE},      //监听类型
            schema = {"changgou_content"},      //指定监听的数据库
            table = {"tb_content"},         //指定监听的表，如果不写默认监听指定数据库的所有表
            destination = "example"         //指定实例的地址
    )
    public void onEventCustom(CanalEntry.EventType eventType, CanalEntry.RowData rowData){
        //获取自定义操作前的数据，遍历操作后的每一列数据
        for (CanalEntry.Column column : rowData.getBeforeColumnsList()) {
            System.out.println("自定义操作前的列名： " + column.getName()+"------变更前的数据："+column.getValue());
        }
        //获取操作后的数据，遍历操作后的每一列数据
        for (CanalEntry.Column column : rowData.getAfterColumnsList()) {
            System.out.println("自定义操作后的列名： " + column.getName()+"------变更后的数据："+column.getValue());
        }

    }


    /**
     * InsertListenPoint 监听数据增加，增加后才有数据
     * rowData.getAfterColumnsList();  获取操作后的数据，适用于获得增加、修改操作后的数据
     * rowData.getBeforeColumnsList();  获取操作前的数据，适用于获得删除、修改操作后的数据
     * @param eventType 当前操作的类型，例如增加数据
     * @param rowData   发生变更的一行数据
     */
    @InsertListenPoint
    public void onEventInsert(CanalEntry.EventType eventType, CanalEntry.RowData rowData){
        //获取操作后的数据，遍历操作后的每一列数据
        for (CanalEntry.Column column : rowData.getAfterColumnsList()) {
            System.out.println("增加操作：列名： " + column.getName()+"------变更后的数据："+column.getValue());
        }

    }

    /**
     * UpdateListenPoint 监听数据修改，修改前后都有数据
     * rowData.getAfterColumnsList();  获取操作后的数据，适用于获得增加、修改操作后的数据
     * rowData.getBeforeColumnsList();  获取操作前的数据，适用于获得删除、修改操作后的数据
     * @param eventType 当前操作的类型，例如增加数据
     * @param rowData   发生变更的一行数据
     */
    @UpdateListenPoint
    public void onEventUpdate(CanalEntry.EventType eventType, CanalEntry.RowData rowData){
        //获取操作前的数据，遍历操作后的每一列数据
        for (CanalEntry.Column column : rowData.getBeforeColumnsList()) {
            System.out.println("修改操作：列名： " + column.getName()+"------变更前的数据："+column.getValue());
        }
        //获取操作后的数据，遍历操作后的每一列数据
        for (CanalEntry.Column column : rowData.getAfterColumnsList()) {
            System.out.println("修改操作：列名： " + column.getName()+"------变更后的数据："+column.getValue());
        }

    }

    /**
     * DeleteListenPoint 监听数据删除，删除前才有数据
     * rowData.getAfterColumnsList();  获取操作后的数据，适用于获得增加、修改操作后的数据
     * rowData.getBeforeColumnsList();  获取操作前的数据，适用于获得删除、修改操作后的数据
     * @param eventType 当前操作的类型，例如增加数据
     * @param rowData   发生变更的一行数据
     */
    @DeleteListenPoint
    public void onEventDelete(CanalEntry.EventType eventType, CanalEntry.RowData rowData){
        //获取操作前的数据，遍历操作后的每一列数据
        for (CanalEntry.Column column : rowData.getBeforeColumnsList()) {
            System.out.println("删除操作：列名： " + column.getName()+"------删除前的数据："+column.getValue());
        }

    }

}
