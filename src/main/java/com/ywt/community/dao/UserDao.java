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
}
