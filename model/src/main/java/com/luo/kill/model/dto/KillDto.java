package com.luo.kill.model.dto;

import lombok.Data;

import java.io.Serializable;

@Data
public class KillDto implements Serializable {
    private Integer killId;
    private Integer userId;
}
