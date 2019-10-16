package com.luo.kill.server.service;

import com.luo.kill.model.dto.KillSuccessUserInfo;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Service;

@Service
public class RabbitReceiverService {


    @RabbitListener(queues = {"mq.kill.item.success.email.queue"},
            containerFactory = "singleListenerContainer")
    public void receiveEmailMsg(KillSuccessUserInfo info) {
        try {
            System.out.println(info);
            //send email

        } catch (Exception e) {

        }

    }
}
