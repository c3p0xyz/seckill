package com.imooc.access;

import com.alibaba.fastjson.JSON;
import com.imooc.entity.SeckillUser;
import com.imooc.redis.AccessKey;
import com.imooc.redis.RedisService;
import com.imooc.result.CodeMsg;
import com.imooc.result.Result;
import com.imooc.service.SeckillUserService;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.handler.HandlerInterceptorAdapter;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.OutputStream;

@Service
public class AccessInterceptor extends HandlerInterceptorAdapter {

    @Autowired
    private SeckillUserService seckillUserService;

    @Autowired
    private RedisService redisService;

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response,
                             Object handler) throws Exception {
        if(handler instanceof HandlerMethod) {
            SeckillUser seckillUser = getUser(request, response);
            UserContext.setSeckillUser(seckillUser);

            HandlerMethod handlerMethod = (HandlerMethod) handler;
            AccessLimit accessLimit = handlerMethod.getMethodAnnotation(AccessLimit.class);
            if(accessLimit == null) {
                return true;
            }
            int seconds = accessLimit.seconds();
            int maxCount = accessLimit.maxCount();
            boolean needLogin = accessLimit.needLogin();
            if(needLogin) {
                if(seckillUser == null) {
                    render(response,CodeMsg.SESSION_ERROR);
                    return false;
                }
            }
            String uri = request.getRequestURI();
            String key = uri+"_"+seckillUser.getId();

            AccessKey ak = AccessKey.withExpire(seconds);
            Integer count = redisService.get(ak,key,Integer.class);
            if(count == null) {
                redisService.set(ak,key,1);
            }else if(count < maxCount) {
                redisService.incr(ak,key);
            }else {
                render(response,CodeMsg.ACCESS_LIMIT);
                return false;
            }

        }
        return true;
    }

    private void render(HttpServletResponse response,CodeMsg codeMsg) throws Exception{
        response.setContentType("application/json;charset=utf-8");
//        response.setContentType("text/html;charset=utf-8");
        OutputStream out = response.getOutputStream();
        String jsonString = JSON.toJSONString(Result.error(codeMsg));
        out.write(jsonString.getBytes("UTF-8"));
        out.flush();
        out.close();
    }

    private SeckillUser getUser(HttpServletRequest request,HttpServletResponse response) {
        String paramToken = request.getParameter(SeckillUserService.COOKIE_NAME_TOKEN);
        String cookieToken = getCookieValue(request, SeckillUserService.COOKIE_NAME_TOKEN);

        if (StringUtils.isEmpty(paramToken) && StringUtils.isEmpty(cookieToken)) {
            return null;
        }

        String token = StringUtils.isEmpty(paramToken) ? cookieToken : paramToken;
        return seckillUserService.getByToken(response, token);
    }

    private String getCookieValue(HttpServletRequest request, String cookieNameToken) {
        Cookie[] cookies = request.getCookies();
        if (cookies == null || cookies.length <= 0) {
            return null;
        }
        for (Cookie cookie : cookies) {
            if (cookie.getName().equals(cookieNameToken)) {
                return cookie.getValue();
            }
        }
        return null;
    }
}
