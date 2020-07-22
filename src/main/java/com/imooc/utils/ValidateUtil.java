package com.imooc.utils;

import org.apache.commons.lang3.StringUtils;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class ValidateUtil {

    private static final Pattern mobile_pattern = Pattern.compile("[1]([3-9])[0-9]{9}");

    public static boolean isMobile(String str) {
        if (StringUtils.isEmpty(str)) {
            return false;
        }
        Matcher matcher = mobile_pattern.matcher(str);
        return matcher.matches();
    }

//    public static void main(String[] args) {
//        boolean b = isMobile("15580465540");
//        System.out.println(b);
//    }


}
