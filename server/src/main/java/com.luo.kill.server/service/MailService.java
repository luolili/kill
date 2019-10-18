package com.luo.kill.server.service;

import com.luo.kill.model.dto.MailDto;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.stereotype.Service;

import javax.mail.internet.MimeMessage;
import java.util.Date;

@Service
@EnableAsync
public class MailService {

    @Autowired
    private JavaMailSender mailSender;
    @Autowired
    private Environment env;

    //发送简单文本
    @Async
    public void sendSimpleEmail(final MailDto mailDto) {
        try {
            SimpleMailMessage simpleMailMessage = new SimpleMailMessage();
            simpleMailMessage.setFrom(env.getProperty("mail.send.from"));
            simpleMailMessage.setTo(mailDto.getTos());
            simpleMailMessage.setText(mailDto.getContext());
            simpleMailMessage.setSentDate(new Date());
            simpleMailMessage.setSubject(mailDto.getSubject());
            mailSender.send(simpleMailMessage);
        } catch (Exception e) {

        }
    }

    @Async
    public void sendHtmlEmail(final MailDto mailDto) {
        try {
            MimeMessage mimeMessage = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(mimeMessage, true, "utf-8");

            helper.setFrom(env.getProperty("mail.send.from"));
            helper.setTo(mailDto.getTos());
            helper.setText(mailDto.getContext(), true);
            helper.setSentDate(new Date());
            helper.setSubject(mailDto.getSubject());
            mailSender.send(mimeMessage);
        } catch (Exception e) {

        }
    }
}
