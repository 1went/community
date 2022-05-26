package com.ywt.community.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.ywt.community.dao.UserDao;
import com.ywt.community.entity.LoginTicket;
import com.ywt.community.entity.User;
import com.ywt.community.service.UserService;
import com.ywt.community.mapper.UserMapper;
import com.ywt.community.util.CommunityConstant;
import com.ywt.community.util.CommunityUtil;
import com.ywt.community.util.MailClient;
import com.ywt.community.util.RedisKeyUtil;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.stereotype.Service;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;

import javax.annotation.Resource;
import java.util.*;

/**
 *
 */
@Service
public class UserServiceImpl extends ServiceImpl<UserMapper, User>
    implements UserService, CommunityConstant {

    @Resource
    private UserDao userDao;

    @Resource
    private RedisTemplate<String, Object> redisTemplate;
//    private LoginTicketDao loginTicketDao;

    /**
     *
     */
    @Resource
    MailClient mailClient;

    @Resource
    TemplateEngine templateEngine;

    /**
     * 项目路径
     */
    @Value("${server.servlet.context-path}")
    private String contextPath;

    /**
     * 域名
     */
    @Value("${community.path.domain}")
    private String domain;

    @Override
    public User findUserById(Integer id) {
//        return userDao.selectUserById(id);
        // 首先查cache
        User user = getCache(id);
        if (user == null) {  // 如果查不到，就从数据库中查再放到redis中
            user = initCache(id);
        }
        return user;
    }

    @Override
    public Map<String, Object> register(User user) {
        Map<String, Object> map = new HashMap<>();
        if (user == null) {
            throw new IllegalArgumentException("参数不能空！");
        }
        // 对传入的账号、密码、邮箱判空
        if (StringUtils.isBlank(user.getUsername())) {
            map.put("usernameMsg", "账号不能为空！");
            return map;
        }
        if (StringUtils.isBlank(user.getPassword())) {
            map.put("passwordMsg", "密码不能为空！");
            return map;
        }
        if (StringUtils.isBlank(user.getEmail())) {
            map.put("emailMsg", "邮箱不能为空！");
            return map;
        }
        // 检验邮箱是否合法
        String pattern ="[1-9][0-9]{4,}@qq.com";
        if (!user.getEmail().matches(pattern)) {
            map.put("emailMsg", "邮箱格式错误");
            return map;
        }
        // 验证账号或者邮箱是否存在
        User u = userDao.selectUserByName(user.getUsername());
        if (u != null) {
            map.put("usernameMsg", "该账号已存在");
            return map;
        }
        u = userDao.selectUserByEmail(user.getEmail());
        if (u != null) {
            map.put("emailMsg", "该邮箱已被注册");
            return map;
        }

        // 注册用户，补充用户完整信息
        user.setSalt(CommunityUtil.generateUUID().substring(0, 5));  // 设置盐值，5位随机数
        user.setPassword(CommunityUtil.MD5(user.getPassword() + user.getSalt()));  // 加密原来的密码
        user.setType(0);  // 普通用户
        user.setStatus(0);  // 未激活
        user.setActivationCode(CommunityUtil.generateUUID());  // 激活码
        // 设置默认头像
        user.setHeaderUrl(String.format("http://images.nowcoder.com/head/%dt.png", new Random().nextInt(1000)));
        user.setCreateTime(new Date());  // 创建时间
        userDao.insertUser(user);  // 注册

        // 给用户发送激活邮箱
        Context context = new Context();
        context.setVariable("email", user.getEmail());
        // 激活地址 http://localhost:8080/community/activation/{id}/{code}
        String url = domain + contextPath + "/activation/" + user.getId() + "/" + user.getActivationCode();
        context.setVariable("url", url);
        String message = templateEngine.process("/mail/activation", context);
        mailClient.sendMail(user.getEmail(), "注册激活", message);

        return map;
    }

    @Override
    public int activation(Integer userId, String code) {
        User user = userDao.selectUserById(userId);
        if (user.getStatus() == 1) {  // 如果该id用户本身已被激活，返回重复激活状态
            return ACTIVATION_REPEAT;
        }
        if (user.getActivationCode().equals(code)) {
            userDao.updateStatus(userId, 1);
            clearCache(userId);  // 数据变更，清除缓存中的信息
            return ACTIVATION_SUCCESS;
        }
        return ACTIVATION_FAILURE;
    }

    @Override
    public Map<String, Object> login(String username, String password, int expiredSeconds) {
        Map<String, Object> map = new HashMap<>();
        // 空值处理
        if (StringUtils.isBlank(username)) {
            map.put("usernameMsg", "账号不能为空");
            return map;
        }
        if (StringUtils.isBlank(password)) {
            map.put("passwordMsg", "密码不能为空");
            return map;
        }
        // 验证账号是否存在
        User user = userDao.selectUserByName(username);
        if (user == null) {
            map.put("usernameMsg", "该账号不存在");
            return map;
        }
        // 验证账号是否激活
        if (user.getStatus() == 0) {
            map.put("usernameMsg", "该账号未激活");
            return map;
        }
        // 检验密码(先加密传入的密码)
        password = CommunityUtil.MD5(password + user.getSalt());
        if (!user.getPassword().equals(password)) {
            map.put("passwordMsg", "密码不正确");
            return map;
        }
        // 生成登录凭证
        LoginTicket loginTicket = new LoginTicket();
        loginTicket.setUserId(user.getId());
        loginTicket.setTicket(CommunityUtil.generateUUID());
        loginTicket.setStatus(0);
        // 得到过期日期
        long se = System.currentTimeMillis() + (long)expiredSeconds * 1000;
        loginTicket.setExpired(new Date(se));
//        loginTicketDao.insertLoginTicket(loginTicket);
        // 存入redis
        String redisKey = RedisKeyUtil.getTicketKey(loginTicket.getTicket());
        redisTemplate.opsForValue().set(redisKey, loginTicket);

        // 将凭证ticket返回给浏览器，充当了cookie的作用
        map.put("ticket", loginTicket.getTicket());

        return map;
    }

    @Override
    public void logOut(String ticket) {
        String redisKey = RedisKeyUtil.getTicketKey(ticket);
        LoginTicket loginTicket = (LoginTicket) redisTemplate.opsForValue().get(redisKey);
        loginTicket.setStatus(1);  // 删除
        redisTemplate.opsForValue().set(redisKey, loginTicket);
//        loginTicketDao.updateStatus(ticket, 1);
    }

    @Override
    public Map<String, Object> forget(String email, String newPassword) {
        Map<String, Object> map = new HashMap<>();
        // 判空
        if (StringUtils.isBlank(email)) {
            map.put("emailMsg", "邮箱不能为空");
            return map;
        }
        if (StringUtils.isBlank(newPassword)) {
            map.put("newPasswordMsg", "密码不能为空");
            return map;
        }
        // 检验邮箱是否存在
        User user = userDao.selectUserByEmail(email);
        if (user == null) {
            map.put("emailMsg", "该邮箱未被注册");
            return map;
        }
        String handlerPwd = CommunityUtil.MD5(newPassword + user.getSalt()) ;
        userDao.updatePassword(user.getId(), handlerPwd);
        return map;
    }

    @Override
    public LoginTicket findLoginTicket(String ticket) {
        String redisKey = RedisKeyUtil.getTicketKey(ticket);
        return (LoginTicket) redisTemplate.opsForValue().get(redisKey);
//        return loginTicketDao.selectByTicket(ticket);
    }

    @Override
    public int updateHeader(Integer userId, String header) {
//        return userDao.updateHeader(userId, header);
        int rows = userDao.updateHeader(userId, header);
        clearCache(userId);
        return rows;
    }

    @Override
    public int modifyPwd(Integer id, String modifyPwd) {
//        return userDao.updatePassword(id, modifyPwd);
        int rows = userDao.updatePassword(id, modifyPwd);
        clearCache(id);
        return rows;
    }

    @Override
    public User findUserByName(String username) {
        return userDao.selectUserByName(username);
    }

    // 获得权限
    @Override
    public Collection<? extends GrantedAuthority> getAuthorities(Integer userId) {
        User user = this.findUserById(userId);
        List<GrantedAuthority> list = new ArrayList<>();
        list.add((GrantedAuthority) () -> {
           switch (user.getType()) {
               case 1:
                   return AUTHORITY_ADMIN;
               case 2:
                   return AUTHORITY_MODERATOR;
               default:
                   return AUTHORITY_USER;
           }
        });
        return list;
    }

    // 1.优先从缓存中取
    private User getCache(int userId) {
        String redisKey = RedisKeyUtil.getUserKey(userId);
        return (User) redisTemplate.opsForValue().get(redisKey);
    }
    // 2.取不到时，再从数据库中找
    private User initCache(int userId) {
        User user = userDao.selectUserById(userId);
        String redisKey = RedisKeyUtil.getUserKey(userId);
        redisTemplate.opsForValue().set(redisKey, user);
        return user;
    }
    // 3.数据变更时需要清除缓存
    private void clearCache(int userId) {
        String redisKey = RedisKeyUtil.getUserKey(userId);
        redisTemplate.delete(redisKey);
    }
}




