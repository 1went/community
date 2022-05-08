package com.ywt.community.dao.impl;

import com.ywt.community.dao.UserDao;
import com.ywt.community.entity.User;
import com.ywt.community.mapper.UserMapper;
import org.springframework.stereotype.Repository;

import javax.annotation.Resource;

/**
 * @author yiwt
 * @Date 2022/5/7 21:14
 */
@Repository
public class UserDaoImpl implements UserDao {
    @Resource
    private UserMapper userMapper;

    @Override
    public User selectUserById(Integer id) {
        return userMapper.selectById(id);
    }
}
