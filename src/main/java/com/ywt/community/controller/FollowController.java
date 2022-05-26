package com.ywt.community.controller;

import com.ywt.community.annotation.LoginRequired;
import com.ywt.community.entity.Event;
import com.ywt.community.entity.MyPage;
import com.ywt.community.entity.User;
import com.ywt.community.event.EventProducer;
import com.ywt.community.service.FollowService;
import com.ywt.community.service.UserService;
import com.ywt.community.util.CommunityConstant;
import com.ywt.community.util.CommunityUtil;
import com.ywt.community.util.HostHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.annotation.Resource;
import java.util.List;
import java.util.Map;

/**
 * @author yiwt
 * @Date 2022/5/16 14:20
 */
@Controller
public class FollowController implements CommunityConstant {
    @Resource
    private EventProducer eventProducer;
    @Resource
    private FollowService followService;
    @Resource
    private UserService userService;
    @Resource
    private HostHolder hostHolder;

    // 处理关注请求
    @PostMapping("/follow")
    @ResponseBody
    public String follow(int entityType, int entityId) {
        User user = hostHolder.getUser();
        followService.follow(user.getId(), entityType, entityId);

        // 触发关注通知用户
        Event event = new Event()
                .setTopic(TOPIC_FOLLOW)
                .setUserId(hostHolder.getUser().getId())
                .setEntityType(entityType)
                .setEntityId(entityId)
                .setEntityUserId(entityId);
        eventProducer.fireEvent(event);

        return CommunityUtil.getJSONString(0, "已关注");
    }

    // 处理取关请求
    @LoginRequired
    @PostMapping("/unfollow")
    @ResponseBody
    public String unFollow(int entityType, int entityId) {
        User user = hostHolder.getUser();
        followService.unFollow(user.getId(), entityType, entityId);
        return CommunityUtil.getJSONString(0, "已取关");
    }

    // 处理查看关注列表请求
    @GetMapping("/followee/{userId}")
    public String getFollowee(@PathVariable("userId") int userId, MyPage myPage, Model model) {
        User user = userService.findUserById(userId);
        if (user == null) {
            throw new RuntimeException("该用户不存在");
        }
        model.addAttribute("user", user);
        myPage.setLimit(5);
        myPage.setPath("/followee/" + userId);
        myPage.setRows((int) followService.findFolloweeCount(userId, ENTITY_TYPE_USER));
        List<Map<String, Object>> followeeList = followService.findFollowee(userId, myPage.getOffset(), myPage.getLimit());
        if (followeeList != null) {
            // 遍历map，添加是否关注的状态
            for (Map<String, Object> map : followeeList) {
                User u = (User) map.get("user");
                map.put("hasFollowed", hasFollowed(u.getId()));
            }
        }
        model.addAttribute("users", followeeList);
        return "/site/followee";
    }

    // 处理查看粉丝列表请求
    @GetMapping("/followers/{userId}")
    public String getFollowers(@PathVariable("userId") int userId, MyPage myPage, Model model) {
        User user = userService.findUserById(userId);
        if (user == null) {
            throw new RuntimeException("该用户不存在");
        }
        model.addAttribute("user", user);
        myPage.setLimit(5);
        myPage.setPath("/followers/" + userId);
        myPage.setRows((int) followService.findFollowerCount(ENTITY_TYPE_USER, userId));
        List<Map<String, Object>> followeeList = followService.findFollowers(userId, myPage.getOffset(), myPage.getLimit());
        if (followeeList != null) {
            // 遍历map，添加是否关注的状态
            for (Map<String, Object> map : followeeList) {
                User u = (User) map.get("user");
                map.put("hasFollowed", hasFollowed(u.getId()));
            }
        }
        model.addAttribute("users", followeeList);
        return "/site/follower";
    }

    // 当前用户是否关注userId对应的用户
    private boolean hasFollowed(int userId) {
        if (hostHolder.getUser() == null) {
            return false;
        }
        return followService.hasFollow(hostHolder.getUser().getId(), ENTITY_TYPE_USER, userId);
    }
}
