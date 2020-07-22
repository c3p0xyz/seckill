package com.imooc.entity;

import lombok.Data;

/**
 *   秒杀订单
 */

@Data
public class SeckillOrder {

    private Long id;

    private Long userId;

    private Long orderId;

    private Long goodsId;
}
