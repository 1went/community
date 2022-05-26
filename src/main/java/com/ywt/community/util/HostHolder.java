package com.ywt.community.util;

import com.ywt.community.entity.User;
import org.springframework.stereotype.Component;

/**
 * 持有用户信息
 * @author yiwt
 * @Date 2022/5/11 14:43
 */
@Component
public class HostHolder {
    private ThreadLocal<User> users = new ThreadLocal<>();

    public void setUser(User user) {
        users.set(user);
    }

    public User getUser() {
        return users.get();
    }

    public void clear() {
        users.remove();
    }
}
