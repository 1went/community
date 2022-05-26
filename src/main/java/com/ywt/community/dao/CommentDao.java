package com.ywt.community.dao;

import com.ywt.community.entity.Comment;

import java.util.List;

/**
 * @author yiwt
 * @Date 2022/5/12 15:33
 */
public interface CommentDao {

    /**
     * 根据用户id查找对应的评论
     * @param entityType 评论对象
     * @param userId     用户id
     * @param offset     当前页
     * @param limit      每页显示数量
     * @return           评论集合
     */
    List<Comment> selectCommentByUser(Integer entityType, Integer userId, int offset, int limit);

    /**
     * 根据评论对象和评论对象id查询对应的评论
     * @param entityType 评论对象
     * @param entityId 评论对象id
     * @param offset 当前页
     * @param limit 每页显示数量
     * @return 分页结果
     */
    List<Comment> selectCommentByEntity(Integer entityType, Integer entityId, int offset, int limit);

    /**
     * 根据评论对象和评论对象id查询对应的评论条数
     */
    int selectCountByEntity(Integer entityType, Integer entityId);

    /**
     * @param comment 添加一条评论
     */
    int insertComment(Comment comment);

    /**
     * 根据用户id查询用户回复某评论对象的数量
     * @param entityType  评论对象
     * @param userId      用户id
     * @return            该用户回复的数量
     */
    int selectCountByUser(Integer entityType, Integer userId);

    /**
     * @param id 根据id查评论
     */
    Comment selectCommentById(Integer id);
}
