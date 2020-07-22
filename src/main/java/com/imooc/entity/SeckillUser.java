package com.imooc.entity;

import lombok.Data;

import java.util.Date;

/**
 *  秒杀用户表
 */

@Data
public class SeckillUser {

    private Long id;

    private String nickname;

    private String password;

    private String salt;

    private String head;

    private Date registerDate;  // 注册时间

    private Date lastLoginDate;  // 最后登录时间

    private Integer loginCount;  // 登录次数
}
