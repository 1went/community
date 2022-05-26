package com.ywt.community.controller.interceptor;

import com.ywt.community.entity.User;
import com.ywt.community.service.DataService;
import com.ywt.community.util.HostHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * 在controller之前统计网站 UV和 DAU
 * @author yiwt
 * @Date 2022/5/21 14:43
 */
@Component
public class DataInterceptor implements HandlerInterceptor {
    @Resource
    private DataService dataService;
    @Resource
    private HostHolder hostHolder;

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        // 统计UV
        String ip = request.getRemoteHost();
        dataService.recordUV(ip);

        // 统计DAU
        User user = hostHolder.getUser();
        if (user != null) {
            dataService.recordDAU(user.getId());
        }
        return true;
    }
}
