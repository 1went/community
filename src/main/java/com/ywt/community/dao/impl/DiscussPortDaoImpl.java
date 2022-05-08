package com.ywt.community.dao.impl;


import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
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


    @Override
    public List<DiscussPost> selectDiscussPosts(int  userId, int offset, int limit) {
        LambdaQueryWrapper<DiscussPost> queryWrapper = new LambdaQueryWrapper<>();
        // 当传入的用户id等于0，表示查询所有
        queryWrapper.ne(DiscussPost::getStatus, "2").eq(userId != 0 , DiscussPost::getUserId, userId)
                .orderByDesc(DiscussPost::getType).orderByDesc(DiscussPost::getCreateTime);

        Page<DiscussPost> page = new Page<>(offset, limit);
        discussPostMapper.selectPage(page, queryWrapper);
        return page.getRecords();
    }

    @Override
    public int selectDiscussPortRows(Integer  userId) {
        LambdaQueryWrapper<DiscussPost> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.ne(DiscussPost::getStatus, 2).eq(userId != 0, DiscussPost::getUserId, userId);
        return discussPostMapper.selectCount(queryWrapper);
    }
}
