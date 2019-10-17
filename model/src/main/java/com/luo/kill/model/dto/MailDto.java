package com.luo.kill.model.dto;

import lombok.Data;

import java.io.Serializable;

@Data
public class MailDto implements Serializable {

    private String subject;//主题
    private String context;//内容
    private String[] tos;//接收人
}
