package com.imooc.utils;

import org.apache.commons.codec.digest.DigestUtils;

public class MD5Util {

    public static String md5(String str) {
        return DigestUtils.md5Hex(str);
    }

    private static final String salt = "1a2b3c4d";    // 固定盐值

    // 用户端 md5 第一次加密
    public static String inputPassToFormPass(String inputPass) {
        String str = "" + salt.charAt(0) + salt.charAt(2) + inputPass + salt.charAt(4) + salt.charAt(5);
        return md5(str);
    }

    // 服务端 md5 第二次加密
    public static String formPassToDBPass(String formPass, String salt) {
        String str = "" + salt.charAt(0) + salt.charAt(2) + formPass + salt.charAt(4) + salt.charAt(5);
        return md5(str);
    }


    // 复合加密方法
    public static String inputPassToDBPass(String inputPass, String dbSalt) {
        String formPass = inputPassToFormPass(inputPass);
        String dbPass = formPassToDBPass(formPass, dbSalt);
        return dbPass;
    }

    public static void main(String[] args) {
        String pass = inputPassToDBPass("123456", "1a2b3c4d");
//        String pass = inputPassToFormPass("123456");
        System.out.println(pass);
    }

}
