package com.imooc.vo;

import com.imooc.entity.OrderInfo;
import lombok.Data;

@Data
public class OrderDetailVo {

    private GoodsVo goods;

    private OrderInfo orderInfo;
}
