package com.imooc.rabbitmq;

import org.springframework.amqp.core.*;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.HashMap;
import java.util.Map;

@Configuration
public class MQConfig {

    static final String SECKILL_QUEUE = "seckill.queue";
    static final String QUEUE = "queue";
    static final String HEADERQUEUE = "heads.queue";
    static final String TOPIC_QUEUE1 = "topic.queue1";
    static final String TOPIC_QUEUE2 = "topic.queue2";
    static final String TOPIC_EXCHANGE = "topicExchange";
    static final String FANOUT_EXCHANGE = "fanoutExchange";
    static final String HEADERS_EXCHANGE = "headersExchange";

    /**
     *   Direct 模式
     *
     */
    @Bean
    public Queue queue() {
        return new Queue(MQConfig.SECKILL_QUEUE,true);
    }


    /**
     *   topic 模式 交换机Exchange
     *
     */

    // 队列1
    /*@Bean
    public Queue topicQueue1() {
        return new Queue(TOPIC_QUEUE1,true);
    }
    // 队列2
    @Bean
    public Queue topicQueue2() {
        return new Queue(TOPIC_QUEUE2,true);
    }
    // 路由
    @Bean
    public TopicExchange topicExchange() {
        return new TopicExchange(TOPIC_EXCHANGE);
    }
    // 路由绑定队列1
    @Bean
    public Binding topicBinding1() {
        return BindingBuilder.bind(topicQueue1()).to(topicExchange()).with("topic.key1");
    }
    // 路由绑定队列2
    @Bean
    public Binding topicBinding2() {
        // *表示一个单词，#表示0个或多个单词
        return BindingBuilder.bind(topicQueue2()).to(topicExchange()).with("topic.#");
    }


    *//**
     *     fanout(广播模式)
     *
     *//*
    // 广播路由
    @Bean
    public FanoutExchange fanoutExchange() {
        return new FanoutExchange(FANOUT_EXCHANGE);
    }
    // 路由绑定队列1，没有rountkey
    @Bean
    public Binding fanoutBinding1() {
        return BindingBuilder.bind(topicQueue1()).to(fanoutExchange());
    }
    // 路由绑定队列2，没有rountkey
    @Bean
    public Binding fanoutBinding2() {
        return BindingBuilder.bind(topicQueue2()).to(fanoutExchange());
    }


    *//**
     *  header 模式
     *
     *//*
    @Bean
    public HeadersExchange headersExchange() {
        return new HeadersExchange(HEADERS_EXCHANGE);
    }
    // 新建 headsQueue
    @Bean
    public Queue headerQueue() {
        return new Queue(HEADERQUEUE,true);
    }
    // 绑定 headQueue
    @Bean
    public Binding headerBinding() {
        Map<String,Object> map = new HashMap<>();
        map.put("key1","liu");
        map.put("key2","zhang");

        return BindingBuilder.bind(headerQueue()).to(headersExchange()).whereAll(map).match();
    }*/

}
