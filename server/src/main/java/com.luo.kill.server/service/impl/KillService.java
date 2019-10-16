package com.luo.kill.server.service.impl;

import com.luo.kill.model.entity.ItemKill;
import com.luo.kill.model.entity.ItemKillSuccess;
import com.luo.kill.model.mapper.ItemKillMapper;
import com.luo.kill.model.mapper.ItemKillSuccessMapper;
import com.luo.kill.server.service.RabbitSenderService;
import com.luo.kill.server.utils.RandomUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class KillService {

    @Autowired
    private ItemKillSuccessMapper itemKillSuccessMapper;
    @Autowired
    private ItemKillMapper itemKillMapper;
    @Autowired
    private RabbitSenderService rabbitSenderService;

    public boolean kill(Integer killId, Integer userId) {
        boolean result = false;
        if (itemKillSuccessMapper.countByKillUserId(killId, userId) <= 0) {
            ItemKill itemKill = itemKillMapper.selectById(killId);
            if (itemKill != null && itemKill.getCanKill() == 1) {
                int res = itemKillMapper.updateKillItem(killId);
                if (res > 0) {
                    commonRecordKillSuccessInfo(itemKill, userId);
                    result = true;
                }
            }

        }
        return result;
    }

    private boolean commonRecordKillSuccessInfo(ItemKill itemKill, Integer userId) {
        ItemKillSuccess entity = new ItemKillSuccess();
        //snow flake
        String orderCode = RandomUtil.generateOrderCode();
        entity.setCode(orderCode);
        entity.setItemId(itemKill.getItemId());
        entity.setKillId(itemKill.getItemId());
        entity.setUserId(userId.toString());

        int res = itemKillSuccessMapper.insertSelective(entity);

        if (res > 0) {
            rabbitSenderService.sendKillSuccessEmailMsg(orderCode);
        }
        return true;

    }

}
