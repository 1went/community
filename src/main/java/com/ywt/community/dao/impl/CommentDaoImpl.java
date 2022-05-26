package com.ywt.community.dao.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.ywt.community.dao.CommentDao;
import com.ywt.community.entity.Comment;
import com.ywt.community.mapper.CommentMapper;
import org.springframework.stereotype.Repository;

import javax.annotation.Resource;
import java.util.List;

/**
 * @author yiwt
 * @Date 2022/5/12 15:34
 */
@Repository
public class CommentDaoImpl implements CommentDao {
    @Resource
    private CommentMapper commentMapper;

    @Override
    public List<Comment> selectCommentByUser(Integer entityType, Integer userId, int offset, int limit) {
        LambdaQueryWrapper<Comment> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(Comment::getStatus, 0).eq(Comment::getEntityType, entityType)
                .eq(Comment::getUserId, userId).orderByDesc(Comment::getCreateTime);
        Page<Comment> page = new Page<>(offset, limit);
        commentMapper.selectPage(page, queryWrapper);
        return page.getRecords();
    }

    /**
     * select (all) from comment where status=0 and entity_type=#{entityType} and entity_id=#{entityId}
     * order by create_time
     * limit offset,limit
     */
    @Override
    public List<Comment> selectCommentByEntity(Integer entityType, Integer entityId, int offset, int limit) {
        LambdaQueryWrapper<Comment> queryWrapper = new LambdaQueryWrapper<>();
        // 查询状态为0（正常评论）
        queryWrapper.eq(Comment::getStatus, 0)
                .eq(Comment::getEntityType, entityType).eq(Comment::getEntityId, entityId)
                .orderByDesc(Comment::getCreateTime);
        Page<Comment> page = new Page<>(offset, limit);
        commentMapper.selectPage(page, queryWrapper);
        return page.getRecords();
    }

    // select count(*) from comment where status=0 and entity_type=#{entityType} and entity_id=#{entityId}
    @Override
    public int selectCountByEntity(Integer entityType, Integer entityId) {
        LambdaQueryWrapper<Comment> queryWrapper = new LambdaQueryWrapper<>();
        // 查询状态为0（正常评论）
        queryWrapper.eq(Comment::getStatus, 0)
                .eq(Comment::getEntityType, entityType).eq(Comment::getEntityId, entityId);
        return commentMapper.selectCount(queryWrapper);
    }

    // insert into comment(...) values(...)
    @Override
    public int insertComment(Comment comment) {
        return commentMapper.insert(comment);
    }

    @Override
    public int selectCountByUser(Integer entityType, Integer userId) {
        LambdaQueryWrapper<Comment> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(Comment::getStatus, 0).eq(Comment::getEntityType, entityType)
                .eq(Comment::getUserId, userId).orderByDesc(Comment::getCreateTime);
        return commentMapper.selectCount(queryWrapper);
    }

    @Override
    public Comment selectCommentById(Integer id) {
        return commentMapper.selectById(id);
    }
}
