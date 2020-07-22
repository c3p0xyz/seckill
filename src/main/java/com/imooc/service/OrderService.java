package com.imooc.service;

import com.imooc.dao.OrderDao;
import com.imooc.entity.OrderInfo;
import com.imooc.entity.SeckillOrder;
import com.imooc.entity.SeckillUser;
import com.imooc.redis.OrderKey;
import com.imooc.redis.RedisService;
import com.imooc.vo.GoodsVo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;

@Service
public class OrderService {

    @Autowired
    private OrderDao orderDao;

    @Autowired
    private RedisService redisService;

    public SeckillOrder getSeckillOrderByUserIdGoodsId(Long userId, Long goodsId) {
        //return orderDao.getSeckillOrderByUserIdGoodsId(userId,goodsId);
        // 不从数据库查，直接从 redis 查
        return redisService.get(OrderKey.getSeckillOrderByUidGid,""+userId+"_"+goodsId,SeckillOrder.class);
    }

    // 创建订单
    @Transactional
    public OrderInfo createOrder(SeckillUser user, GoodsVo goods) {
        OrderInfo orderInfo = new OrderInfo();

        orderInfo.setCreateDate(new Date());
        orderInfo.setDeliveryAddrId(0L);
        orderInfo.setGoodsCount(1);
        orderInfo.setGoodsId(goods.getId());
        orderInfo.setGoodsName(goods.getGoodsName());
        orderInfo.setGoodsPrice(goods.getSeckillPrice());
        orderInfo.setOrderChannel(3);
        orderInfo.setStatus(0);

        orderDao.insert(orderInfo);
        SeckillOrder seckillOrder = new SeckillOrder();
        seckillOrder.setGoodsId(goods.getId());
        seckillOrder.setOrderId(orderInfo.getId());   // 尤其注意
        seckillOrder.setUserId(user.getId());

        orderDao.insertSeckillOrder(seckillOrder);
        // 将秒杀订单放入 redis 中
        redisService.set(OrderKey.getSeckillOrderByUidGid,""+user.getId()+"_"+goods.getId(),seckillOrder);

        return orderInfo;
    }

    public OrderInfo getOrderById(long orderId) {

        return orderDao.getOrderById(orderId);
    }
}
