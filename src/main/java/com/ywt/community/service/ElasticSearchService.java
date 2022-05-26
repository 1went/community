package com.ywt.community.service;

import com.ywt.community.entity.DiscussPost;
import org.springframework.data.domain.Page;

/**
 * @author yiwt
 * @Date 2022/5/19 16:33
 */
public interface ElasticSearchService {
    /**
     * 添加或修改帖子
     * @param post 帖子实体
     */
    void saveDiscussPost(DiscussPost post);

    /**
     *
     * @param id 根据id删除帖子
     */
    void deleteDiscussPost(int id);

    /**
     * 根据关键字搜索并返回分页结果
     * @param keyword  搜索关键字
     * @param current  当前页，从0开始
     * @param limit    每页显示数量
     */
    Page<DiscussPost> searchDiscussPost(String keyword, int current, int limit);
}
