package com.ywt.community.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.ywt.community.dao.MessageDao;
import com.ywt.community.entity.Message;
import com.ywt.community.service.MessageService;
import com.ywt.community.mapper.MessageMapper;
import com.ywt.community.util.SensitiveFilter;
import org.springframework.stereotype.Service;
import org.springframework.web.util.HtmlUtils;

import javax.annotation.Resource;
import java.util.List;

/**
 * 消息服务类
 */
@Service
public class MessageServiceImpl extends ServiceImpl<MessageMapper, Message>
    implements MessageService{

    @Resource
    private MessageDao messageDao;

    @Resource
    private SensitiveFilter sensitiveFilter;

    @Override
    public List<Message> findConversations(Integer userId, int offset, int limit) {
        return messageDao.selectConversations(userId, offset, limit);
    }

    @Override
    public int findConversationCount(Integer userId) {
        return messageDao.selectConversationCount(userId);
    }

    @Override
    public List<Message> findLetters(String conversationId, int offset, int limit) {
        return messageDao.selectLetters(conversationId, offset, limit);
    }

    @Override
    public int findLetterCount(String conversationId) {
        return messageDao.selectLetterCount(conversationId);
    }

    @Override
    public int findLetterUnreadCount(Integer userId, String conversationId) {
        return messageDao.selectLetterUnreadCount(userId, conversationId);
    }

    @Override
    public int addMessage(Message message) {
        message.setContent(HtmlUtils.htmlEscape(message.getContent()));
        message.setContent(sensitiveFilter.filter(message.getContent()));
        return messageDao.insertMessage(message);
    }

    @Override
    public int readMessage(List<Integer> ids) {
        return messageDao.updateStatus(ids, 1);
    }

    @Override
    public int deleteMessage(Integer id) {
        return messageDao.deleteMessage(id);
    }

    @Override
    public Message findLatestNotice(Integer userId, String topic) {
        return messageDao.selectLatestNotice(userId, topic);
    }

    @Override
    public int findNoticeCount(Integer userId, String topic) {
        return messageDao.selectNoticeCount(userId, topic);
    }

    @Override
    public int findUnreadNoticeCount(Integer userId, String topic) {
        return messageDao.selectUnreadNoticeCount(userId, topic);
    }

    @Override
    public List<Message> findNotices(Integer userId, String topic, int offset, int limit) {
        return messageDao.selectNotices(userId, topic, offset, limit);
    }
}




