package com.ywt.community.controller;

import com.alibaba.fastjson.JSONObject;
import com.ywt.community.entity.Message;
import com.ywt.community.entity.MyPage;
import com.ywt.community.entity.User;
import com.ywt.community.service.MessageService;
import com.ywt.community.service.UserService;
import com.ywt.community.util.CommunityConstant;
import com.ywt.community.util.CommunityUtil;
import com.ywt.community.util.HostHolder;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.util.HtmlUtils;

import javax.annotation.Resource;
import java.util.*;

/**
 * @author yiwt
 * @Date 2022/5/14 9:28
 */
@Controller
public class MessageController implements CommunityConstant {
    @Resource
    private MessageService messageService;
    @Resource
    private UserService userService;
    @Resource
    private HostHolder hostHolder;

    // 处理私信列表请求
    @GetMapping("/letter/list")
    public String getLetterList(Model model, MyPage myPage) {
        User user = hostHolder.getUser();
        // 分页信息
        myPage.setLimit(5);
        myPage.setPath("/letter/list");
        myPage.setRows(messageService.findConversationCount(user.getId()));
        // 分页列表
        List<Message> conversationList = messageService.findConversations(
                user.getId(), myPage.getOffset(), myPage.getLimit());
        // 封装每条会话
        List<Map<String, Object>> conversations = new ArrayList<>();
        if (conversationList != null) {
            for (Message message : conversationList) {
                Map<String, Object> map = new HashMap<>();
                map.put("conversation", message);  // 每条会话
                map.put("letterCount", messageService.findLetterCount(message.getConversationId()));  // 每条会话的私信数量
                map.put("unreadCount", messageService.findLetterUnreadCount(user.getId(), message.getConversationId()));  // 每条会话的未读数量
                // 页面显示的会话头像应该是与当前用户对应的用户头像
                int target = (user.getId().equals(message.getFromId())) ? message.getToId() : message.getFromId();
                map.put("target", userService.findUserById(target));

                conversations.add(map);
            }
        }
        model.addAttribute("conversations", conversations);
        // 总的私信未读数量
        int allUnreadCount = messageService.findLetterUnreadCount(user.getId(), null);
        model.addAttribute("allUnreadCount", allUnreadCount);
        // 总的未读通知数量
        int noticeUnreadCount = messageService.findUnreadNoticeCount(user.getId(), null);
        model.addAttribute("noticeUnreadCount", noticeUnreadCount);
        return "/site/letter";
    }

    // 处理查看会话详情的请求
    @GetMapping("/letter/detail/{conversationId}")
    public String getLetterDetail(@PathVariable("conversationId") String conversationId, Model model, MyPage myPage) {
        myPage.setLimit(5);
        myPage.setPath("/letter/detail/" + conversationId);
        myPage.setRows(messageService.findLetterCount(conversationId));
        List<Message> letterList = messageService.findLetters(conversationId, myPage.getCurrent(), myPage.getLimit());
        List<Map<String, Object>> letters = new ArrayList<>();
        if (letterList != null) {
            for (Message letter : letterList) {
                Map<String, Object> map = new HashMap<>();
                map.put("letter", letter);
                map.put("fromUser", userService.findUserById(letter.getFromId()));
                letters.add(map);
            }
        }
        model.addAttribute("letters", letters);
        model.addAttribute("target", getLetterTarget(conversationId));  // 私信目标

        // 设置为已读
        List<Integer> ids = getLetterIds(letterList);
        if (!ids.isEmpty()) {
            messageService.readMessage(ids);
        }
        return "/site/letter-detail";
    }

    // 发送私信请求
    @PostMapping("/letter/send")
    @ResponseBody
    public String sendLetter(String toName, String content) {  // 参数为收信人的用户名和私信内容
        // 得到对话目标
        User target = userService.findUserByName(toName);
        if (target == null) {
            return CommunityUtil.getJSONString(1, "目标用户不存在");
        }
        // 构造完整的message对象
        Message message = new Message();
        message.setFromId(hostHolder.getUser().getId());
        message.setToId(target.getId());
        if (message.getFromId() < message.getToId()) {
            message.setConversationId(message.getFromId() + "_" + message.getToId());
        } else {
            message.setConversationId(message.getToId() + "_" + message.getFromId());
        }
        message.setContent(content);
        message.setCreateTime(new Date());
        messageService.addMessage(message);
        return CommunityUtil.getJSONString(0);
    }

    // 处理删除私信请求
    @PostMapping("/letter/del")
    @ResponseBody
    public String deleteLetter(String letterId) {
        if (StringUtils.isBlank(letterId)) {
            return CommunityUtil.getJSONString(1, "id为空");
        }
        int id = Integer.parseInt(letterId);
        int result = messageService.deleteMessage(id);
        if (result > 0) {
            return CommunityUtil.getJSONString(0);
        }
        return CommunityUtil.getJSONString(1, "删除失败");
    }

