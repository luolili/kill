package com.luo.kill.model.dto;

import com.luo.kill.model.entity.ItemKillSuccess;
import lombok.Data;

import java.io.Serializable;

@Data
public class KillSuccessUserInfo extends ItemKillSuccess implements Serializable {

    private String userName;
    private String phone;
    private String email;
    private String itemName;
}
