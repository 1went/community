package com.ywt.community.controller;

import com.ywt.community.service.DataService;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import javax.annotation.Resource;
import java.time.LocalDate;

/**
 * @author yiwt
 * @Date 2022/5/21 14:47
 */
@Controller
public class DataController {
    @Resource
    private DataService dataService;

    /**
     * LocalDate不提供自己的构造函数，而是使用静态方法调用进行实例化。
     * 在模型中加入 key = localDate,  value = LocalDate.now()的属性
     * 这样在controller方法中的LocalDate就会通过该属性定义的值注入，而不在使用其构造器
     * 从而避免出现 no primary or default constructor found for class java.time.LocalDate
     */
    @ModelAttribute
    LocalDate initLocalDate() {
        return LocalDate.now();
    }

    // 打开统计页面
    @RequestMapping(path = "/data", method = {RequestMethod.GET, RequestMethod.POST})
    public String getDataPage() {
        return "/site/admin/data";
    }

    // 统计网站UV
    @PostMapping("/data/uv")
    public String getUV(@DateTimeFormat(pattern = "yyyy-MM-dd") @ModelAttribute LocalDate start,
                        @DateTimeFormat(pattern = "yyyy-MM-dd") @ModelAttribute LocalDate end, Model model) {
        long uv = dataService.calculateUV(start, end);
        model.addAttribute("uv", uv);
        // 将时间返回给页面用于显示
        model.addAttribute("uvStart", start);
        model.addAttribute("uvEnd", end);
        // 回到页面
        return "forward:/data";
    }

    // 统计网站DAU
    @PostMapping("/data/dau")
    public String getDAU(@DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate start,
                         @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate end, Model model) {
        long dau = dataService.calculateDAU(start, end);
        model.addAttribute("dau", dau);
        // 将时间返回给页面用于显示
        model.addAttribute("dauStart", start);
        model.addAttribute("dauEnd", end);
        // 回到页面
        return "forward:/data";
    }

}
