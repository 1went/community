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
     * 热帖
     * @param offset 当前页
     * @param limit 每页显示的条数
     * @return 返回结果，并先按帖子类型、再按照帖子得分、最后发帖时间降序排序
     */
    List<DiscussPost> findHotDiscussPosts(int offset, int limit);

    /**
     * 根据用户 id 请求数据
     * @param userId 如果用户 id为 0，表示请求所有
     * @return 受影响的行数
     */
    int findDiscussPortRows(Integer  userId);

    // 插入一条帖子
    int addDiscussPost(DiscussPost discussPost);


    /**
     * @param id 根据id查询帖子
     */
    DiscussPost findDiscussPostById(Integer id);

    /**
     * 更新帖子的评论数量
     * @param id           帖子id
     * @param commentCount 帖子的评论数
     */
    int updateCommentCount(Integer id, Integer commentCount);

    /**
     * 修改帖子类型
     * @param id   帖子id
     * @param type 0-普通  1-置顶
     */
    int updateType(Integer id, Integer type);

    /**
     * 修改帖子状态
     * @param id     帖子id
     * @param status 0-正常  1-精华  2-拉黑
     */
    int updateStatus(Integer id, Integer status);

    /**
     * 修改帖子分数
     * @param id    帖子id
     * @param score 帖子分数
     */
    int updateScore(Integer id, Double score);
}
