package com.luo.kill.server.service.impl;

import com.luo.kill.model.entity.ItemKill;
import com.luo.kill.model.entity.ItemKillSuccess;
import com.luo.kill.model.mapper.ItemKillMapper;
import com.luo.kill.model.mapper.ItemKillSuccessMapper;
import com.luo.kill.server.service.RabbitSenderService;
import com.luo.kill.server.utils.RandomUtil;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.stereotype.Service;

import java.util.concurrent.TimeUnit;

@Service
public class KillService {

    @Autowired
    private ItemKillSuccessMapper itemKillSuccessMapper;
    @Autowired
    private ItemKillMapper itemKillMapper;
    @Autowired
    private RabbitSenderService rabbitSenderService;
    @Autowired
    private StringRedisTemplate stringRedisTemplate;

    @Autowired
    private RedissonClient redissonClient;


    //库存超卖

    /**
     * 数据库优化：在查询和更新的时候 判断 库存是否大于0
     * killId 和 userId 构成唯一索引
     * <p>
     * redis 分布式锁
     *
     * @param killId killId
     * @param userId userId
     * @return 是否秒杀成功
     */
    public boolean kill(Integer killId, Integer userId) {
        boolean result = false;
        if (itemKillSuccessMapper.countByKillUserId(killId, userId) <= 0) {
            ItemKill itemKill = itemKillMapper.selectById(killId);
            if (itemKill != null && itemKill.getCanKill() == 1) {
                int res = itemKillMapper.updateKillItem(killId);
                //同一个 user 抢购一次，缺点：高并发的时候，需要不断的访问mysql
                if (itemKillSuccessMapper.countByKillUserId(killId, userId) <= 0) {
                    if (res > 0) {
                        //先读后写：thread-safe 问题
                        commonRecordKillSuccessInfo(itemKill, userId);
                        result = true;
                    }
                }
            }

        }
        return result;
    }

    public boolean killV2(Integer killId, Integer userId) throws Exception {
        boolean result = false;
        if (itemKillSuccessMapper.countByKillUserId(killId, userId) <= 0) {

            //redis 原子操作
            ValueOperations<String, String> ops = stringRedisTemplate.opsForValue();
            final String key = new StringBuffer().append(killId).append("-")
                    .append(userId).toString();
            final String value = RandomUtil.generateOrderCode();
            Boolean cacheResult = ops.setIfAbsent(key, value);
            //redis 挂掉了，会出现锁死
            if (cacheResult) {//会出现锁死
                stringRedisTemplate.expire(key, 30, TimeUnit.MILLISECONDS);
                try {
                    ItemKill itemKill = itemKillMapper.selectByIdV2(killId);
                    if (itemKill != null && itemKill.getCanKill() == 1) {
                        int res = itemKillMapper.updateKillItem(killId);
                        //同一个 user 抢购一次，缺点：高并发的时候，需要不断的访问mysql
                        if (itemKillSuccessMapper.countByKillUserId(killId, userId) <= 0) {
                            if (res > 0) {
                                //先读后写：thread-safe 问题
                                commonRecordKillSuccessInfo(itemKill, userId);
                                result = true;
                            }
                        }
                    }
                } catch (Exception e) {
                    throw new Exception("--redis");
                } finally {
                    //释放的锁 是之前获得的锁
                    if (value.equals(ops.get(key).toString())) {
                        stringRedisTemplate.delete(key);

                    }
                }
            }


        }
        return result;
    }

    public boolean killV3(Integer killId, Integer userId) {
        boolean result = false;
        final String lockKey = new StringBuffer().append(killId).append("-")
                .append(userId).toString();
        RLock lock = redissonClient.getLock(lockKey);

        try {

            boolean cacheRes = lock.tryLock(100, 10, TimeUnit.MILLISECONDS);
            if (cacheRes) {
                //核心业务
                if (itemKillSuccessMapper.countByKillUserId(killId, userId) <= 0) {
                    ItemKill itemKill = itemKillMapper.selectById(killId);
                    if (itemKill != null && itemKill.getCanKill() == 1) {
                        int res = itemKillMapper.updateKillItem(killId);
                        //同一个 user 抢购一次，缺点：高并发的时候，需要不断的访问mysql
                        if (itemKillSuccessMapper.countByKillUserId(killId, userId) <= 0) {
                            if (res > 0) {
                                //先读后写：thread-safe 问题
                                commonRecordKillSuccessInfo(itemKill, userId);
                                result = true;
                            }
                        }
                    }

                }
            }


        } catch (Exception e) {

        } finally {
            lock.unlock();
            // lock.forceUnlock();//force 强制解锁
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
            // 入死信队列
            rabbitSenderService.sendKillSuccessExpireMsg(orderCode);
        }
        return true;

    }

}
