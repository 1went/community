package com.ywt.community.dao;

import com.ywt.community.entity.DiscussPost;

import java.util.List;

/**
 * @author yiwt
 * @Date 2022/5/7 20:12
 */
public interface DiscussPortDao {

    /**
     * 分页
     * @param userId 如果用户id为0，表示查询所有
     * @param offset 当前页
     * @param limit 每页显示的条数
     * @return 分页结果
     */
    List<DiscussPost> selectDiscussPosts(int  userId, int offset, int limit);

    /**
     * 查询表里一共有多少条数据
     * @param userId 如果用户 id为 0，表示查询所有
     * @return 受影响的行数
     */
    int selectDiscussPortRows(Integer  userId);
}
