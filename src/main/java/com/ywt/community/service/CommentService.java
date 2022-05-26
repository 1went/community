package com.ywt.community.service;

import com.ywt.community.entity.Comment;
import com.baomidou.mybatisplus.extension.service.IService;

import java.util.List;

/**
 *
 */
public interface CommentService extends IService<Comment> {

    /**
     * 分页查询评论结果
     * @param entityType 评论对象
     * @param entityId   评论对象id
     * @param offset     当前页
     * @param limit      每页显示的数据量
     */
    List<Comment> findCommentByEntity(Integer entityType, Integer entityId, int offset, int limit);

    /**
     * 查询评论的数量
     * @param entityType 评论对象
     * @param entityId   评论对象id
     * @return           总的评论数
     */
    int findCommentCount(Integer entityType, Integer entityId);

    /**
     * 添加评论
     * @param comment
     * @return
     */
    int addComment(Comment comment);

    /**
     * 查询某用户对某评论的回复数量
     * @param entityType 评论对象
     * @param userId     用户
     * @return           用户回复数量
     */
    int findCommentCountByUser(Integer entityType, Integer userId);

    /**
     * 查询用户对实体的评论
     * @param entityType 实体类型
     * @param userId     用户id
     * @return
     */
    List<Comment> findCommentByUser(Integer entityType, Integer userId, int offset, int limit);

    /**
     * 根据id查对应的评论
     * @param id  评论id
     */
    Comment findCommentById(Integer id);
}
