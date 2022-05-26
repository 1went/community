package com.ywt.community.service.impl;

import com.ywt.community.service.DataService;
import com.ywt.community.util.RedisKeyUtil;
import org.springframework.dao.DataAccessException;
import org.springframework.data.redis.connection.RedisConnection;
import org.springframework.data.redis.connection.RedisStringCommands;
import org.springframework.data.redis.core.RedisCallback;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

/**
 * @author yiwt
 * @Date 2022/5/21 14:20
 */
@Service
public class DataServiceImpl implements DataService {
    @Resource
    private RedisTemplate<String, Object> redisTemplate;

    private DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyyMMdd");

    // 将指定ip计入UV(独立访客)
    @Override
    public void recordUV(String ip) {
        String key = RedisKeyUtil.getUvKey(formatter.format(LocalDate.now()));
        redisTemplate.opsForHyperLogLog().add(key, ip);
    }

    @Override
    public long calculateUV(LocalDate start, LocalDate end) {
        if (start == null || end == null) {
            throw new IllegalArgumentException("参数不能为空");
        }
        // 得到该日期范围内的key
        List<String> list = new ArrayList<>();
        LocalDate tmp = start;
        while (!tmp.isAfter(end)) {
            String key = RedisKeyUtil.getUvKey(formatter.format(tmp));
            list.add(key);
            tmp = tmp.plusDays(1);
        }
        // 合并
        String resultKey = RedisKeyUtil.getUvKey(formatter.format(start), formatter.format(end));
        redisTemplate.opsForHyperLogLog().union(resultKey, list.toArray(new String[0]));
        return redisTemplate.opsForHyperLogLog().size(resultKey);
    }

    // 将指定用户计入DAU(日活跃用户量)
    @Override
    public void recordDAU(int userId) {
        String key = RedisKeyUtil.getDauKey(formatter.format(LocalDate.now()));
        redisTemplate.opsForValue().setBit(key, userId, true);
    }

    @Override
    public long calculateDAU(LocalDate start, LocalDate end) {
        if (start == null || end == null) {
            throw new IllegalArgumentException("参数不能为空");
        }
        // 得到该日期范围内的key
        List<byte[]> list = new ArrayList<>();
        LocalDate tmp = start;
        while (!tmp.isAfter(end)) {
            String key = RedisKeyUtil.getDauKey(formatter.format(tmp));
            list.add(key.getBytes());
            tmp = tmp.plusDays(1);
        }

        // 进行or运算
        return (long) redisTemplate.execute(new RedisCallback<Object>() {
            @Override
            public Object doInRedis(RedisConnection connection) throws DataAccessException {
                String resultKey = RedisKeyUtil.getDauKey(formatter.format(start), formatter.format(end));
                connection.bitOp(RedisStringCommands.BitOperation.OR, resultKey.getBytes(), list.toArray(new byte[0][0]));
                return connection.bitCount(resultKey.getBytes());
            }
        });
    }
}
