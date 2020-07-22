package com.imooc.result;

import lombok.Getter;

@Getter
public class CodeMsg {

    private int code;

    private String msg;

    private CodeMsg(int code, String msg) {
        this.code = code;
        this.msg = msg;
    }

    public static CodeMsg SUCCESS = new CodeMsg(200,"success");

    public static CodeMsg SERVICE_ERROR = new CodeMsg(500100,"服务端异常");

    public static CodeMsg BIND_ERROR = new CodeMsg(500101,"参数校验异常: %s");

    public static CodeMsg REQUEST_ILLEGAL = new CodeMsg(500102,"请求非法");

    public static CodeMsg ACCESS_LIMIT = new CodeMsg(500103,"访问太频繁");

    // 登录异常
    public static CodeMsg PASSWORD_EMPTY = new CodeMsg(500210,"登录密码不能为空");

    public static CodeMsg MOBILE_EMPTY = new CodeMsg(500211,"手机号不能为空");

    public static CodeMsg MOBILE_ERROR = new CodeMsg(500212,"手机号格式错误");

    public static CodeMsg MOBILE_NOTEXIST = new CodeMsg(500213,"手机号不存在");

    public static CodeMsg PASSWORD_ERROR = new CodeMsg(500214,"密码错误");

    public static CodeMsg SESSION_ERROR = new CodeMsg(500215,"session失效");

    // 秒杀模块
    public static CodeMsg SECKILL_OVER = new CodeMsg(500500,"商品已经秒杀完毕");

    public static CodeMsg REPEAT_SECKILL = new CodeMsg(500501,"不能重复秒杀");

    public static CodeMsg SECKILL_FAIL = new CodeMsg(500502,"秒杀失败");

    // 订单模块
    public static CodeMsg ORDER_NOT_EXIST = new CodeMsg(500400,"订单不存在");

    // 填充参数
    public CodeMsg fillArgs(Object...args) {
        int code = this.code;
        String message = String.format(this.msg,args);
        return new CodeMsg(code,message);
    }

}
