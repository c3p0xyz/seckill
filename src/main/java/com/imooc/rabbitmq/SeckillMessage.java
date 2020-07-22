package com.imooc.rabbitmq;

// 秒杀信息
import com.imooc.entity.SeckillUser;
import lombok.Data;

@Data
public class SeckillMessage {

    private SeckillUser seckillUser;

    private long goodsId;
}
