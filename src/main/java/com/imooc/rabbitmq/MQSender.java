package com.imooc.rabbitmq;

import com.imooc.redis.RedisService;
import lombok.extern.java.Log;
import org.springframework.amqp.core.AmqpTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
@Log
public class MQSender {

    @Autowired
    private AmqpTemplate amqpTemplate;

    public void sendSeckillMessage(SeckillMessage message) {
        String msg = RedisService.beanToString(message);
        log.info("send seckill message"+msg);

        amqpTemplate.convertAndSend(MQConfig.SECKILL_QUEUE,msg);

    }

    /*public void send(Object message) {
        String msg = RedisService.beanToString(message);
        log.info("send message: "+msg);

        amqpTemplate.convertAndSend(MQConfig.QUEUE,msg);
    }

    public void sendTopic(Object message) {
        String msg = RedisService.beanToString(message);
        log.info("send topic message: "+msg);

        amqpTemplate.convertAndSend(MQConfig.TOPIC_EXCHANGE,"topic.key1",msg+"1");
        amqpTemplate.convertAndSend(MQConfig.TOPIC_EXCHANGE,"topic.key2",msg+"2");
    }

    public void sendFanout(Object message) {
        String msg = RedisService.beanToString(message);
        log.info("send fanout message");

        amqpTemplate.convertAndSend(MQConfig.FANOUT_EXCHANGE,"",msg);
    }

    public void sendHeader(Object message) {
        String msg = RedisService.beanToString(message);
        log.info("send header message");

        MessageProperties messageProperties = new MessageProperties();
        messageProperties.setHeader("key1","liu");
        messageProperties.setHeader("key2","zhang");
        Message obj = new Message(msg.getBytes(),messageProperties);

        amqpTemplate.convertAndSend(MQConfig.HEADERS_EXCHANGE,"",obj);
    }*/


}
