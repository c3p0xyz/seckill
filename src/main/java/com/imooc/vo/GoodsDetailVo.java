package com.imooc.vo;

import com.imooc.entity.SeckillUser;
import lombok.Data;

@Data
public class GoodsDetailVo {

    private GoodsVo goods;

    private SeckillUser seckillUser;

    private int seckillStatus = 0;

    private int remainSeconds = 0;


}
