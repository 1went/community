package com.ywt.community.dao.impl;


import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.ywt.community.dao.DiscussPortDao;
import com.ywt.community.entity.DiscussPost;
import com.ywt.community.mapper.DiscussPostMapper;
import org.springframework.stereotype.Repository;

import javax.annotation.Resource;
import java.util.List;

/**
 * @author yiwt
 * @Date 2022/5/7 20:11
 */
@Repository
public class DiscussPortDaoImpl implements DiscussPortDao {

    @Resource
    private DiscussPostMapper discussPostMapper;

    /**
     * select count(*) from discuss_post where status!=2
     * <if test="userId != 0">
     *     and user_id=#{userId}
     * </if>
     * order by type desc, create_time desc
     * limit offset,limit
     */
    @Override
    public List<DiscussPost> selectDiscussPosts(int userId, int offset, int limit) {
        LambdaQueryWrapper<DiscussPost> queryWrapper = new LambdaQueryWrapper<>();
        // 当传入的用户id等于0，表示查询所有
        queryWrapper.ne(DiscussPost::getStatus, "2").eq(userId != 0 , DiscussPost::getUserId, userId)
                .orderByDesc(DiscussPost::getType).orderByDesc(DiscussPost::getCreateTime);
        Page<DiscussPost> page = new Page<>(offset, limit);
        discussPostMapper.selectPage(page, queryWrapper);
        return page.getRecords();
    }

    @Override
    public List<DiscussPost> selectHotDiscussPost(int userId, int offset, int limit) {
        LambdaQueryWrapper<DiscussPost> queryWrapper = new LambdaQueryWrapper<>();
        // 当传入的用户id等于0，表示查询所有
        queryWrapper.ne(DiscussPost::getStatus, "2").eq(userId != 0 , DiscussPost::getUserId, userId)
                .orderByDesc(DiscussPost::getType).orderByDesc(DiscussPost::getScore).orderByDesc(DiscussPost::getCreateTime);
        Page<DiscussPost> page = new Page<>(offset, limit);
        discussPostMapper.selectPage(page, queryWrapper);
        return page.getRecords();
    }

    /**
     * select count(*) from discuss_post where status!=2
     * <if test="userId != 0">
     *     and user_id=#{userId}
     * </if>
     */
    @Override
    public int selectDiscussPortRows(Integer userId) {
        LambdaQueryWrapper<DiscussPost> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.ne(DiscussPost::getStatus, 2).eq(userId != 0, DiscussPost::getUserId, userId);
        return discussPostMapper.selectCount(queryWrapper);
    }

    // insert into discuss_post(...) values(...)
    @Override
    public int insertDiscussPost(DiscussPost discussPost) {
        return discussPostMapper.insert(discussPost);
    }

    // select (all) from discuss_post where id=#{id}
    @Override
    public DiscussPost selectDiscussPostById(Integer id) {
        return discussPostMapper.selectById(id);
    }

    /**
     * update from discuss_post set comment_count=#{commentCount} where id=#{id}
     */
    @Override
    public int updateCommentCount(Integer id, Integer commentCount) {
        LambdaUpdateWrapper<DiscussPost> updateWrapper = new LambdaUpdateWrapper<>();
        updateWrapper.eq(DiscussPost::getId, id).set(DiscussPost::getCommentCount, commentCount);
        return discussPostMapper.update(null, updateWrapper);
    }

    @Override
    public int updateType(Integer id, Integer type) {
        LambdaUpdateWrapper<DiscussPost> updateWrapper = new LambdaUpdateWrapper<>();
        updateWrapper.eq(DiscussPost::getId, id).set(DiscussPost::getType, type);
        return discussPostMapper.update(null, updateWrapper);
    }

    @Override
    public int updateStatus(Integer id, Integer status) {
        LambdaUpdateWrapper<DiscussPost> updateWrapper = new LambdaUpdateWrapper<>();
        updateWrapper.eq(DiscussPost::getId, id).set(DiscussPost::getStatus, status);
        return discussPostMapper.update(null, updateWrapper);
    }

    @Override
    public int updateScore(Integer id, Double score) {
        LambdaUpdateWrapper<DiscussPost> updateWrapper = new LambdaUpdateWrapper<>();
        updateWrapper.eq(DiscussPost::getId, id).set(DiscussPost::getScore, score);
        return discussPostMapper.update(null, updateWrapper);
    }
}
