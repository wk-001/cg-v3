package entity;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

import java.io.Serializable;
import java.util.Date;

/**
 * 用户排队抢单信息封装
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
public class SeckillStatus implements Serializable {

    //用户名
    private String username;

    //秒杀订单创建时间
    private Date createTime;

    //秒杀状态  1:排队中，2:秒杀等待支付（已下单）,3:支付超时，4:秒杀失败,5:支付完成
    private Integer status;

    //秒杀的商品ID，用于订单库存回滚
    private Long goodsId;

    //应付金额
    private Float money;

    //订单号
    private Long orderId;

    //时间段
    private String time;

    public SeckillStatus() {
    }

    public SeckillStatus(String username, Date createTime, Integer status, Long goodsId, String time) {
        this.username = username;
        this.createTime = createTime;
        this.status = status;
        this.goodsId = goodsId;
        this.time = time;
    }

}
