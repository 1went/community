package com.ywt.community.dao.impl;

import com.ywt.community.dao.DiscussPortDao;
import com.ywt.community.entity.DiscussPost;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/**
 * @author yiwt
 * @Date 2022/5/7 20:42
 */

@SpringBootTest
class DiscussPortDaoImplTest {

    @Autowired
    private DiscussPortDao discussPortDao;

    @Test
    void selectDiscussPosts() {
        List<DiscussPost> discussPosts = discussPortDao.selectDiscussPosts(0, 20, 10);
        discussPosts.forEach(System.out::println);
    }

    @Test
    void selectDiscussPortRows() {
        System.out.println(discussPortDao.selectDiscussPortRows(149));
    }
}