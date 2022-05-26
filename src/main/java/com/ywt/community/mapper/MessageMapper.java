package com.ywt.community.mapper;

import com.ywt.community.entity.Message;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;

import java.util.List;

/**
 * @Entity com.ywt.community.entity.Message
 */
public interface MessageMapper extends BaseMapper<Message> {

    /**
     * 自定义查询当前用户的会话列表，支持分页。并且每个会话只返回最新的一条消息
     *
     * @param userId 当前用户id
     * @param offset 当前页
     * @param limit  每页显示的数量
     * @return       当前会话列表的分页结果
     */
    List<Message> selectConversations(Integer userId, int offset, int limit);

    /**
     * 自定义查询当前用户的会话数量
     *
     * @param userId 当前用户id
     * @return 该用户一共有多少条会话
     */
    int selectConversationCount(Integer userId);

    /**
     * 自定义批量修改数据
     */
    int updateStatus(List<Integer> ids, Integer status);

    /**
     * 自定义查询某主题下的最新通知
     * @param userId  用户id
     * @param topic   某主题
     * @return        最新的一条通知
     */
    Message selectLatestNotice(Integer userId, String topic);
}




