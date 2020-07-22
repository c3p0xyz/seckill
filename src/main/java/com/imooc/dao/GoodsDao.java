package com.imooc.dao;

import com.imooc.entity.SeckillGoods;
import com.imooc.vo.GoodsVo;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;
import org.springframework.stereotype.Repository;

import java.util.List;

@Mapper
@Repository
public interface GoodsDao {

    @Select("select g.*,sg.seckill_price,sg.stock_count,sg.start_date,sg.end_date from seckill_goods sg " +
            "left join goods g on sg.goods_id = g.id")
    public List<GoodsVo> listGoodsVo();


    @Select("select g.*,sg.seckill_price,sg.stock_count,sg.start_date,sg.end_date from seckill_goods sg " +
            "left join goods g on sg.goods_id = g.id where sg.goods_id = #{goodsId}")
    public GoodsVo getGoodsVoByGoodsId(@Param("goodsId")Long goodsId);

    // and stock_count > 0 是为了防止库存出现负数
    @Update("update seckill_goods set stock_count = stock_count-1 where goods_id = #{goodsId} and stock_count > 0")
    int reduceStock(SeckillGoods g);
}
