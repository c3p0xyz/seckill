package com.imooc.result;

import lombok.Getter;

@Getter
public class Result<T> {

    private int code;

    private String msg;

    private T data;

    private Result(T data) {
        this.code = 200;
        this.msg = "success";
        this.data = data;
    }

    private Result(CodeMsg codeMsg) {
        if (codeMsg == null) {
            return;
        }
        this.code = codeMsg.getCode();
        this.msg = codeMsg.getMsg();
    }

    public static <T>Result success(T data) {
        return new Result(data);
    }

    public static <T>Result error(CodeMsg codeMsg) {
        return new Result(codeMsg);
    }
}
