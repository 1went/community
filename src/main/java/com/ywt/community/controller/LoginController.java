package com.ywt.community.controller;

import com.google.code.kaptcha.Producer;
import com.ywt.community.entity.User;
import com.ywt.community.service.UserService;
import com.ywt.community.util.CommunityConstant;
import com.ywt.community.util.CommunityUtil;
import com.ywt.community.util.MailClient;
import com.ywt.community.util.RedisKeyUtil;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;

import javax.annotation.Resource;
import javax.imageio.ImageIO;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.OutputStream;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.TimeUnit;

/**
 * @author yiwt
 * @Date 2022/5/9 9:39
 */
@Slf4j
@Controller
public class LoginController implements CommunityConstant {

    @Resource
    private MailClient mailClient;

    @Resource
    private TemplateEngine templateEngine;

    @Resource
    private UserService userService;

    @Resource
    private Producer kaptchaProducer;

    @Resource
    private RedisTemplate<String, Object> redisTemplate;

    /**
     * 项目路径
     */
    @Value("${server.servlet.context-path}")
    private String contextPath;

    // 返回忘记密码页面
    @GetMapping("/forget")
    public String getForgetPage() {
        return "/site/forget";
    }

    // 返回注册页面
    @GetMapping("/register")
    public String getRegisterPage() {
        return "/site/register";
    }

    // 返回登录页面
    @GetMapping("/login")
    public String getLoginPage() {
        return "/site/login";
    }

    // 处理注册请求
    @PostMapping("/register")
    public String register(Model model, User user) {
        Map<String, Object> map = userService.register(user);
        if (map == null || map.isEmpty()) {
            model.addAttribute("msg", "注册成功，我们已经向您的邮箱发送了一封激活邮件，请尽快激活");
            model.addAttribute("target", "/index");
            return "/site/operate-result";
        }
        // 注册失败
        model.addAttribute("usernameMsg", map.get("usernameMsg"));
        model.addAttribute("passwordMsg", map.get("passwordMsg"));
        model.addAttribute("emailMsg", map.get("emailMsg"));
        return "/site/register";  // 回到注册页
    }

    // 处理登录请求
    @PostMapping("/login")
    public String login(String username, String password, String code, boolean rememberMe, // 页面传入的参数
                        Model model /*, HttpServletRequest request*/, HttpServletResponse response,
                        @CookieValue("kaptchaOwner") String kaptchaOwner) {
        // 判断验证码
//        String kaptcha = (String) request.getSession().getAttribute("kaptcha");
        // 从redis中取出验证码
        String kaptcha = null;
        if (StringUtils.isNotBlank(kaptchaOwner)) {
            String redisKey = RedisKeyUtil.getKaptchaKey(kaptchaOwner);
            kaptcha = (String) redisTemplate.opsForValue().get(redisKey);
        }
        if (StringUtils.isBlank(kaptcha) || StringUtils.isBlank(code) || !kaptcha.equalsIgnoreCase(code)) {
            model.addAttribute("codeMsg", "验证码不正确");
            return "/site/login";
        }
        // 检查账号密码
        // 如果用户没有勾选rememberMe，就记录短一点。
        int expiredSeconds = rememberMe ? REMEMBER_EXPIRED_SECONDS : DEFAULT_EXPIRED_SECONDS;
        Map<String, Object> map = userService.login(username, password, expiredSeconds);
        if (map.containsKey("ticket")) {  // 如果 map里有ticket的键，说明登录成功
            Cookie cookie = new Cookie("ticket", map.get("ticket").toString());
            cookie.setPath(contextPath);
            cookie.setMaxAge(expiredSeconds);
            response.addCookie(cookie);
            return "redirect:/index";
        }
        // 登录失败
        model.addAttribute("usernameMsg", map.get("usernameMsg"));
        model.addAttribute("passwordMsg", map.get("passwordMsg"));
        return "/site/login";
    }

    // 处理退出登录
    @GetMapping("/logout")
    public String logOut(@CookieValue("ticket") String ticket) {
        userService.logOut(ticket);
        // 退出登录时，也要清除用户认证结果
        SecurityContextHolder.clearContext();
        return "redirect:/login";
    }

