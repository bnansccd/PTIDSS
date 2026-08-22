package com.troy.sync.service.impl;

import com.alibaba.fastjson2.JSON;
import com.troy.common.core.constant.Constants;
import com.troy.common.core.enums.ResultEnum;
import com.troy.common.core.exception.ServiceException;
import com.troy.common.redis.service.RedisService;
import com.troy.sync.constants.RedisEnum;
import com.troy.sync.dao.TargetDao;
import com.troy.sync.domain.DTO.SyncDTO;
import com.troy.sync.domain.DTO.SyncScriptDTO;
import com.troy.sync.domain.DTO.SyncTargetDTO;
import com.troy.sync.entity.TargetEntity;
import com.troy.sync.service.SyncService;
import com.troy.sync.service.TargetService;
import lombok.extern.slf4j.Slf4j;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.concurrent.TimeUnit;

/**
 * @author chenxl
 * @description
 * @date 2024-09-06 9:53
 */
@Service
@Slf4j
public class TargetServiceImpl implements TargetService {

    @Autowired
    private TargetDao targetDao;

    @Autowired
    private RedisService redisService;

    @Autowired
    private RedissonClient redissonClient;

    @Autowired
    private SyncService syncService;

    @Override
    public void syncTarget(SyncTargetDTO dto) {
        TargetEntity target = targetDao.findByTarget(dto.getTarget());
        if (target == null){
            throw new ServiceException();
        }

        Integer times = target.getTimes();
        if (times != null){
            Integer time = redisService.getCacheObject(RedisEnum.SYNC_TARGET_KEY + dto.getTarget());
            if (time != null && time >= times){
                throw new ServiceException(ResultEnum.BE_CURRENT, RedisEnum.ERROR);
            }

            redisService.getRedisTemplate().opsForValue().increment(RedisEnum.SYNC_TARGET_KEY + dto.getTarget());
            redisService.expire(RedisEnum.SYNC_TARGET_KEY + dto.getTarget(), 24, TimeUnit.HOURS);
        }

        RLock lock = redissonClient.getLock(RedisEnum.SYNC_TARGET_KEY_LOCK + dto.getTarget());
        boolean success = true;
        try {
            success = lock.tryLock(0, 0, TimeUnit.SECONDS);
            if (success) {
                String expression = target.getExpression();
                if (Constants.ONE.equals(target.getType())){
                    if (target.getIsRpc()){
                        SyncDTO parsed = JSON.parseObject(expression, SyncDTO.class);
                        parsed.setType(dto.getType());
                        parsed.setBeginTime(dto.getStart());
                        parsed.setEndTime(dto.getEnd());
                        syncService.syncRpc(parsed);
                    } else {
                        SyncDTO parsed = JSON.parseObject(expression, SyncDTO.class);
                        parsed.setType(dto.getType());
                        parsed.setBeginTime(dto.getStart());
                        parsed.setEndTime(dto.getEnd());
                        syncService.sync(parsed);
                    }
                } else {
                    if (target.getIsRpc()){
                        SyncScriptDTO parsed = JSON.parseObject(expression, SyncScriptDTO.class);
                        parsed.setBeginTime(dto.getStart());
                        parsed.setEndTime(dto.getEnd());
                        syncService.syncScriptRpc(parsed);
                    } else {
                        SyncScriptDTO parsed = JSON.parseObject(expression, SyncScriptDTO.class);
                        parsed.setBeginTime(dto.getStart());
                        parsed.setEndTime(dto.getEnd());
                        syncService.syncScript(parsed);
                    }
                }
            } else {
                log.error(Thread.currentThread().getName() + "未能获取到锁，已放弃尝试");
            }
        } catch (InterruptedException e) {
            log.error("同步锁错误：{}", e.getMessage());
        } finally {
            // 判断当前线程是否持有锁
            if (success && lock.isHeldByCurrentThread()) {
                //释放当前锁
                lock.unlock();
                System.out.println(Thread.currentThread().getName() + "释放锁"+ LocalDateTime.now());
            }
        }
    }


}
