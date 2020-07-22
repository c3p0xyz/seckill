package com.imooc.controller;

import com.imooc.entity.SeckillUser;
import com.imooc.rabbitmq.MQSender;
import com.imooc.result.Result;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller
@RequestMapping("/demo")
public class SimpleController {

    @Autowired
    private MQSender mqSender;

    @RequestMapping("/mq")
    @ResponseBody
    public Result info() {
        String message = "hello world";
//        mqSender.send(message);

        return Result.success(message);
    }

    @RequestMapping("/mq/topic")
    @ResponseBody
    public Result topic() {
        String message = "hello world";
//        mqSender.sendTopic(message);

        return Result.success(message);
    }

    @RequestMapping("/mq/fanout")
    @ResponseBody
    public Result fanout() {
        String message = "hello world";
//        mqSender.sendFanout(message);

        return Result.success(message);
    }

    @RequestMapping("/mq/header")
    @ResponseBody
    public Result header() {
        String message = "hello world";
//        mqSender.sendHeader(message);

        return Result.success(message);
    }
}
