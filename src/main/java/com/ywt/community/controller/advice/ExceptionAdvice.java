package com.ywt.community.controller.advice;

import com.ywt.community.util.CommunityUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;

/**
 * 异常通知
 * @author yiwt
 * @Date 2022/5/14 14:56
 */
@Slf4j
@ControllerAdvice(annotations = Controller.class)  // 仅扫描controller注解修饰的方法
public class ExceptionAdvice {

    @ExceptionHandler({Exception.class})
    public void handlerException(Exception e, HttpServletRequest request, HttpServletResponse response) throws IOException {
        log.error("服务器发生异常,{}", e.getMessage());
        // 记录详细信息
        for (StackTraceElement element : e.getStackTrace()) {
            log.error(element.toString());
        }
        // 获取请求方式
        String requested = request.getHeader("x-requested-with");
        if ("XMLHttpRequest".equals(requested)) {  // 如果发生异常是来自异步请求，响应JSON数据
            response.setContentType("application/plain;charset=utf-8");
            PrintWriter writer = response.getWriter();
            writer.write(CommunityUtil.getJSONString(1, "服务器异常"));
            return;
        }
        // 否则，重定向到错误页面
        response.sendRedirect(request.getContextPath() + "/error");
    }
}
