package com.ywt.community.controller.interceptor;

import com.ywt.community.annotation.LoginRequired;
import com.ywt.community.entity.User;
import com.ywt.community.util.HostHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.HandlerInterceptor;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.lang.reflect.Method;

/**
 * 针对未登录状态下，某些方法请求的拦截
 * @author yiwt
 * @Date 2022/5/11 18:19
 */
@Component
public class LoginRequiredInterceptor implements HandlerInterceptor {

    @Resource
    private HostHolder hostHolder;

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        // 拦截的是方法
        if (handler instanceof HandlerMethod) {
            HandlerMethod handlerMethod = (HandlerMethod) handler;
            Method method = handlerMethod.getMethod();  // 获取该方法对象
            LoginRequired loginRequired = method.getAnnotation(LoginRequired.class);  // 查找方法是否被LoginRequired注解修饰
            if (loginRequired != null && hostHolder.getUser() == null) {  // 如果被注解修饰，并且用户处于未登录，就重定向到登录页
                response.sendRedirect(request.getContextPath() + "/login");
                return false;
            }
        }
        return true;
    }
}
