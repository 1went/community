package com.ywt.community.controller;

import com.ywt.community.entity.*;
import com.ywt.community.event.EventProducer;
import com.ywt.community.service.CommentService;
import com.ywt.community.service.DiscussPostService;
import com.ywt.community.service.LikeService;
import com.ywt.community.service.UserService;
import com.ywt.community.util.CommunityConstant;
import com.ywt.community.util.CommunityUtil;
import com.ywt.community.util.HostHolder;
import com.ywt.community.util.RedisKeyUtil;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.util.*;

/**
 * @author yiwt
 * @Date 2022/5/12 11:09
 */
@Controller
@RequestMapping("/discuss")
public class DiscussPostController implements CommunityConstant {
    @Resource
    private DiscussPostService discussPostService;

    @Resource
    private UserService userService;

    @Resource
    private CommentService commentService;

    @Resource
    private LikeService likeService;

    @Resource
    private EventProducer eventProducer;

    @Resource
    private HostHolder hostHolder;

    @Resource
    private RedisTemplate<String, Object> redisTemplate;

    // 发帖
    @PostMapping("/add")
    @ResponseBody
    public String addDiscussPost(String title, String content) {
        User user = hostHolder.getUser();
        if (user == null) {
            return CommunityUtil.getJSONString(403, "你还没有登录");
        }
        DiscussPost post = new DiscussPost();
        post.setUserId(user.getId());
        post.setTitle(title);
        post.setContent(content);
        post.setCreateTime(new Date());
        discussPostService.addDiscussPost(post);

        // 触发事件，将新发贴存入es
        triggerEventByPost(user.getId(), post.getId());

        // 加入redis待计算帖子分数
        String key = RedisKeyUtil.getPostScoreKey();
        redisTemplate.opsForSet().add(key, post.getId());

        return CommunityUtil.getJSONString(0, "发布成功");
    }

    // 帖子详情
    @GetMapping("/detail/{id}")
    public String getDiscussPostDetail(@PathVariable("id") Integer id, Model model, MyPage myPage) {
        // 查帖子
        DiscussPost discussPost = discussPostService.findDiscussPostById(id);
        model.addAttribute("post", discussPost);
        // 查帖子的作者
        model.addAttribute("user", userService.findUserById(discussPost.getUserId()));
        // 查帖子的点赞数量
        long likeCount = likeService.findEntityLikeCount(ENTITY_TYPE_POST, id);
        model.addAttribute("likeCount", likeCount);
        // 查帖子的点赞状态(用户没登录时，状态返回0)
        int likeStatus = hostHolder.getUser() == null ? 0 :
                likeService.findEntityLikeStatus(hostHolder.getUser().getId(), ENTITY_TYPE_POST, id);
        model.addAttribute("likeStatus", likeStatus);
        // 评论的分页信息
        myPage.setLimit(5);
        myPage.setPath("/discuss/detail/" + id);
        myPage.setRows(discussPost.getCommentCount());  // 直接从帖子表里获取该帖子对应的评论数

        // 查询给帖子的评论
        List<Comment> commentList = commentService.findCommentByEntity(
                ENTITY_TYPE_POST, discussPost.getId(), myPage.getCurrent(), myPage.getLimit());
        // 帖子的评论的显示列表
        List<Map<String, Object>> commentViewList = new ArrayList<>();
        if (commentList != null) {
            for (Comment comment : commentList) {
                Map<String, Object> commentMap = new HashMap<>();
                //帖子的评论
                commentMap.put("comment", comment);
                // 评论的作者
                commentMap.put("user", userService.findUserById(comment.getUserId()));
                // 帖子的点赞数量
                likeCount = likeService.findEntityLikeCount(ENTITY_TYPE_COMMENT, comment.getId());
                commentMap.put("likeCount", likeCount);
                // 帖子的点赞状态(用户没登录时，状态返回0)
                likeStatus = hostHolder.getUser() == null ? 0 :
                        likeService.findEntityLikeStatus(hostHolder.getUser().getId(), ENTITY_TYPE_COMMENT, comment.getId());
                commentMap.put("likeStatus", likeStatus);

                // 查询针对评论的评论，即回复
                List<Comment> replyList = commentService.findCommentByEntity(
                        ENTITY_TYPE_COMMENT, comment.getId(), 0, Integer.MAX_VALUE);
                // 回复的显示列表
                List<Map<String, Object>> replyViewList = new ArrayList<>();
                if (replyList != null) {
                    for (Comment reply : replyList) {
                        Map<String, Object> replyMap = new HashMap<>();
                        // 回复
                        replyMap.put("reply", reply);
                        // 回复的作者
                        replyMap.put("user", userService.findUserById(reply.getUserId()));
                        // 回复的目标
                        User target = reply.getTargetId() == 0 ? null : userService.findUserById(reply.getTargetId());
                        replyMap.put("target", target);
                        // 帖子的点赞数量
                        likeCount = likeService.findEntityLikeCount(ENTITY_TYPE_COMMENT, reply.getId());
                        replyMap.put("likeCount", likeCount);
                        // 帖子的点赞状态(用户没登录时，状态返回0)
                        likeStatus = hostHolder.getUser() == null ? 0 :
                                likeService.findEntityLikeStatus(hostHolder.getUser().getId(), ENTITY_TYPE_COMMENT, reply.getId());
                        replyMap.put("likeStatus", likeStatus);

                        replyViewList.add(replyMap);
                    }
                }
                commentMap.put("replies", replyViewList);
                // 每个帖子的评论数量or回复的数量
                int replyCount = commentService.findCommentCount(ENTITY_TYPE_COMMENT, comment.getId());
                commentMap.put("replyCount", replyCount);

                commentViewList.add(commentMap);
            }
        }
        model.addAttribute("comments", commentViewList);

        return "/site/discuss-detail";
    }

    // 处理置顶帖子请求
    @PostMapping("/top")
    @ResponseBody
    public String setTop(int id) {
        discussPostService.updateType(id, 1);

        // 同步到es
        triggerEventByPost(hostHolder.getUser().getId(), id);

        return CommunityUtil.getJSONString(0);
    }

    // 处理加精帖子请求
    @PostMapping("/wonderful")
    @ResponseBody
    public String setWonderful(int id) {
        discussPostService.updateStatus(id, 1);

        // 同步到es
        triggerEventByPost(hostHolder.getUser().getId(), id);

        // 计算帖子分数
        String key = RedisKeyUtil.getPostScoreKey();
        redisTemplate.opsForSet().add(key, id);

        return CommunityUtil.getJSONString(0);
    }

    // 处理删除帖子请求
    @PostMapping("/delete")
    @ResponseBody
    public String setDelete(int id) {
        discussPostService.updateStatus(id, 2);

        // 触发删贴事件
        Event event = new Event().setTopic(TOPIC_DELETE).setUserId(hostHolder.getUser().getId())
                .setEntityType(ENTITY_TYPE_POST)
                .setEntityId(id);
        eventProducer.fireEvent(event);

        return CommunityUtil.getJSONString(0);
    }

    // 触发发帖事件，同步到es
    private void triggerEventByPost(int userId, int entityId) {
        Event event = new Event().setTopic(TOPIC_PUBLISH).setUserId(userId)
                .setEntityType(ENTITY_TYPE_POST)
                .setEntityId(entityId);
        eventProducer.fireEvent(event);
    }
}
