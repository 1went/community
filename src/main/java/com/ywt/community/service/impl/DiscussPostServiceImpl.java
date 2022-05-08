package com.ywt.community.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.ywt.community.dao.DiscussPortDao;
import com.ywt.community.entity.DiscussPost;
import com.ywt.community.service.DiscussPostService;
import com.ywt.community.mapper.DiscussPostMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;

/**
 *
 */
@Service
public class DiscussPostServiceImpl extends ServiceImpl<DiscussPostMapper, DiscussPost>
    implements DiscussPostService{

    @Resource
    private DiscussPortDao discussPortDao;

    @Override
    public List<DiscussPost> findDiscussPosts(int userId, int offset, int limit) {
        return discussPortDao.selectDiscussPosts(userId, offset, limit);
    }

    @Override
    public int findDiscussPortRows(Integer userId) {
        return discussPortDao.selectDiscussPortRows(userId);
    }
}




