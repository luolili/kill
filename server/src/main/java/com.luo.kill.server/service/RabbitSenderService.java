package com.luo.kill.server.service;

import org.springframework.amqp.core.MessageBuilder;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Service;

@Service
public class RabbitSenderService {

    @Autowired
    private RabbitTemplate rabbitTemplate;
    @Autowired
    private Environment env;

    /**
     * 秒杀成功，发送消息
     *
     * @param orderNo 订单号
     */
    public void sendKillSuccessEmailMsg(String orderNo) {
        try {
            rabbitTemplate.setMessageConverter(new Jackson2JsonMessageConverter());
            rabbitTemplate.setExchange(env.getProperty("mq.kill.item.success.email.exchange"));
            rabbitTemplate.setRoutingKey(env.getProperty("mq.kill.item.success.email.routing.key"));
            rabbitTemplate.convertAndSend(MessageBuilder.withBody(orderNo.getBytes()));

        } catch (Exception e) {

        }
    }
}
