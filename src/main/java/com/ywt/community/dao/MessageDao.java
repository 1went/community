package com.ywt.community.dao;

import com.ywt.community.entity.Message;

import java.util.List;

/**
 * @author yiwt
 * @Date 2022/5/13 15:20
 */
public interface MessageDao {
    /**
     * 查询当前用户的会话列表
     *
     * @param userId 当前用户id
     * @param offset 当前页
     * @param limit  每页显示的数量
     * @return       list中的每一项都是该用户某一个会话列表中最新的一条消息
     */
    List<Message> selectConversations(Integer userId, int offset, int limit);

    /**
     * 查询当前用户的会话数量
     *
     * @param userId 当前用户id
     * @return 该用户一共有多少条会话
     */
    int selectConversationCount(Integer userId);

    /**
     * 查询每个会话所包含的私信，支持分页
     *
     * @param conversationId 会话id
     * @param offset         当前页
     * @param limit          每页显示的数量
     * @return               当前会话私信的分页结果
     */
    List<Message> selectLetters(String conversationId, int offset, int limit);

    /**
     * 查询某个会话包含的私信数量
     *
     * @param conversationId 会话id
     * @return 该会话包含的私信数量
     */
    int selectLetterCount(String conversationId);

    /**
     *  查询未读私信数量
     *
     * @param userId         用户id
     * @param conversationId 会话id，如果为0，表示查询所有会话的未读私信数量
     * @return               所有会话或者某个会话的未读私信数量
     */
    int selectLetterUnreadCount(Integer userId, String conversationId);

    /**
     * 新增一条消息
     */
    int insertMessage(Message message);

    /**
     * 批量修改消息状态
     */
    int updateStatus(List<Integer> ids, Integer status);

    /**
     * 删除id对应的消息
     * @param id  消息id
     */
    int deleteMessage(Integer id);

    /**
     * 查询某用户的某主题下的最新通知
     * @param userId  用户id
     * @param topic   某主题
     * @return        最新的一条通知
     */
    Message selectLatestNotice(Integer userId, String topic);

    /**
     * 查询某个主题所包含的通知的数量
     */
    int selectNoticeCount(Integer userId, String topic);

    /**
     * 查询某用户未读通知的数量
     * @param userId 用户id
     * @param topic  主题id，即查询某一主题的未读通知数量。如果传入null，则查询所有主题的数量
     */
    int selectUnreadNoticeCount(Integer userId, String topic);

    /**
     * 查询某个主题的通知列表
     */
    List<Message> selectNotices(int userId, String topic, int offset, int limit);
}
