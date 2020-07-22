package com.imooc.access;

import com.imooc.entity.SeckillUser;

// 保存拦截器里用户信息
public class UserContext {

    /**
     *   threadLocal 绑定当前线程，线程安全
     */
    private static ThreadLocal<SeckillUser> userHolder = new ThreadLocal<>();

    public static void setSeckillUser(SeckillUser user) {
        userHolder.set(user);
    }

    public static SeckillUser getSeckillUser() {
        return userHolder.get();
    }
}
