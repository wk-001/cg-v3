package constant;

/**
 * Redis key的前缀
 */
public interface RedisKeyConstant {
    /**
     * 广告
     */
    String Content = "content_";

    /**
     * 购物车
     */
    String Cart = "Cart_";

    /**
     * 秒杀订单商品排队状态
     */
    String UserQueueStatus = "UserQueueStatus";

    /**
     * 秒杀订单
     */
    String SeckillOrder = "SeckillOrder";

    /**
     * 商品库存队列
     */
    String SeckillGoodsCountList = "SeckillGoodsCountList";

    /**
     * 用户排队次数，避免用户重复提交
     */
    String UserQueueCount = "UserQueueCount";

    /**
     * 秒杀商品
     */
    String SeckillGoods = "SeckillGoods_";

    /**
     * 秒杀商品的队列
     */
    String SeckillOrderStatus = "SeckillOrderStatus";
}
