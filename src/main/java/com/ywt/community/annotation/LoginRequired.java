package com.ywt.community.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 自定义注解，被打上该注解的方法必须登录后才能访问
 * @author yiwt
 * @Date 2022/5/11 18:16
 */
@Target(ElementType.METHOD)  // 该注解只声明在方法上
@Retention(RetentionPolicy.RUNTIME)  // 程序运行期间生效
public @interface LoginRequired {
}
