package com.ywt.community.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.ywt.community.dao.CommentDao;
import com.ywt.community.entity.Comment;
import com.ywt.community.service.CommentService;
import com.ywt.community.mapper.CommentMapper;
import com.ywt.community.service.DiscussPostService;
import com.ywt.community.util.CommunityConstant;
import com.ywt.community.util.SensitiveFilter;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.util.HtmlUtils;

import javax.annotation.Resource;
import java.util.List;

/**
 *
 */
@Service
public class CommentServiceImpl extends ServiceImpl<CommentMapper, Comment>
    implements CommentService, CommunityConstant {

    @Resource
    private CommentDao commentDao;

    @Resource
    private SensitiveFilter sensitiveFilter;

    @Resource
    private DiscussPostService discussPostService;

    @Override
    public List<Comment> findCommentByEntity(Integer entityType, Integer entityId, int offset, int limit) {
        return commentDao.selectCommentByEntity(entityType, entityId, offset, limit);
    }

    @Override
    public int findCommentCount(Integer entityType, Integer entityId) {
        return commentDao.selectCountByEntity(entityType, entityId);
    }

    @Transactional(isolation = Isolation.READ_COMMITTED, propagation = Propagation.REQUIRED)
    @Override
    public int addComment(Comment comment) {
        if (comment == null) {
            throw new IllegalArgumentException("参数不能为空");
        }
        // 内容过滤
        comment.setContent(HtmlUtils.htmlEscape(comment.getContent()));  // 标签过滤
        comment.setContent(sensitiveFilter.filter(comment.getContent()));  // 敏感词过滤
        // 插入评论
        int result = commentDao.insertComment(comment);
        // 注意，是更新帖子的评论数量
        if (comment.getEntityType() == ENTITY_TYPE_POST) {
            // 返回该条帖子的最新评论数量
            int count = commentDao.selectCountByEntity(ENTITY_TYPE_POST, comment.getEntityId());
            // 更新
            discussPostService.updateCommentCount(comment.getEntityId(), count);
        }
        return result;
    }

    @Override
    public int findCommentCountByUser(Integer entityType, Integer userId) {
        return commentDao.selectCountByUser(entityType, userId);
    }

    @Override
    public List<Comment> findCommentByUser(Integer entityType, Integer userId, int offset, int limit) {
        return commentDao.selectCommentByUser(entityType, userId, offset, limit);
    }

    @Override
    public Comment findCommentById(Integer id) {
        return commentDao.selectCommentById(id);
    }
}




