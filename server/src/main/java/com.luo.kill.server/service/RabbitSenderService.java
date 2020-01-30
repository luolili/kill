package com.luo.kill.server.service;

import com.luo.kill.model.dto.KillSuccessUserInfo;
import com.luo.kill.model.mapper.ItemKillSuccessMapper;
import org.apache.commons.lang3.StringUtils;
import org.springframework.amqp.AmqpException;
import org.springframework.amqp.core.*;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.support.converter.AbstractJavaTypeMapper;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;

@Service
public class RabbitSenderService {

    @Autowired
    private RabbitTemplate rabbitTemplate;
    @Autowired
    private Environment env;
    @Resource
    private ItemKillSuccessMapper itemKillSuccessMapper;
    /**
     * 秒杀成功，发送消息
     *传输的应该是订单信息 ：KillSuccessUserInfo
     * @param orderNo 订单号
     */
    public void sendKillSuccessEmailMsg(String orderNo) {
        try {
            if (StringUtils.isNotEmpty(orderNo)) {
                KillSuccessUserInfo info = itemKillSuccessMapper.selectByCode(orderNo);
                if (info != null) {

                    rabbitTemplate.setMessageConverter(new Jackson2JsonMessageConverter());
                    rabbitTemplate.setExchange(env.getProperty("mq.kill.item.success.email.exchange"));
                    rabbitTemplate.setRoutingKey(env.getProperty("mq.kill.item.success.email.routing.key"));
                    //rabbitTemplate.convertAndSend(MessageBuilder.withBody(orderNo.getBytes()));

                    rabbitTemplate.convertAndSend(info, new MessagePostProcessor() {
                        @Override
                        public Message postProcessMessage(Message message) throws AmqpException {
                            MessageProperties messageProperties = message.getMessageProperties();
                            messageProperties.setDeliveryMode(MessageDeliveryMode.PERSISTENT);
                            messageProperties.setHeader(AbstractJavaTypeMapper.DEFAULT_KEY_CLASSID_FIELD_NAME,
                                    KillSuccessUserInfo.class);

                            return message;
                        }
                    });
                }
            }

        } catch (Exception e) {

        }
    }

    //秒杀成功之后，发送消息到dead queue
    public void sendKillSuccessExpireMsg(String orderCode) {
        try {
            if (StringUtils.isNoneBlank(orderCode)) {
                KillSuccessUserInfo info = itemKillSuccessMapper.selectByCode(orderCode);
                if (info != null) {
                    rabbitTemplate.setMessageConverter(new Jackson2JsonMessageConverter());
                    rabbitTemplate.setExchange(env.getProperty("mq.kill.item.success.kill.dead.prod.exchange"));
                    rabbitTemplate.setRoutingKey(env.getProperty("\"mq.kill.item.success.kill.dead.routing.key"));
                    rabbitTemplate.convertAndSend(info, new MessagePostProcessor() {
                        @Override
                        public Message postProcessMessage(Message message) throws AmqpException {
                            MessageProperties mp = message.getMessageProperties();
                            mp.setDeliveryMode(MessageDeliveryMode.PERSISTENT);
                            mp.setHeader(AbstractJavaTypeMapper.DEFAULT_KEY_CLASSID_FIELD_NAME,
                                    KillSuccessUserInfo.class);

                            // 动态设置 ttl
                            mp.setExpiration(env.getProperty("mq.kill.item.success.kill.expire"));
                            return message;
                        }
                    });

                }
            }
        } catch (Exception e) {

        }

    }

}
