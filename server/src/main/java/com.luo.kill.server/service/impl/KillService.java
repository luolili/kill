package com.luo.kill.server.service.impl;

import com.luo.kill.model.entity.ItemKill;
import com.luo.kill.model.entity.ItemKillSuccess;
import com.luo.kill.model.mapper.ItemKillSuccessMapper;
import com.luo.kill.server.utils.RandomUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class KillService {

    @Autowired
    private ItemKillSuccessMapper itemKillSuccessMapper;

    public void kill(Integer killId, Integer userId) {
        ItemKillSuccess entity = new ItemKillSuccess();
        //snow flake
        entity.setCode(RandomUtil.generateOrderCode());
        //entity.setItemId(itemKill.getItemId());
        entity.setKillId(killId);
        entity.setUserId(userId.toString());


        int res = itemKillSuccessMapper.insertSelective(entity);

        if (res > 0) {

        }


    }

}
