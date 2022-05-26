package com.ywt.community.util;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Component;

import javax.mail.internet.MimeMessage;


/**
 * 发送邮箱
 * @author yiwt
 * @Date 2022/5/8 15:59
 */
@Slf4j
@Component
public class MailClient {

    @Autowired
    private JavaMailSender mailSender;

    /**
     * 发件人
     */
    @Value("${spring.mail.username}")
    private String from;

    /**
     *
     * @param to 收件人
     * @param subject 邮件主题
     * @param content 邮件的内容
     */
    public void sendMail(String to, String subject, String content) {
        try {
            MimeMessage mimeMessage = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(mimeMessage);
            helper.setFrom(from);
            helper.setTo(to);
            helper.setSubject(subject);
            helper.setText(content, true);  // 设置支持html
            mailSender.send(helper.getMimeMessage());  // 发送邮件
        } catch (Exception e) {
            log.error("send mail failed, cause by {}", e.getMessage());
        }
    }
}
