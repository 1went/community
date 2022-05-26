package com.ywt.community.dao.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.ywt.community.dao.LoginTicketDao;
import com.ywt.community.entity.LoginTicket;
import com.ywt.community.mapper.LoginTicketMapper;
import org.springframework.stereotype.Repository;

import javax.annotation.Resource;

/**
 * @author yiwt
 * @Date 2022/5/10 14:48
 */
@Repository
public class LoginTicketDaoImpl implements LoginTicketDao {

    @Resource
    private LoginTicketMapper loginTicketMapper;

    /**
     * insert into login_ticket(user_id,ticket,status,expired) values(....)
     */
    @Override
    public int insertLoginTicket(LoginTicket loginTicket) {
        return loginTicketMapper.insert(loginTicket);
    }

    /**
     * select id,user_id,ticket,status,expired from login_ticket where ticket=#{ticket}
     */
    @Override
    public LoginTicket selectByTicket(String ticket) {
        LambdaQueryWrapper<LoginTicket> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(LoginTicket::getTicket, ticket);
        return loginTicketMapper.selectOne(queryWrapper);
    }

    /**
     * update login_ticket set status=#{status} where ticket=#{ticket}
     */
    @Override
    public int updateStatus(String ticket, Integer status) {
        LambdaUpdateWrapper<LoginTicket> updateWrapper = new LambdaUpdateWrapper<>();
        updateWrapper.eq(LoginTicket::getTicket, ticket).set(LoginTicket::getStatus, status);
        return loginTicketMapper.update(null, updateWrapper);
    }
}
