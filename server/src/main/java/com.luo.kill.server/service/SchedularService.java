package com.luo.kill.server.service;

import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

@Service
public class SchedularService {

    @Scheduled(cron = "10 * * * * *　？")
    public void closeOrder() {
        //查询每个订单的diffTime: 当前时间和下订单的时间差
        //判断diffTime 是否大于30分钟，是，改变订单的状态
    }
}
