package com.imooc.dao;

import com.imooc.entity.OrderInfo;
import com.imooc.entity.SeckillOrder;
import org.apache.ibatis.annotations.*;
import org.springframework.stereotype.Repository;

@Mapper
@Repository
public interface OrderDao {

    @Select("select * from seckill_order where user_id = #{userId} and goods_id = #{goodsId}")
    public SeckillOrder getSeckillOrderByUserIdGoodsId(@Param("userId") Long userId, @Param("goodsId") Long goodsId);

    // 新增信息，并拿到新增信息主表的主键
    @Insert("insert into order_info(create_date,delivery_addr_id,goods_count,goods_id,goods_name,goods_price," +
            "order_channel,status) values(#{createDate},#{deliveryAddrId},#{goodsCount},#{goodsId},#{goodsName}," +
            "#{goodsPrice},#{orderChannel},#{status})")
    @SelectKey(keyColumn = "id",keyProperty = "id",resultType = Long.class,before = false,statement = "select last_insert_id()")
    long insert(OrderInfo orderInfo);

    @Insert("insert into seckill_order(goods_id,order_id,user_id) values(#{goodsId},#{orderId},#{userId})")
    int insertSeckillOrder(SeckillOrder seckillOrder);

    @Select("select * from order_info where id=#{orderId}")
    OrderInfo getOrderById(@Param("orderId") long orderId);
}
