package com.ywt.community.service;

import com.ywt.community.entity.Message;
import com.baomidou.mybatisplus.extension.service.IService;

import java.util.List;

/**
 *
 */
public interface MessageService extends IService<Message> {

    /**
     * 查询当前用户的会话列表
     *
     * @param userId 当前用户id
     * @param offset 当前页
     * @param limit  每页显示的数量
     * @return       list中的每一项都是该用户某一个会话列表中最新的一条消息
     */
    List<Message> findConversations(Integer userId, int offset, int limit);

    /**
     * 查询当前用户的会话数量
     *
     * @param userId 当前用户id
     * @return 该用户一共有多少条会话
     */
    int findConversationCount(Integer userId);

    /**
     * 查询每个会话所包含的私信，支持分页
     *
     * @param conversationId 会话id
     * @param offset         当前页
     * @param limit          每页显示的数量
     * @return               当前会话私信的分页结果
     */
    List<Message> findLetters(String conversationId, int offset, int limit);

    /**
     * 查询某个会话包含的私信数量
     *
     * @param conversationId 会话id
     * @return 该会话包含的私信数量
     */
    int findLetterCount(String conversationId);

    /**
     *  查询未读私信数量
     *
     * @param userId         用户id
     * @param conversationId 会话id，如果为null，表示查询所有会话的未读私信数量
     * @return               所有会话或者某个会话的未读私信数量
     */
    int findLetterUnreadCount(Integer userId, String conversationId);

    /**
     * 添加一条消息
     */
    int addMessage(Message message);

    /**
     * 一次性读多条消息，并将消息状态改为已读
     * @param ids 多条数据的id
     */
    int readMessage(List<Integer> ids);

    /**
     * 删除一条私信
     * @param id 私信id
     */
    int deleteMessage(Integer id);

    /**
     * 查询某用户的某主题下的最新通知
     * @param userId  用户id
     * @param topic   某主题
     * @return        最新的一条通知
     */
    Message findLatestNotice(Integer userId, String topic);

    /**
     * 查询某个主题所包含的通知的数量
     */
    int findNoticeCount(Integer userId, String topic);

    /**
     * 查询某用户未读通知的数量
     * @param userId 用户id
     * @param topic  主题id，即返回某特定一主题的未读通知数量。如果为null，则返回所有主题的未读通知数量
     */
    int findUnreadNoticeCount(Integer userId, String topic);

    /**
     * 返回某个主题的通知列表
     * @param userId  用户
     * @param topic   某主题
     * @param offset  当前页
     * @param limit   每页显示数量
     * @return        某个主题的通知列表
     */
    List<Message> findNotices(Integer userId, String topic, int offset, int limit);
}
