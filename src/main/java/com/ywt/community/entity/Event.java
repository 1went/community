package com.ywt.community.entity;

import lombok.Getter;

import java.util.HashMap;
import java.util.Map;

/**
 * 封装事件的实体类
 * @author yiwt
 * @Date 2022/5/18 15:36
 */
@Getter
public class Event {
    private String topic;     // 事件主题（点赞、评论、关注、发帖）
    private int userId;       // 触发该事件的人
    private int entityType;   // 触发的事件是哪一种 实体类型
    private int entityId;     // 该实体类型的id
    private int entityUserId; // 实体所属的用户
    private Map<String, Object> data = new HashMap<>();  // 其它数据

    public Event setTopic(String topic) {
        this.topic = topic;
        return this;
    }

    public Event setUserId(int userId) {
        this.userId = userId;
        return this;
    }

    public Event setEntityType(int entityType) {
        this.entityType = entityType;
        return this;
    }

    public Event setEntityId(int entityId) {
        this.entityId = entityId;
        return this;
    }

    public Event setEntityUserId(int entityUserId) {
        this.entityUserId = entityUserId;
        return this;
    }

    public Event setData(String key, Object value) {
        this.data.put(key, value);
        return this;
    }
}
