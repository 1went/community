package com.ywt.community.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.github.benmanes.caffeine.cache.CacheLoader;
import com.github.benmanes.caffeine.cache.Caffeine;
import com.github.benmanes.caffeine.cache.LoadingCache;
import com.ywt.community.dao.DiscussPortDao;
import com.ywt.community.entity.DiscussPost;
import com.ywt.community.service.DiscussPostService;
import com.ywt.community.mapper.DiscussPostMapper;
import com.ywt.community.util.SensitiveFilter;
import lombok.extern.slf4j.Slf4j;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.checkerframework.checker.nullness.qual.Nullable;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.util.HtmlUtils;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;
import java.util.List;
import java.util.concurrent.TimeUnit;

/**
 *
 */
@Slf4j
@Service
public class DiscussPostServiceImpl extends ServiceImpl<DiscussPostMapper, DiscussPost>
        implements DiscussPostService {

    @Resource
    private DiscussPortDao discussPortDao;

    @Resource
    private SensitiveFilter sensitiveFilter;

    @Value("${caffeine.posts.max-size}")
    private int maxSize;

    @Value("${caffeine.posts.expireSeconds}")
    private int expireSecond;

    // 帖子列表的缓存
    private LoadingCache<String, List<DiscussPost>> postListCache;

    // 帖子总数缓存
    private LoadingCache<Integer, Integer> postRowsCache;

    @PostConstruct
    public void init() {
        postListCache = Caffeine.newBuilder()
                .maximumSize(maxSize).expireAfterWrite(expireSecond, TimeUnit.SECONDS)
                .build(new CacheLoader<String, List<DiscussPost>>() {
                    @Nullable
                    @Override
                    public List<DiscussPost> load(@NonNull String key) throws Exception {
                        if (key == null || key.length() == 0) {
                            throw new IllegalArgumentException("参数错误");
                        }
                        String[] split = key.split(":");
                        if (split == null || split.length != 2) {
                            throw new IllegalArgumentException("参数错误");
                        }
                        int offset = Integer.parseInt(split[0]);
                        int limit = Integer.parseInt(split[1]);
                        return discussPortDao.selectHotDiscussPost(0, offset, limit);
                    }
                });
        postRowsCache = Caffeine.newBuilder().maximumSize(1).expireAfterWrite(expireSecond, TimeUnit.SECONDS)
                .build(new CacheLoader<Integer, Integer>() {
                    @Nullable
                    @Override
                    public Integer load(@NonNull Integer key) throws Exception {
                        return discussPortDao.selectDiscussPortRows(key);
                    }
                });
    }

    @Override
    public List<DiscussPost> findDiscussPosts(int userId, int offset, int limit) {
        return discussPortDao.selectDiscussPosts(userId, offset, limit);
    }

    @Override
    public List<DiscussPost> findHotDiscussPosts(int offset, int limit) {
        // 缓存热帖
        return postListCache.get(offset + ":" + limit);
//        return discussPortDao.selectHotDiscussPost(0, offset, limit);
    }

    @Override
    public int findDiscussPortRows(Integer userId) {
        if (userId == 0) {
            return postRowsCache.get(0);
        }
        log.debug("load post rows from db...");
        return discussPortDao.selectDiscussPortRows(userId);
    }

    @Override
    public int addDiscussPost(DiscussPost discussPost) {
        if (discussPost == null) {
            throw new IllegalArgumentException("参数不能为空！");
        }
        // 对帖子中的标题和正文中的html标记转义
        discussPost.setTitle(HtmlUtils.htmlEscape(discussPost.getTitle()));
        discussPost.setContent(HtmlUtils.htmlEscape(discussPost.getContent()));
        // 过滤标题和正文中的敏感词
        discussPost.setTitle(sensitiveFilter.filter(discussPost.getTitle()));
        discussPost.setContent(sensitiveFilter.filter(discussPost.getContent()));

        return discussPortDao.insertDiscussPost(discussPost);
    }

    @Override
    public DiscussPost findDiscussPostById(Integer id) {
        return discussPortDao.selectDiscussPostById(id);
    }

    @Override
    public int updateCommentCount(Integer id, Integer commentCount) {
        return discussPortDao.updateCommentCount(id, commentCount);
    }

    @Override
    public int updateType(Integer id, Integer type) {
        return discussPortDao.updateType(id, type);
    }

    @Override
    public int updateStatus(Integer id, Integer status) {
        return discussPortDao.updateStatus(id, status);
    }

    @Override
    public int updateScore(Integer id, Double score) {
        return discussPortDao.updateScore(id, score);
    }
}




