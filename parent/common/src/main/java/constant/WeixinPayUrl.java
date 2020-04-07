package constant;

/**
 * 微信支付相关URL
 */
public interface WeixinPayUrl {

    /**
     * 微信服务器下单URL地址
     */
    String payUrl = "https://api.mch.weixin.qq.com/pay/unifiedorder";

    /**
     * 查询支付订单的URL
     */
    String queryUrl = "https://api.mch.weixin.qq.com/pay/orderquery";

    /**
     * 关闭订单支付的URL
     */
    String closeUrl = "https://api.mch.weixin.qq.com/pay/closeorder";
}
