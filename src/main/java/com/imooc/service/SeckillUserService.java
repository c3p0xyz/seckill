package com.imooc.service;

import com.imooc.dao.SeckillUserDao;
import com.imooc.entity.SeckillUser;
import com.imooc.exception.GlobalException;
import com.imooc.redis.RedisService;
import com.imooc.redis.SeckillUserKey;
import com.imooc.result.CodeMsg;
import com.imooc.utils.MD5Util;
import com.imooc.utils.UUIDUtil;
import com.imooc.vo.LoginVo;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletResponse;

@Service
public class SeckillUserService {

    public static final String COOKIE_NAME_TOKEN = "token";

    @Autowired
    private SeckillUserDao seckillUserDao;

    @Autowired
    private RedisService redisService;

    public SeckillUser getById(long id) {
        // 取缓存
        SeckillUser user = redisService.get(SeckillUserKey.getById,""+id,SeckillUser.class);
        if(user != null) {
            return user;
        }
        // 取数据库
        user = seckillUserDao.getById(id);
        if(user != null) {
            redisService.set(SeckillUserKey.getById,""+id,user);
        }
        return user;
    }

    public boolean updatePassword(String token,long id,String inputPass) {
        SeckillUser user = getById(id);
        if(user == null) {
          throw new GlobalException(CodeMsg.MOBILE_NOTEXIST);
        }
        SeckillUser toBeUpdate = new SeckillUser();
        toBeUpdate.setId(id);
        toBeUpdate.setPassword(MD5Util.inputPassToDBPass(inputPass,user.getSalt()));
        seckillUserDao.update(toBeUpdate);

        // 处理缓存
        redisService.delete(SeckillUserKey.getById,""+id);
        user.setPassword(toBeUpdate.getPassword());
        redisService.set(SeckillUserKey.token,token,user);

        return true;
    }



    public String login(HttpServletResponse response, LoginVo loginVo) {
        if (loginVo == null) {
            throw new GlobalException(CodeMsg.SERVICE_ERROR);
        }
        // 判断手机号是否存在
        String mobile = loginVo.getMobile();
        String formPass = loginVo.getPassword();

        SeckillUser user = getById(Long.parseLong(mobile));
        if (user == null) {
            throw new GlobalException(CodeMsg.MOBILE_NOTEXIST);
        }

        // 验证密码
        String dbSalt = user.getSalt();
        String dbPass = user.getPassword();

        String calcPass = MD5Util.formPassToDBPass(formPass, dbSalt);
        if (!calcPass.equals(dbPass)) {
            throw new GlobalException(CodeMsg.PASSWORD_ERROR);
        }

        // 生成 cookie
        String token = UUIDUtil.uuid();
        addCookie(response,token,user);
        return token;

    }

    public SeckillUser getByToken(HttpServletResponse response, String token) {
        if (StringUtils.isEmpty(token)) {
            return null;
        }

        // 延长有效期
        SeckillUser user = redisService.get(SeckillUserKey.token,token,SeckillUser.class);
        if (user != null) {
            addCookie(response,token,user);
        }
        return user;
    }

    private void addCookie(HttpServletResponse response, String token, SeckillUser user) {

        redisService.set(SeckillUserKey.token,token,user);
        Cookie cookie = new Cookie(COOKIE_NAME_TOKEN,token);
        cookie.setMaxAge(SeckillUserKey.token.expireSeconds());
        cookie.setPath("/");

        response.addCookie(cookie);
    }
}
