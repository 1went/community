package com.ywt.community.config;

import com.google.code.kaptcha.Producer;
import com.google.code.kaptcha.impl.DefaultKaptcha;
import com.google.code.kaptcha.util.Config;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.Properties;

/**
 * 验证码配置类
 *
 * @author yiwt
 * @Date 2022/5/10 13:59
 */
@Configuration
public class KaptchaConfig {

    @Bean
    public Producer kaptchaProducer() {
        Properties properties = new Properties();
        properties.setProperty("kaptcha.image.width", "100");  // 生成的验证码图片的宽度
        properties.setProperty("kaptcha.image.height", "40");  // 生成的验证码图片的高度
        properties.setProperty("kaptcha.textproducer.font.size", "32");  // 验证码图片的文字大小
        properties.setProperty("kaptcha.textproducer.font.color", "black");  // 验证码图片的文字颜色
        properties.setProperty("kaptcha.textproducer.char.string", "ABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789");  // 验证码随机字符
        properties.setProperty("kaptcha.textproducer.char.length", "4");  // 验证码字符个数
        properties.setProperty("kaptcha.noise.impl", "com.google.code.kaptcha.impl.NoNoise");  // 验证码干扰策略

        DefaultKaptcha kaptcha = new DefaultKaptcha();
        Config config = new Config(properties);
        kaptcha.setConfig(config);
        return kaptcha;
    }
}
