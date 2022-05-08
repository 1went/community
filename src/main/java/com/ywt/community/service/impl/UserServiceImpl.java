package com.ywt.community.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.ywt.community.dao.UserDao;
import com.ywt.community.entity.User;
import com.ywt.community.service.UserService;
import com.ywt.community.mapper.UserMapper;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;

/**
 *
 */
@Service
public class UserServiceImpl extends ServiceImpl<UserMapper, User>
    implements UserService{

    @Resource
    private UserDao userDao;

    @Override
    public User findUserById(Integer id) {
        return userDao.selectUserById(id);
    }
}




