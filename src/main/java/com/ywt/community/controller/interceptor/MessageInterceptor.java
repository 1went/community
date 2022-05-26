package com.ywt.community.controller.interceptor;

import com.ywt.community.entity.User;
import com.ywt.community.service.MessageService;
import com.ywt.community.util.HostHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * 每次在controller执行完成、模板引擎调用之前，将消息通知的数量返给页面
 * @author yiwt
 * @Date 2022/5/19 10:04
 */
@Component
public class MessageInterceptor implements HandlerInterceptor {
    @Resource
    private HostHolder hostHolder;
    @Resource
    private MessageService messageService;

    @Override
    public void postHandle(HttpServletRequest request, HttpServletResponse response, Object handler, ModelAndView modelAndView) throws Exception {
        User user = hostHolder.getUser();
        if (user != null && modelAndView != null) {
            int letterUnreadCount = messageService.findLetterUnreadCount(user.getId(), null);
            int noticeUnreadCount = messageService.findUnreadNoticeCount(user.getId(), null);
            modelAndView.addObject("totalUnreadCount", letterUnreadCount + noticeUnreadCount);
        }
    }
}