    // 处理系统通知请求
    @GetMapping("/notice/list")
    public String getNoticeList(Model model) {
        User user = hostHolder.getUser();
        // 查询评论类通知
        Message notice = messageService.findLatestNotice(user.getId(), TOPIC_COMMENT);
        if (notice != null) {
            Map<String, Object> noticeVO = new HashMap<>();
            noticeVO.put("notice", notice);
            // 还原内容里的转义字符
            String content = HtmlUtils.htmlUnescape(notice.getContent());
            Map<String, Object> data = JSONObject.parseObject(content, HashMap.class);
            noticeVO.put("user", userService.findUserById((Integer) data.get("userId")));
            noticeVO.put("entityType", data.get("entityType"));
            noticeVO.put("entityId", data.get("entityId"));
            noticeVO.put("postId", data.get("postId"));

            // 通知数量
            int count = messageService.findNoticeCount(user.getId(), TOPIC_COMMENT);
            noticeVO.put("count", count);
            // 未读通知数量
            int unread = messageService.findUnreadNoticeCount(user.getId(), TOPIC_COMMENT);
            noticeVO.put("unread", unread);

            model.addAttribute("commentNotice", noticeVO);
        }
        // 查询点赞类的通知
        notice = messageService.findLatestNotice(user.getId(), TOPIC_LIKE);
        if (notice != null) {
            Map<String, Object> noticeVO = new HashMap<>();
            noticeVO.put("notice", notice);
            // 还原内容里的转义字符
            String content = HtmlUtils.htmlUnescape(notice.getContent());
            Map<String, Object> data = JSONObject.parseObject(content, HashMap.class);
            noticeVO.put("user", userService.findUserById((Integer) data.get("userId")));
            noticeVO.put("entityType", data.get("entityType"));
            noticeVO.put("entityId", data.get("entityId"));
            noticeVO.put("postId", data.get("postId"));
            // 通知数量
            int count = messageService.findNoticeCount(user.getId(), TOPIC_LIKE);
            noticeVO.put("count", count);
            // 未读通知数量
            int unread = messageService.findUnreadNoticeCount(user.getId(), TOPIC_LIKE);
            noticeVO.put("unread", unread);

            model.addAttribute("likeNotice", noticeVO);
        }
        // 查询关注类的通知
        notice = messageService.findLatestNotice(user.getId(), TOPIC_FOLLOW);
        if (notice != null) {
            Map<String, Object> noticeVO = new HashMap<>();
            noticeVO.put("notice", notice);
            // 还原内容里的转义字符
            String content = HtmlUtils.htmlUnescape(notice.getContent());
            // 补充数据
            Map<String, Object> data = JSONObject.parseObject(content, HashMap.class);
            noticeVO.put("user", userService.findUserById((Integer) data.get("userId")));
            noticeVO.put("entityType", data.get("entityType"));
            noticeVO.put("entityId", data.get("entityId"));
            // 通知数量
            int count = messageService.findNoticeCount(user.getId(), TOPIC_FOLLOW);
            noticeVO.put("count", count);
            // 未读通知数量
            int unread = messageService.findUnreadNoticeCount(user.getId(), TOPIC_FOLLOW);
            noticeVO.put("unread", unread);

            model.addAttribute("followNotice", noticeVO);
        }

        // 查询私信未读数量
        int letterUnreadCount = messageService.findLetterUnreadCount(user.getId(), null);
        model.addAttribute("letterUnreadCount", letterUnreadCount);
        // 总的未读通知数量
        int noticeUnreadCount = messageService.findUnreadNoticeCount(user.getId(), null);
        model.addAttribute("noticeUnreadCount", noticeUnreadCount);

        return "/site/notice";
    }

    // 处理通知详情
    @GetMapping("/notice/detail/{topic}")
    public String getNoticeDetail(@PathVariable("topic") String topic, MyPage myPage, Model model) {
        User user = hostHolder.getUser();
        myPage.setLimit(5);
        myPage.setPath("/notice/detail/" + topic);
        myPage.setRows(messageService.findNoticeCount(user.getId(), topic));
        List<Message> notices = messageService.findNotices(user.getId(), topic, myPage.getCurrent(), myPage.getLimit());
        List<Map<String, Object>> noticeVOList = new ArrayList<>();
        if (notices != null) {
            for (Message notice : notices) {
                Map<String, Object> map = new HashMap<>();
                // 通知
                map.put("notice", notice);
                // 内容
                String content = HtmlUtils.htmlUnescape(notice.getContent());
                Map<String, Object> data = JSONObject.parseObject(content, HashMap.class);
                map.put("user", userService.findUserById((Integer) data.get("userId")));
                map.put("entityType", data.get("entityType"));
                map.put("entityId", data.get("entityId"));
                map.put("postId", data.get("postId"));
                // 通知的作者
                map.put("fromUser", userService.findUserById(notice.getFromId()));
                noticeVOList.add(map);
            }
        }
        model.addAttribute("notices", noticeVOList);

        // 设置已读
        List<Integer> ids = getLetterIds(notices);
        if (!ids.isEmpty()) {
            messageService.readMessage(ids);
        }
        return "/site/notice-detail";
    }

    // 返回对话中非当前用户的对象
    private User getLetterTarget(String conversationId) {
        String[] split = conversationId.split("_");
        int ds0 = Integer.parseInt(split[0]);
        int ds1 = Integer.parseInt(split[1]);
        if (hostHolder.getUser().getId() == ds0) {
            return userService.findUserById(ds1);
        }
        return userService.findUserById(ds0);
    }

    // 获取未读消息
    private List<Integer> getLetterIds(List<Message> list) {
        List<Integer> ids = new ArrayList<>();
        if (list != null) {
            for (Message message : list) {
                if (hostHolder.getUser().getId().equals(message.getToId()) && message.getStatus() == 0) {
                    ids.add(message.getId());
                }
            }
        }
        return ids;
    }
}
