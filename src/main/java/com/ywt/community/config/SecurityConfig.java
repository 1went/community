package com.ywt.community.config;

import com.ywt.community.util.CommunityConstant;
import com.ywt.community.util.CommunityUtil;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.builders.WebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;


/**
 * @author yiwt
 * @Date 2022/5/20 16:11
 */
@Configuration
public class SecurityConfig extends WebSecurityConfigurerAdapter implements CommunityConstant {

    @Override
    public void configure(WebSecurity web) throws Exception {
        // 开放静态资源
        web.ignoring().antMatchers("/resources/**");
    }

    @Override
    protected void configure(HttpSecurity http) throws Exception {
        // 授权
        http.authorizeRequests()
                .antMatchers(  // 这里的路径只有登录了才能访问（所有用户）
                    "/user/setting",
                        "/user/upload",
                        "/discuss/add",
                        "/comment/add/**",
                        "/letter/**",
                        "/notice/**",
                        "/like",
                        "/follow",
                        "/unfollow"
                )
                .hasAnyAuthority(
                    AUTHORITY_USER, AUTHORITY_ADMIN, AUTHORITY_MODERATOR
                )
                .antMatchers(  // 这里只有版主（即user.type=2）的用户可使用
                        "/discuss/top",
                        "/discuss/wonderful"
                )
                .hasAnyAuthority(
                    AUTHORITY_MODERATOR
                )
                .antMatchers(  // 这里只有管理员（即user.type=1）的用户可使用
                        "/discuss/delete",
                        "/data/**"
                )
                .hasAnyAuthority(
                        AUTHORITY_ADMIN
                )
                .anyRequest().permitAll()
                .and().csrf().disable();  // 禁用防csrf攻击
        // 权限不够时的处理
        http.exceptionHandling()
                .authenticationEntryPoint((request, response, exception) -> {
                    // 没有登录时的处理
                    String header = request.getHeader("x-requested-with");
                    if ("XMLHttpRequest".equals(header)) {  // 异步请求相应的是JSON字符串
                        response.setContentType("application/plain;charset=utf-8");
                        response.getWriter().write(CommunityUtil.getJSONString(403, "没有登录"));
                        return;
                    }
                    // 非异步请求相应的是页面
                    response.sendRedirect(request.getContextPath() + "/login");
                })
                .accessDeniedHandler((request, response, exception) -> {
                    // 权限不足时的处理
                    String header = request.getHeader("x-requested-with");
                    if ("XMLHttpRequest".equals(header)) {  // 异步请求相应的是JSON字符串
                        response.setContentType("application/plain;charset=utf-8");
                        response.getWriter().write(CommunityUtil.getJSONString(403, "你没有访问此功能的权限"));
                        return;
                    }
                    // 非异步请求相应的是页面
                    response.sendRedirect(request.getContextPath() + "/denied");
                });
        // security默认拦截/logout请求，需要覆盖才能执行自己的逻辑
        http.logout().logoutUrl("/securitylogout");
    }
}
