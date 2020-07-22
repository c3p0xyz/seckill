package com.imooc.redis;

public abstract class BasePrefix implements KeyPrefix{

    private int expireSeconds;  // 过期时间

    private String prefix;      // 前缀

    public BasePrefix(int expireSeconds, String prefix) {
        this.expireSeconds = expireSeconds;
        this.prefix = prefix;
    }

    public BasePrefix(String prefix) {   // 0代表永不过期
        this(0,prefix);
    }

    public int expireSeconds() {
        return expireSeconds;
    }

    public String getPrefix() {
        String className = getClass().getSimpleName();
        return className + ":" + prefix;
    }

}
