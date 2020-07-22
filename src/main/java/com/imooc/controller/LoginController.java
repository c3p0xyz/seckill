package com.imooc.controller;

import com.imooc.result.Result;
import com.imooc.service.SeckillUserService;
import com.imooc.vo.LoginVo;
import lombok.extern.java.Log;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;

@Controller
@RequestMapping("login")
@Log
public class LoginController {

    @Autowired
    private SeckillUserService seckillUserService;

    @RequestMapping("to_login")
    public String toLogin() {
        return "login";
    }

    @RequestMapping("do_login")
    @ResponseBody
    public Result doLogin(HttpServletResponse response, @Valid LoginVo loginVo) {
        log.info(loginVo.toString());

        // 登录
        String token = seckillUserService.login(response,loginVo);
        return Result.success(token);
    }
}
