package com.ywt.community.event;

import com.alibaba.fastjson.JSONObject;
import com.ywt.community.entity.Event;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;

/**
 * 事件生产者
 * @author yiwt
 * @Date 2022/5/18 15:43
 */
@Component
public class EventProducer {
    @Resource
    private KafkaTemplate<String, Object> kafkaTemplate;

    // 处理事件(发消息)
    public void fireEvent(Event event) {
        // 将事件发布到指定主题
        kafkaTemplate.send(event.getTopic(), JSONObject.toJSONString(event));
    }
}