    // 处理邮件激活请求，激活地址 http://localhost:8080/community/activation/{userId}/{code}
    @GetMapping("/activation/{userId}/{code}")
    public String activation(Model model,
                             @PathVariable("userId") int userId, @PathVariable("code") String code) {
        int activation = userService.activation(userId, code);
        if (activation == ACTIVATION_SUCCESS) {
            model.addAttribute("msg", "激活成功，您的账号已经可以正常登录了！");
            model.addAttribute("target", "/login");
        }else if (activation == ACTIVATION_REPEAT) {
            model.addAttribute("msg", "无效操作，该账号已经被激活了！");
            model.addAttribute("target", "/index");
        }else {
            model.addAttribute("msg", "激活失败，您提供的激活码不正确！");
            model.addAttribute("target", "/index");
        }
        return "/site/operate-result";
    }

    // 处理生成图片验证码的请求
    @GetMapping("/kaptcha")
    public void getKaptcha(HttpServletResponse response/*, HttpServletRequest request*/) {
        // 生成验证码
        String text = kaptchaProducer.createText();
        BufferedImage image = kaptchaProducer.createImage(text);

        // 将验证码存入session
//        request.getSession().setAttribute("kaptcha", text);

        // 验证码的归属
        String kaptchaOwner = CommunityUtil.generateUUID();
        Cookie cookie = new Cookie("kaptchaOwner", kaptchaOwner);
        cookie.setMaxAge(60);
        cookie.setPath(contextPath);
        response.addCookie(cookie);
        // 将验证码存入redis并且设置过期时间60s
        String redisKey = RedisKeyUtil.getKaptchaKey(kaptchaOwner);
        redisTemplate.opsForValue().set(redisKey, text, 60, TimeUnit.SECONDS);

        // 将图片输出给浏览器
        response.setContentType("image/png");
        try {
            OutputStream os = response.getOutputStream();
            ImageIO.write(image,"png", os);
        } catch (IOException e) {
            log.error("响应验证码失败：{}", e.getMessage());
        }
    }

    // 处理重置密码请求
    @PostMapping("/forget")
    public String forgetPwd(String email, String code, String newPassword,
                            Model model, HttpServletRequest request) {
        String forgetcode = (String) request.getSession().getAttribute("forgetcode");
        if (StringUtils.isBlank(forgetcode) || StringUtils.isBlank(code) || !forgetcode.equalsIgnoreCase(code)) {
            model.addAttribute("codeMsg", "验证码不正确");
            return "/site/forget";
        }
        Map<String, Object> map = userService.forget(email, newPassword);
        if (map == null || map.isEmpty()) {
            model.addAttribute("msg", "密码重置成功");
            model.addAttribute("target", "/login");
            return "/site/operate-result";
        }
        // 重置失败
        model.addAttribute("emailMsg", map.get("emailMsg"));
        model.addAttribute("newPasswordMsg", map.get("newPasswordMsg"));
        return "/site/forget";
    }

    // 处理获取忘记密码验证码请求
    @GetMapping("/forgetcode")
    @ResponseBody
    public String getForgetCode(String email, HttpServletRequest request) {
        // 封装邮件发送内容
        Context context = new Context();
        context.setVariable("email", email);
        String code = CommunityUtil.generateUUID().substring(0, 5);
        context.setVariable("code", code);
        String process = templateEngine.process("/mail/forget", context);
        mailClient.sendMail(email, "重置密码", process);
        // 保证验证码
        request.getSession().setAttribute("forgetcode", code);
        this.removeAttribute(request.getSession());
        return CommunityUtil.getJSONString(0);
    }

    /**
     *
     * @param session 设置session过期时间
     */
    private void removeAttribute(HttpSession session) {
        Timer timer = new Timer();
        timer.schedule(new TimerTask() {
            @Override
            public void run() {
                session.removeAttribute("forgetcode");
                timer.cancel();
            }
        },5*60*1000);
    }
}
