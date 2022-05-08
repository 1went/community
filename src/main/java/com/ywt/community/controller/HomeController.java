package com.ywt.community.controller;

import com.ywt.community.entity.DiscussPost;
import com.ywt.community.entity.MyPage;
import com.ywt.community.service.DiscussPostService;
import com.ywt.community.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author yiwt
 * @Date 2022/5/7 21:20
 */
@Controller
public class HomeController {
    @Autowired
    private DiscussPostService discussPostService;
    @Autowired
    private UserService userService;

    /**
     *
     * @param model 向request域中共享数据
     * @param myPage myPage会自动注入给 model
     * @return  返回首页
     */
    @GetMapping("/index")
    public String getIndexPage(Model model, MyPage myPage) {
        // 首页，查询所有帖子
        myPage.setRows(discussPostService.findDiscussPortRows(0));
        myPage.setPath("/index");

        List<DiscussPost> posts = discussPostService.findDiscussPosts(0, myPage.getCurrent(), myPage.getLimit());
        List<Map<String, Object>> handlerPosts = new ArrayList<>();
        // 页面显示时，因为DiscussPost只有userId，而我们需要的显示用户名字，所有这里要处理一下
        if (posts != null) {
            for (DiscussPost post : posts) {
                Map<String, Object> map = new HashMap<>();
                map.put("post", post);
                map.put("user", userService.findUserById(post.getUserId()));
                handlerPosts.add(map);
            }
        }
        // 通过model将数据共享到request域中
        model.addAttribute("discussPosts", handlerPosts);
        return "index";
    }
}
