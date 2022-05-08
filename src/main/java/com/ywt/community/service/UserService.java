package com.ywt.community.service;

import com.ywt.community.entity.User;
import com.baomidou.mybatisplus.extension.service.IService;

/**
 *
 */
public interface UserService extends IService<User> {

    User findUserById(Integer id);

}
