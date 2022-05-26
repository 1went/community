package com.ywt.community.controller;

import com.ywt.community.entity.Comment;
import com.ywt.community.entity.DiscussPost;
import com.ywt.community.entity.Event;
import com.ywt.community.event.EventProducer;
import com.ywt.community.service.CommentService;
import com.ywt.community.service.DiscussPostService;
import com.ywt.community.util.CommunityConstant;
import com.ywt.community.util.HostHolder;
import com.ywt.community.util.RedisKeyUtil;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import javax.annotation.Resource;
import java.util.Date;

/**
 * @author yiwt
 * @Date 2022/5/13 14:35
 */
@Controller
@RequestMapping("/comment")
public class CommentController implements CommunityConstant {
    @Resource
    private EventProducer eventProducer;

    @Resource
    private CommentService commentService;

    @Resource
    private DiscussPostService discussPostService;

    @Resource
    private HostHolder hostHolder;

    @Resource
    private RedisTemplate<String, Object> redisTemplate;

    @PostMapping("/add/{discussPostId}")
    public String addComment(@PathVariable("discussPostId")Integer discussPostId, Comment comment) {
        comment.setUserId(hostHolder.getUser().getId());
        comment.setStatus(0);
        comment.setCreateTime(new Date());
        commentService.addComment(comment);

        // 评论之后，将这个事件通知给相应的用户
        Event event = new Event()
                .setTopic(TOPIC_COMMENT)
                .setUserId(hostHolder.getUser().getId())
                .setEntityType(comment.getEntityType())
                .setEntityId(comment.getEntityId())
                .setData("postId", discussPostId);
        if (comment.getEntityType() == ENTITY_TYPE_POST) {  // 如果是帖子，就要查帖子
            DiscussPost target = discussPostService.findDiscussPostById(comment.getEntityId());
            event.setEntityUserId(target.getUserId());
        } else if (comment.getEntityType() == ENTITY_TYPE_COMMENT) {  // 如果是评论，查评论
            Comment target = commentService.findCommentById(comment.getEntityId());
            event.setEntityUserId(target.getUserId());
        }
        // 触发事件
        eventProducer.fireEvent(event);

        // 评论同步到es
        if (comment.getEntityType() == ENTITY_TYPE_POST) {
            event = new Event()
                    .setTopic(TOPIC_PUBLISH)
                    .setUserId(comment.getUserId())
                    .setEntityType(ENTITY_TYPE_POST)
                    .setEntityId(discussPostId);
            eventProducer.fireEvent(event);
            String key = RedisKeyUtil.getPostScoreKey();
            redisTemplate.opsForSet().add(key, discussPostId);
        }

        return "redirect:/discuss/detail/" + discussPostId;
    }
}
