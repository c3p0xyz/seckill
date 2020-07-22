package com.imooc.service;

import com.imooc.entity.OrderInfo;
import com.imooc.entity.SeckillOrder;
import com.imooc.entity.SeckillUser;
import com.imooc.redis.RedisService;
import com.imooc.redis.SeckillKey;
import com.imooc.utils.MD5Util;
import com.imooc.utils.UUIDUtil;
import com.imooc.vo.GoodsVo;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.script.ScriptEngine;
import javax.script.ScriptEngineManager;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.util.Random;

@Service
public class SeckillService {

    @Autowired
    GoodsService goodsService;

    @Autowired
    OrderService orderService;

    @Autowired
    RedisService redisService;

    // 减库存 下订单 写入秒杀订单
    @Transactional
    public OrderInfo seckill(SeckillUser user, GoodsVo goods) {

        // 减少库存
        boolean success = goodsService.reduceStock(goods);
        if(success) {
            // order_info, seckill_order  两张表创建订单
            return orderService.createOrder(user,goods);
        }else {
            setGoodsOver(goods.getId());
            return null;
        }


    }

    // 获取秒杀结果
    public long getSeckillResult(Long userId, Long goodsId) {

        SeckillOrder seckillOrder = orderService.getSeckillOrderByUserIdGoodsId(userId, goodsId);
        if(seckillOrder != null) { // 秒杀成功
            return seckillOrder.getOrderId();
        }else {
            boolean isOver = getGoodsOver(goodsId);
            if(isOver) {
                return -1;  // 商品卖完，秒杀结束
            }else {
                return 0;   // 排队中
            }
        }
    }

    private void setGoodsOver(Long goodsId) {
        redisService.set(SeckillKey.isGoodsOver,""+goodsId,true);
    }

    private boolean getGoodsOver(Long goodsId) {
        return redisService.exists(SeckillKey.isGoodsOver,""+goodsId);
    }

    public String createPath(SeckillUser user, Long goodsId) {
        String str = MD5Util.md5(UUIDUtil.uuid()+"123456");
        redisService.set(SeckillKey.getSeckillPath,""+user.getId()+"_"+goodsId,str);
        return str;
    }

    public boolean checkPath(SeckillUser user, Long goodsId, String path) {
        if(user == null || StringUtils.isEmpty(path)) {
            return false;
        }

        String path_old = redisService.get(SeckillKey.getSeckillPath,""+user.getId()+"_"+goodsId,String.class);
        return path.equals(path_old);
    }

    public BufferedImage createVerifyCode(SeckillUser user, Long goodsId) {
        if(user == null || goodsId<=0) {
            return null;
        }

        int width = 80;
        int height = 32;
        //create the image
        BufferedImage image = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
        Graphics g = image.getGraphics();
        // set the background color
        g.setColor(new Color(0xDCDCDC));
        g.fillRect(0, 0, width, height);
        // draw the border
        g.setColor(Color.black);
        g.drawRect(0, 0, width - 1, height - 1);
        // create a random instance to generate the codes
        Random rdm = new Random();
        // make some confusion
        for (int i = 0; i < 50; i++) {
            int x = rdm.nextInt(width);
            int y = rdm.nextInt(height);
            g.drawOval(x, y, 0, 0);
        }
        // generate a random code
        String verifyCode = generateVerifyCode(rdm);
        g.setColor(new Color(0, 100, 0));
        g.setFont(new Font("DejaVu Sans Mono", Font.BOLD, 24));
        g.drawString(verifyCode, 8, 24);
        g.dispose();
        //把验证码存到redis中
        int rnd = calc(verifyCode);
        redisService.set(SeckillKey.getVerifyCode, user.getId()+"_"+goodsId, rnd);
        //输出图片
        return image;
    }

    private int calc(String exp) {
        try {
            ScriptEngineManager manager = new ScriptEngineManager();
            ScriptEngine engine = manager.getEngineByName("JavaScript");
            return (Integer) engine.eval(exp);
        }catch (Exception e) {
            e.printStackTrace();
            return 0;
        }
    }

    private static char[] ops = {'+','-','*'};

    private String generateVerifyCode(Random rdm) {
        int num1 = rdm.nextInt(9)+1;
        int num2 = rdm.nextInt(9)+1;
        int num3 = rdm.nextInt(9)+1;
        char op1 = ops[rdm.nextInt(3)];
        char op2 = ops[rdm.nextInt(3)];

        String exp = ""+num1+op1+num2+op2+num3;
        return exp;

    }

    public boolean checkVerifyCode(SeckillUser user, Long goodsId, int verifyCode) {
        if(user == null || goodsId <= 0) {
            return false;
        }
        int verifyCode_old = redisService.get(SeckillKey.getVerifyCode, user.getId()+"_"+goodsId,Integer.class);
        return verifyCode_old-verifyCode == 0;

    }
}
