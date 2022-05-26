package com.ywt.community.dao;

import com.ywt.community.entity.LoginTicket;

/**
 * @author yiwt
 * @Date 2022/5/10 14:49
 */
public interface LoginTicketDao {

    /**
     * 插入一条登录凭证
     */
    int insertLoginTicket(LoginTicket loginTicket);

    /**
     * @param ticket 根据ticket返回对应的凭证
     */
    LoginTicket selectByTicket(String ticket);

    /**
     * @param ticket 更改ticket对应凭证的状态
     * @param status 修改后的状态
     */
    int updateStatus(String ticket, Integer status);
}
