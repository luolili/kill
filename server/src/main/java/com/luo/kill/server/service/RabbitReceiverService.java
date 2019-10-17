package com.luo.kill.server.service;

import com.luo.kill.model.dto.KillSuccessUserInfo;
import com.luo.kill.model.dto.MailDto;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.AutoConfigureOrder;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Service;

@Service
public class RabbitReceiverService {


    @Autowired
    private MailService mailService;
    @Autowired
    private Environment env;
    @RabbitListener(queues = {"mq.kill.item.success.email.queue"},
            containerFactory = "singleListenerContainer")
    public void receiveEmailMsg(KillSuccessUserInfo info) {
        try {
            System.out.println(info);
            //send email
            MailDto mailDto = new MailDto();
            mailDto.setSubject(env.getProperty("mail.kill.item.success.subject"));
            mailDto.setContext(env.getProperty("mail.kill.item.success.content"));
            mailDto.setTos(new String[]{info.getEmail()});
            mailService.sendSimpleEmail(mailDto);

        } catch (Exception e) {

        }

    }
}
