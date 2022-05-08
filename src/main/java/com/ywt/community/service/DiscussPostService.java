package com.ywt.community.service;

import com.ywt.community.entity.DiscussPost;
import com.baomidou.mybatisplus.extension.service.IService;

import java.util.List;

/**
 *
 */
public interface DiscussPostService extends IService<DiscussPost> {

    /**
     * 请求帖子
     * @param userId 如果用户id为0，表示查询所有。否则查询当前用户的帖子
     * @param offset 当前页
     * @param limit 每页显示的条数
     * @return 返回结果，并先后按帖子类型、发帖时间降序排序
     */
    List<DiscussPost> findDiscussPosts(int  userId, int offset, int limit);

    /**
     * 根据用户 id 请求数据
     * @param userId 如果用户 id为 0，表示请求所有
     * @return 受影响的行数
     */
    int findDiscussPortRows(Integer  userId);
}
