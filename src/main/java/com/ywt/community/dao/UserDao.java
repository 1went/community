package com.ywt.community.dao;

import com.ywt.community.entity.User;

/**
 * @author yiwt
 * @Date 2022/5/7 21:12
 */
public interface UserDao {

    /**
     *
     * @param id 根据id查找用户
     * @return 返回 id对应的用户
     */
    User selectUserById(Integer id);

    /**
     * @param username 根据用户名查找
     */
    User selectUserByName(String username);

    /**
     * @param email 根据邮箱查找
     */
    User selectUserByEmail(String email);

    /**
     *
     * @param user 将 user插入数据库
     */
    int insertUser(User user);

    /**
     *  更改用户状态
     * @param id 需要更改的用户id
     * @param status 更改后的状态
     */
    int updateStatus(Integer id, Integer status);

    /**
     * 更改用户头像
     * @param id 具体的用户
     * @param headerUrl 上传的头像
     */
    int updateHeader(Integer id, String headerUrl);

    /**
     * 修改密码
     */
    int updatePassword(Integer id, String password);
}
