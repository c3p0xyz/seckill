package com.imooc.rabbitmq;

import com.imooc.entity.SeckillOrder;
import com.imooc.entity.SeckillUser;
import com.imooc.redis.RedisService;
import com.imooc.result.CodeMsg;
import com.imooc.result.Result;
import com.imooc.service.GoodsService;
import com.imooc.service.OrderService;
import com.imooc.service.SeckillService;
import com.imooc.vo.GoodsVo;
import lombok.extern.java.Log;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
@Log
public class MQReceiver {

    @Autowired
    private GoodsService goodsService;

    @Autowired
    private OrderService orderService;

    @Autowired
    private SeckillService seckillService;

    @RabbitListener(queues = MQConfig.SECKILL_QUEUE)
    public void receive(String message) {
        log.info("receive message"+message);
        SeckillMessage seckillMessage = RedisService.stringToBean(message, SeckillMessage.class);
        SeckillUser seckillUser = seckillMessage.getSeckillUser();
        long goodsId = seckillMessage.getGoodsId();

        // 判断库存
        GoodsVo goods = goodsService.getGoodsVoByGoodsId(goodsId);
        int stock = goods.getStockCount();
        if(stock <= 0) {
            return;
        }
        // 判断是否秒杀到了
        SeckillOrder seckillOrder = orderService.getSeckillOrderByUserIdGoodsId(seckillUser.getId(),goodsId);
        if (seckillOrder != null) {
            return;
        }
        // 减库存 下订单 写入秒杀订单
        seckillService.seckill(seckillUser,goods);
    }



    /*@RabbitListener(queues = MQConfig.QUEUE)
    public void receive(String message) {
        log.info("receive message: "+message);
    }

    @RabbitListener(queues = MQConfig.TOPIC_QUEUE1)
    public void receiveTopic1(String message) {
        log.info("topic_queue1 message: "+message);
    }

    @RabbitListener(queues = MQConfig.TOPIC_QUEUE2)
    public void receiveTopic2(String message) {
        log.info("topic_queue2 message: "+message);
    }

    @RabbitListener(queues = MQConfig.HEADERQUEUE)
    public void receiveHeaderQueue(byte[] message) {
        log.info("header_queue message: "+new String(message));
    }*/
}
