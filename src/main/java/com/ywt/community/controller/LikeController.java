package com.ywt.community.controller;

import com.ywt.community.entity.Event;
import com.ywt.community.entity.User;
import com.ywt.community.event.EventProducer;
import com.ywt.community.service.LikeService;
import com.ywt.community.util.CommunityConstant;
import com.ywt.community.util.CommunityUtil;
import com.ywt.community.util.HostHolder;
import com.ywt.community.util.RedisKeyUtil;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.annotation.Resource;
import java.util.HashMap;
import java.util.Map;

/**
 * @author yiwt
 * @Date 2022/5/15 14:17
 */
@Controller
public class LikeController implements CommunityConstant {
    @Resource
    private EventProducer eventProducer;

    @Resource
    private LikeService likeService;

    @Resource
    private HostHolder hostHolder;

    @Resource
    private RedisTemplate<String, Object> redisTemplate;

    @PostMapping("/like")
    @ResponseBody
    public String like(int entityType, int entityId, int entityUserId, int postId) {
        User currentUser = hostHolder.getUser();
        // 点赞
        likeService.like(currentUser.getId(), entityType, entityId, entityUserId);
        // 点赞数量
        long likeCount = likeService.findEntityLikeCount(entityType, entityId);
        // 点赞状态
        int likeStatus = likeService.findEntityLikeStatus(currentUser.getId(), entityType, entityId);
        Map<String, Object> map = new HashMap<>();
        map.put("likeCount", likeCount);
        map.put("likeStatus", likeStatus);

        // 发送点赞事件消息通知
        if (likeStatus == 1) {  // 点赞时发送通知，否则不发送
            Event event = new Event()
                    .setTopic(TOPIC_LIKE)
                    .setUserId(hostHolder.getUser().getId())
                    .setEntityType(entityType)
                    .setEntityId(entityId)
                    .setEntityUserId(entityUserId)
                    .setData("postId", postId);
            eventProducer.fireEvent(event);
        }
        if (entityType == ENTITY_TYPE_POST) {
            // 计算帖子分数
            String key = RedisKeyUtil.getPostScoreKey();
            redisTemplate.opsForSet().add(key, postId);
        }

        return CommunityUtil.getJSONString(0, null, map);
    }
}
