package com.ywt.community.config;

import com.ywt.community.controller.interceptor.DataInterceptor;
import com.ywt.community.controller.interceptor.LoginRequiredInterceptor;
import com.ywt.community.controller.interceptor.LoginTicketInterceptor;
import com.ywt.community.controller.interceptor.MessageInterceptor;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import javax.annotation.Resource;

/**
 * @author yiwt
 * @Date 2022/5/11 14:30
 */
@Configuration
public class WebMvcConfig implements WebMvcConfigurer {
    @Resource
    private LoginTicketInterceptor loginTicketInterceptor;

//    @Resource
//    private LoginRequiredInterceptor loginRequiredInterceptor;

    @Resource
    private DataInterceptor dataInterceptor;

    @Resource
    private MessageInterceptor messageInterceptor;

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(loginTicketInterceptor)
                // 不拦截静态资源
                .excludePathPatterns("/**/*.css", "/**/*.js", "/**/*.png", "/**/*.jpg", "/**/*.jpeg");
        // 登录拦截
//        registry.addInterceptor(loginRequiredInterceptor)
//                // 不拦截静态资源
//                .excludePathPatterns("/**/*.css", "/**/*.js", "/**/*.png", "/**/*.jpg", "/**/*.jpeg");

        registry.addInterceptor(messageInterceptor)
                // 不拦截静态资源
                .excludePathPatterns("/**/*.css", "/**/*.js", "/**/*.png", "/**/*.jpg", "/**/*.jpeg");
        // 网站统计拦截器
        registry.addInterceptor(dataInterceptor)
                // 不拦截静态资源
                .excludePathPatterns("/**/*.css", "/**/*.js", "/**/*.png", "/**/*.jpg", "/**/*.jpeg");

    }
}
