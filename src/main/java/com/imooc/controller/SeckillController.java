package com.imooc.controller;

import com.imooc.access.AccessLimit;
import com.imooc.entity.OrderInfo;
import com.imooc.entity.SeckillOrder;
import com.imooc.entity.SeckillUser;
import com.imooc.rabbitmq.MQSender;
import com.imooc.rabbitmq.SeckillMessage;
import com.imooc.redis.AccessKey;
import com.imooc.redis.GoodsKey;
import com.imooc.redis.RedisService;
import com.imooc.redis.SeckillKey;
import com.imooc.result.CodeMsg;
import com.imooc.result.Result;
import com.imooc.service.GoodsService;
import com.imooc.service.OrderService;
import com.imooc.service.SeckillService;
import com.imooc.utils.MD5Util;
import com.imooc.utils.UUIDUtil;
import com.imooc.vo.GoodsVo;
import org.apache.ibatis.annotations.Param;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.*;

import javax.imageio.ImageIO;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.awt.image.BufferedImage;
import java.io.OutputStream;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 *   实现秒杀功能
 */

@Controller
@RequestMapping("/seckill")
public class SeckillController implements InitializingBean {

    @Autowired
    private GoodsService goodsService;

    @Autowired
    private OrderService orderService;

    @Autowired
    private SeckillService seckillService;

    @Autowired
    private RedisService redisService;

    @Autowired
    private MQSender sender;

    // 内存标记，减少 redis 访问
    private Map<Long,Boolean> localOverMap = new HashMap<>();

    // 系统初始化
    @Override
    public void afterPropertiesSet() throws Exception {
        List<GoodsVo> goodsVoList = goodsService.listGoodsVo();
        if(goodsVoList == null) {
            return;
        }
        for(GoodsVo goodsVo : goodsVoList) {
            redisService.set(GoodsKey.getSeckillGoodsStock,""+goodsVo.getId(),goodsVo.getStockCount());
            localOverMap.put(goodsVo.getId(),false);
        }
    }

    @RequestMapping(value = "/{path}/do_seckill",method = RequestMethod.POST)
    @ResponseBody
    public Result doSeckill(ModelMap map, SeckillUser user, @RequestParam("goodsId") Long goodsId,
                            @PathVariable("path") String path) {
        map.addAttribute("user",user);
        if (user == null) {
            return Result.error(CodeMsg.SESSION_ERROR);
        }

        boolean over = localOverMap.get(goodsId);
        if(over) {
            return Result.error(CodeMsg.SECKILL_OVER);
        }

        boolean check = seckillService.checkPath(user,goodsId,path);
        if(!check) {
            return Result.error(CodeMsg.REQUEST_ILLEGAL);
        }

        // 预减库存
        long stock = redisService.decr(GoodsKey.getSeckillGoodsStock,""+goodsId);
        if(stock < 0) {
            localOverMap.put(goodsId,true);
            return Result.error(CodeMsg.SECKILL_OVER);
        }
        // 判断是否秒杀到了
        SeckillOrder seckillOrder = orderService.getSeckillOrderByUserIdGoodsId(user.getId(),goodsId);
        if (seckillOrder != null) {
            return Result.error((CodeMsg.REPEAT_SECKILL));
        }
        // 入队
        SeckillMessage message = new SeckillMessage();
        message.setSeckillUser(user);
        message.setGoodsId(goodsId);

        sender.sendSeckillMessage(message);

        return Result.success(0);   // 排队中...


       /* // 判断库存
        GoodsVo goods = goodsService.getGoodsVoByGoodsId(goodsId);
        int stock = goods.getStockCount();
        if (stock <= 0) {
            return Result.error(CodeMsg.SECKILL_OVER);
        }

        // 判断是否秒杀到了
        SeckillOrder seckillOrder = orderService.getSeckillOrderByUserIdGoodsId(user.getId(),goodsId);
        if (seckillOrder != null) {
            return Result.error((CodeMsg.REPEAT_SECKILL));
        }

        // 减库存 下订单 写入秒杀订单 (需要放到一个事务中)
        OrderInfo orderInfo = seckillService.seckill(user, goods);
        return Result.success(orderInfo);*/
    }

    /**
     *  orderId  成功
     *  -1       秒杀失败
     *  0        排队中
     *
     */
    @RequestMapping(value = "result",method = RequestMethod.GET)
    @ResponseBody
    public Result seckillResult(ModelMap map, SeckillUser user, @RequestParam("goodsId") Long goodsId) {
        map.addAttribute("user", user);
        if (user == null) {
            return Result.error(CodeMsg.SESSION_ERROR);
        }

        long ret = seckillService.getSeckillResult(user.getId(), goodsId);
        return Result.success(ret);

    }


    @AccessLimit(seconds = 5,maxCount = 5)
    @RequestMapping(value = "path",method = RequestMethod.GET)
    @ResponseBody
    public Result path(HttpServletRequest request, SeckillUser user, @RequestParam("goodsId") Long goodsId,
                       @RequestParam(value = "verifyCode",defaultValue = "0") int verifyCode) {
        if (user == null) {
            return Result.error(CodeMsg.SESSION_ERROR);
        }

        /*
            查询访问的次数
            此功能被 @AccessLimit 注解所取代
        String uri = request.getRequestURI();
        String key = uri+"_"+user.getId();
        Integer count = redisService.get(AccessKey.access,key,Integer.class);
        if(count == null) {
            redisService.set(AccessKey.access,key,1);
        }else if(count < 5) {
            redisService.incr(AccessKey.access,key);
        }else {
            return Result.error(CodeMsg.ACCESS_LIMIT);
        }
        */


        boolean check = seckillService.checkVerifyCode(user,goodsId,verifyCode);

        if(!check) {
            return Result.error(CodeMsg.REQUEST_ILLEGAL);
        }

        String path = seckillService.createPath(user,goodsId);

        return Result.success(path);
    }


    @RequestMapping(value = "verifyCode",method = RequestMethod.GET)
    @ResponseBody
    public Result getVerifyCode(HttpServletResponse response, SeckillUser user, @RequestParam("goodsId") Long goodsId) {
        if (user == null) {
            return Result.error(CodeMsg.SESSION_ERROR);
        }

        BufferedImage bufferedImage = seckillService.createVerifyCode(user,goodsId);
        try {
            OutputStream out = response.getOutputStream();
            ImageIO.write(bufferedImage,"JPEG",out);
            out.flush();
            out.close();
            return null;
        }catch (Exception e) {
            e.printStackTrace();
            return Result.error(CodeMsg.SECKILL_FAIL);
        }
    }

}
