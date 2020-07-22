package com.imooc.controller;

import com.imooc.entity.SeckillUser;
import com.imooc.result.Result;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller
@RequestMapping("user")
public class UserController {

    @RequestMapping("/info")
    @ResponseBody
    public Result info(SeckillUser user) {
        return Result.success(user);
    }
}
