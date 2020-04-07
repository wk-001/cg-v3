package com.wk.seckill.timer;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.wk.seckill.entity.SeckillGoods;
import com.wk.seckill.mapper.SeckillGoodsMapper;
import constant.RedisKeyConstant;
import entity.DateUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.Date;
import java.util.List;
import java.util.Set;

/**
 * 定时将秒杀商品存入到Redis缓存
 */
@Component
public class SeckillGoodsPushTask {

    @Autowired
    private SeckillGoodsMapper goodsMapper;

    @Autowired
    private RedisTemplate redisTemplate;

    /**
     * @Scheduled(cron = "0/10 * * * * ?") 从每分钟的0秒开始执行，每过10秒中执行一次该方法
     *
     * 秒杀商品入库要求：
     *  符合当前时间的秒杀商品菜单
     *  审核通过，status=1
     *  秒杀商品库存>0
     *  开始时间<=当前时间  9点开始 10点可以抢购 10点之后开始的还不能抢购
     *  结束时间>当前时间   11点结束 10点可以抢购 无法参与10点之前结束的秒杀活动
     *      获取整个时间菜单，共5个时间段，每个时间段2个小时
     *      确定每个时间菜单的区间值（2个小时一次秒杀）
     *      根据菜单时间的区间值获取对应的秒杀商品数据
     *      将对应时间段内的秒杀商品数据存入到Redis
     */
    @Scheduled(cron = "0/10 * * * * ?")
    public void loadGoodsPushRedis(){

        //时间菜单 2个小时为一个阶段，获取当前时间及以后的5个时间段
        for (Date dateMenu : DateUtil.getDateMenus()) {
            //时间段转字符串
            String time = RedisKeyConstant.SeckillGoods+DateUtil.data2str(dateMenu,DateUtil.PATTERN_YYYYMMDDHH);

            QueryWrapper<SeckillGoods> wrapper = new QueryWrapper<SeckillGoods>()
                    .eq("status","1")       //审核通过
                    .gt("stock_count",0)     //库存大于0
                    .ge("start_time",dateMenu)   //开始时间大于等于当前时间对应时间段的开始时间
                    .lt("end_time",DateUtil.addDateHour(dateMenu,2));   //结束时间小于当前时间2小时后时间段的结束时间

            /**
             * 排除已经存入Redis中的seckillGoods
             *  获取当前命名空间下所有商品的ID(key)
             *  每次查询排除掉之前存在的商品信息
             */
            Set keys = redisTemplate.boundHashOps(time).keys();
            if (keys != null && keys.size()>0) {
                //id not in(存储在Redis中对应时间段的所有数据)
                wrapper.notIn("id",keys);
            }

            //查询出对应时间段的秒杀商品信息
            List<SeckillGoods> seckillGoods = goodsMapper.selectList(wrapper);

            for (SeckillGoods seckillGood : seckillGoods) {
                //把商品信息放入Redis中，数据结构是hash，namespace是对应时间段，key是商品ID，value是对应商品
                redisTemplate.boundHashOps(time).put(seckillGood.getId(),seckillGood);

                //将商品库存数量信息放在Redis中操作，解决多线程情况下库存减少的并发问题
                redisTemplate.boundHashOps(RedisKeyConstant.SeckillGoodsCountList).increment(seckillGood.getId(),seckillGood.getStockCount());
            }
        }
    }

}
