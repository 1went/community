package com.ywt.community.dao.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.ywt.community.dao.MessageDao;
import com.ywt.community.entity.Message;
import com.ywt.community.mapper.MessageMapper;
import org.springframework.stereotype.Repository;

import javax.annotation.Resource;
import java.util.List;

/**
 * @author yiwt
 * @Date 2022/5/13 15:20
 */
@Repository
public class MessageDaoImpl implements MessageDao {
    @Resource
    private MessageMapper messageMapper;

    @Override
    public List<Message> selectConversations(Integer userId, int offset, int limit) {
        return messageMapper.selectConversations(userId, offset, limit);
    }

    @Override
    public int selectConversationCount(Integer userId) {
        return messageMapper.selectConversationCount(userId);
    }

    /**
     * select id,from_id,to_id,conversation_id,content,status,create_time
     * from message
     * where status != 2 and from_id != 1 and conversation_id = #{conversationId}
     * order by id
     * limit offset, limit
     */
    @Override
    public List<Message> selectLetters(String conversationId, int offset, int limit) {
        LambdaQueryWrapper<Message> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.ne(Message::getStatus, 2).ne(Message::getFromId, 1)
                .eq(Message::getConversationId, conversationId)
                .orderByDesc(Message::getId);
        Page<Message> page = new Page<>(offset, limit);
        messageMapper.selectPage(page, queryWrapper);
        return page.getRecords();
    }

    /**
     * select count(id)
     * from message
     * where status != 2 and from_id != 1 and conversation_id = #{conversationId}
     */
    @Override
    public int selectLetterCount(String conversationId) {
        LambdaQueryWrapper<Message> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.ne(Message::getStatus, 2).ne(Message::getFromId, 1)
                .eq(Message::getConversationId, conversationId);
        return messageMapper.selectCount(queryWrapper);
    }

    /**
     * select count(id)
     * from message
     * where status = 0 and from_id != 1 and to_id = #{userId}
     * <if test="conversationId != null">
     *     and conversation_id = #{conversationId}
     * </>
     */
    @Override
    public int selectLetterUnreadCount(Integer userId, String conversationId) {
        LambdaQueryWrapper<Message> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(Message::getStatus, 0).ne(Message::getFromId, 1)
                .eq(Message::getToId, userId).eq(conversationId!=null, Message::getConversationId, conversationId);
        return messageMapper.selectCount(queryWrapper);
    }

    @Override
    public int insertMessage(Message message) {
        return messageMapper.insert(message);
    }

    @Override
    public int updateStatus(List<Integer> ids, Integer status) {
        return messageMapper.updateStatus(ids, status);
    }

    @Override
    public int deleteMessage(Integer id) {
        LambdaUpdateWrapper<Message> updateWrapper = new LambdaUpdateWrapper<>();
        updateWrapper.eq(Message::getId, id).set(Message::getStatus, 2);
        return messageMapper.update(null, updateWrapper);
    }

    @Override
    public Message selectLatestNotice(Integer userId, String topic) {
        return messageMapper.selectLatestNotice(userId, topic);
    }

    @Override
    public int selectNoticeCount(Integer userId, String topic) {
        LambdaQueryWrapper<Message> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.ne(Message::getStatus, 2).eq(Message::getFromId, 1)
                .eq(Message::getToId, userId).eq(Message::getConversationId, topic);
        return messageMapper.selectCount(queryWrapper);
    }

    @Override
    public int selectUnreadNoticeCount(Integer userId, String topic) {
        LambdaQueryWrapper<Message> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(Message::getStatus, 0).eq(Message::getFromId, 1)
                .eq(Message::getToId, userId).eq(topic!=null, Message::getConversationId, topic);
        return messageMapper.selectCount(queryWrapper);
    }

    @Override
    public List<Message> selectNotices(int userId, String topic, int offset, int limit) {
        LambdaQueryWrapper<Message> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.ne(Message::getStatus, 2).eq(Message::getFromId, 1)
                .eq(Message::getToId, userId).eq(Message::getConversationId, topic).orderByDesc(Message::getCreateTime);
        Page<Message> page = new Page<>(offset, limit);
        messageMapper.selectPage(page, queryWrapper);
        return page.getRecords();
    }

}
