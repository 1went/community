package com.ywt.community.event;

import com.alibaba.fastjson.JSONObject;
import com.ywt.community.entity.DiscussPost;
import com.ywt.community.entity.Event;
import com.ywt.community.entity.Message;
import com.ywt.community.service.DiscussPostService;
import com.ywt.community.service.ElasticSearchService;
import com.ywt.community.service.MessageService;
import com.ywt.community.util.CommunityConstant;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

/**
 * 事件消费者
 * @author yiwt
 * @Date 2022/5/18 15:45
 */
@Slf4j
@Component
public class EventConsumer implements CommunityConstant {
    @Resource
    private MessageService messageService;
    @Resource
    private DiscussPostService discussPostService;
    @Resource
    private ElasticSearchService elasticSearchService;

    // 消费评论、点赞、关注事件
    @KafkaListener(topics = {TOPIC_COMMENT, TOPIC_LIKE, TOPIC_FOLLOW})
    public void handleMessage(ConsumerRecord record) {
        Event event = generalJudgment(record);
        if (event == null) {
            return;
        }
        // 封装成Message存入数据库
        Message message = new Message();
        message.setFromId(SYSTEM_USER_ID);
        message.setToId(event.getEntityUserId());
        message.setConversationId(event.getTopic());
        message.setCreateTime(new Date());
        // message的内容拼接
        Map<String, Object> content = new HashMap<>();
        content.put("userId", event.getUserId());
        content.put("entityType", event.getEntityType());
        content.put("entityId", event.getEntityId());
        if (!event.getData().isEmpty()) {
            for (Map.Entry<String, Object> entry : event.getData().entrySet()) {
                content.put(entry.getKey(), entry.getValue());
            }
        }
        message.setContent(JSONObject.toJSONString(content));
        messageService.addMessage(message);
    }

    // 消费发帖事件
    @KafkaListener(topics = {TOPIC_PUBLISH})
    public void handlePublishMessage(ConsumerRecord record) {
        Event event = generalJudgment(record);
        if (event == null) {
            return;
        }
        DiscussPost post = discussPostService.findDiscussPostById(event.getEntityId());
        elasticSearchService.saveDiscussPost(post);
    }

    // 消费删帖事件
    @KafkaListener(topics = {TOPIC_DELETE})
    public void handleDeleteMessage(ConsumerRecord record) {
        Event event = generalJudgment(record);
        if (event == null) {
            return;
        }
        elasticSearchService.deleteDiscussPost(event.getEntityId());
    }

    // 一般性判断
    private Event generalJudgment(ConsumerRecord record) {
        if (record == null || record.value() == null) {
            log.error("消息内容为空");
            return null;
        }
        // 拿到生产者发送的数据
        Event event = JSONObject.parseObject(record.value().toString(), Event.class);
        if (event == null) {
            log.error("消息格式错误");
            return null;
        }
        return event;
    }
}
