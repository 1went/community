package com.ywt.community.dao.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
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

    @Override
    public User selectUserByName(String username) {
        LambdaQueryWrapper<User> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(User::getUsername, username);
        return userMapper.selectOne(queryWrapper);
    }

    @Override
    public User selectUserByEmail(String email) {
        LambdaQueryWrapper<User> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(User::getEmail, email);
        return userMapper.selectOne(queryWrapper);
    }

    // insert into user(...) values(...)
    @Override
    public int insertUser(User user) {
        return userMapper.insert(user);
    }

    // update from user set status = #{status} where id = #{id}
    @Override
    public int updateStatus(Integer id, Integer status) {
        LambdaUpdateWrapper<User> updateWrapper = new LambdaUpdateWrapper<>();
        updateWrapper.eq(User::getId, id).set(User::getStatus, status);
        return userMapper.update(null, updateWrapper);
    }

    // update from user set header_url = #{headerUrl} where id = #{id}
    @Override
    public int updateHeader(Integer id, String headerUrl) {
        LambdaUpdateWrapper<User> updateWrapper = new LambdaUpdateWrapper<>();
        updateWrapper.eq(User::getId, id).set(User::getHeaderUrl, headerUrl);
        return userMapper.update(null, updateWrapper);
    }

    // update from user set password = #{password} where id = #{id}
    @Override
    public int updatePassword(Integer id, String password) {
        LambdaUpdateWrapper<User> updateWrapper = new LambdaUpdateWrapper<>();
        updateWrapper.eq(User::getId, id).set(User::getPassword, password);
        return userMapper.update(null, updateWrapper);
    }
}
