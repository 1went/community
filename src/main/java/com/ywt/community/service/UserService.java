package com.ywt.community.service;

import com.ywt.community.entity.LoginTicket;
import com.ywt.community.entity.User;
import com.baomidou.mybatisplus.extension.service.IService;
import org.springframework.security.core.GrantedAuthority;

import java.util.Collection;
import java.util.Map;

/**
 * 用户服务类
 */
public interface UserService extends IService<User> {

    /**
     *
     * @param id 根据用户id查找用户
     * @return 返回该 id对应的用户
     */
    User findUserById(Integer id);

    /**
     * 处理用户注册逻辑
     * @return 返回值map中封装了异常信息，如果返回的map内容为空，则注册成功。
     */
    Map<String, Object> register(User user);

    /**
     * 账号激活
     * @param userId 激活的用户账号
     * @param code 激活码
     * @return 激活的状态码
     */
    int activation(Integer userId, String code);

    /**
     * 用户登录逻辑
     * @param username 浏览器传入的账号
     * @param password 传入的密码
     * @param expiredSeconds 设置一个过期时间,单位 ms
     * @return 返回封装了异常信息的 Map结合，如果返回的map里存放有ticket的key，则登录成功。
     */
    Map<String, Object> login(String username, String password, int expiredSeconds);

    /**
     * 退出登录
     * @param ticket 修改对应的凭证状态
     */
    void logOut(String ticket);

    /**
     * 处理忘记密码逻辑
     * @param email 账号绑定的邮箱
     * @param newPassword 新密码
     * @return 封装了异常的Map
     */
    Map<String, Object> forget(String email, String newPassword);

    /**
     * @param ticket 通过ticket查找对应的登录凭证
     */
    LoginTicket findLoginTicket(String ticket);

    /**
     * 更新头像
     * @param userId 用户id
     * @param header 头像路径
     */
    int updateHeader(Integer userId, String header);

    /**
     * 修改用户密码
     * @param id 用户
     * @param modifyPwd 修改后的密码
     */
    int modifyPwd(Integer id, String modifyPwd);

    /**
     * @param username 根据用户名查找用户
     */
    User findUserByName(String username);

    /**
     * 获得某用户的权限
     * @param userId 用户id
     * @return      用户的权限
     */
    Collection<? extends GrantedAuthority> getAuthorities(Integer userId);

}
