package com.imooc.entity;

import lombok.Data;

import java.util.Date;

@Data
public class OrderInfo {

    private Long id;

    private Long userId;

    private Long goodsId;

    private Long deliveryAddrId;

    private String goodsName;

    private Integer goodsCount;

    private Double goodsPrice;

    private Integer orderChannel;  // 订单来源

    private Integer status;

    private Date createDate;

    private Date payDate;
}
