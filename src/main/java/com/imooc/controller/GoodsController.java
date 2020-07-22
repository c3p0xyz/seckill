package com.imooc.controller;

import com.imooc.entity.SeckillUser;
import com.imooc.redis.GoodsKey;
import com.imooc.redis.RedisService;
import com.imooc.result.Result;
import com.imooc.service.GoodsService;
import com.imooc.service.SeckillUserService;
import com.imooc.vo.GoodsDetailVo;
import com.imooc.vo.GoodsVo;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.thymeleaf.context.WebContext;
import org.thymeleaf.spring5.view.ThymeleafViewResolver;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.List;


@Controller
@RequestMapping("goods")
public class GoodsController {

    @Autowired
    private SeckillUserService seckillUserService;

    @Autowired
    private GoodsService goodsService;

    @Autowired
    private RedisService redisService;

    @Autowired
    private ThymeleafViewResolver resolver;

    // 商品列表
    @RequestMapping("/to_list")
    @ResponseBody
    public String toList(HttpServletRequest request, HttpServletResponse response, ModelMap map, SeckillUser user) {
        // 取缓存
        String html = redisService.get(GoodsKey.getGoodsList,"",String.class);
        if(!StringUtils.isEmpty(html)) {
            return html;
        }

        List<GoodsVo> goodsList = goodsService.listGoodsVo();
        map.addAttribute("goodsList",goodsList);

        // 手动渲染
        WebContext webContext = new WebContext(request,response,request.getServletContext(),request.getLocale(),
                map);

        html = resolver.getTemplateEngine().process("goods_list",webContext);
        if(!StringUtils.isEmpty(html)) {
            redisService.set(GoodsKey.getGoodsList,"",html);
        }

        return html;

//        return "goods_list";
    }

    // 商品详细信息
    @RequestMapping("to_detail/{goodsId}")
    @ResponseBody
    public String toDetail(HttpServletRequest request, HttpServletResponse response, ModelMap map,
                           SeckillUser user, @PathVariable("goodsId") Long goodsId) {
         // 取缓存
         String html = redisService.get(GoodsKey.getGoodsDetail,String.valueOf(goodsId),String.class);
         if(!StringUtils.isEmpty(html)) {
             return html;
         }

         map.addAttribute("user",user);
         GoodsVo goods = goodsService.getGoodsVoByGoodsId(goodsId);
         map.addAttribute("goods",goods);

         // 开始时间
         long startAt = goods.getStartDate().getTime();
         // 结束时间
         long endAt = goods.getEndDate().getTime();
         long now = System.currentTimeMillis();

         int seckillStatus = 0;   // 秒杀状态
         int remainSeconds = 0;    // 秒杀倒计时

         if (now < startAt) {  // 秒杀还未开始, 倒计时
             seckillStatus = 0;
             remainSeconds = (int)((startAt - now)/1000);
         } else if (now > endAt) {  // 秒杀结束
            seckillStatus = 2;
            remainSeconds = -1;
         } else {                   // 正在秒杀中
            seckillStatus = 1;
            remainSeconds = 0;
         }

         map.addAttribute("seckillStatus",seckillStatus);
         map.addAttribute("remainSeconds",remainSeconds);

         // 手动渲染
        WebContext webContext = new WebContext(request,response,request.getServletContext(),request.getLocale(),
                map);

        html = resolver.getTemplateEngine().process("goods_detail",webContext);
        if(!StringUtils.isEmpty(html)) {
            redisService.set(GoodsKey.getGoodsDetail,String.valueOf(goodsId),html);
        }

        return html;

//         return "goods_detail";
    }



    @RequestMapping("detail/{goodsId}")
    @ResponseBody
    public Result detail(HttpServletRequest request, HttpServletResponse response, ModelMap map,
                           SeckillUser user, @PathVariable("goodsId") Long goodsId) {

        GoodsVo goods = goodsService.getGoodsVoByGoodsId(goodsId);
        map.addAttribute("goods",goods);

        // 开始时间
        long startAt = goods.getStartDate().getTime();
        // 结束时间
        long endAt = goods.getEndDate().getTime();
        long now = System.currentTimeMillis();

        int seckillStatus = 0;   // 秒杀状态
        int remainSeconds = 0;    // 秒杀倒计时

        if (now < startAt) {  // 秒杀还未开始, 倒计时
            seckillStatus = 0;
            remainSeconds = (int)((startAt - now)/1000);
        } else if (now > endAt) {  // 秒杀结束
            seckillStatus = 2;
            remainSeconds = -1;
        } else {                   // 正在秒杀中
            seckillStatus = 1;
            remainSeconds = 0;
        }

        GoodsDetailVo goodsDetailVo = new GoodsDetailVo();
        goodsDetailVo.setGoods(goods);
        goodsDetailVo.setSeckillUser(user);
        goodsDetailVo.setRemainSeconds(remainSeconds);
        goodsDetailVo.setSeckillStatus(seckillStatus);

        return Result.success(goodsDetailVo);
    }
}
