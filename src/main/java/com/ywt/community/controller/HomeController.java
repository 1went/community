package com.ywt.community.controller;

import com.ywt.community.entity.DiscussPost;
import com.ywt.community.entity.MyPage;
import com.ywt.community.service.DiscussPostService;
import com.ywt.community.service.LikeService;
import com.ywt.community.service.UserService;
import com.ywt.community.util.CommunityConstant;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author yiwt
 * @Date 2022/5/7 21:20
 */
@Controller
public class HomeController implements CommunityConstant {
    @Resource
    private DiscussPostService discussPostService;
    @Resource
    private UserService userService;
    @Resource
    private LikeService likeService;

    /**
     *
     * @param model 向request域中共享数据
     * @param myPage myPage会自动注入给 model
     * @return  返回首页
     */
    @GetMapping("/index")
    public String getIndexPage(Model model, MyPage myPage,
                               @RequestParam(name = "orderMode", defaultValue = "0") int orderMode) {
        // 首页，查询所有帖子
        myPage.setRows(discussPostService.findDiscussPortRows(0));
        myPage.setPath("/index?orderMode=" + orderMode);
        if (myPage.getCurrent() > myPage.getTotal()) {
            myPage.setCurrent(myPage.getTotal());
        }
        List<DiscussPost> posts;
        // 查询分页结果
        if (orderMode == 0) {
            posts = discussPostService.findDiscussPosts(0, myPage.getCurrent(), myPage.getLimit());
        } else {
            posts = discussPostService.findHotDiscussPosts(myPage.getCurrent(), myPage.getLimit());
        }
        List<Map<String, Object>> handlerPosts = new ArrayList<>();
        // 页面显示时，因为DiscussPost只有userId，而我们需要的显示用户名字，所有这里要处理一下
        if (posts != null) {
            for (DiscussPost post : posts) {
                Map<String, Object> map = new HashMap<>();
                map.put("post", post);
                map.put("user", userService.findUserById(post.getUserId()));
                // 点赞数量
                long likeCount = likeService.findEntityLikeCount(ENTITY_TYPE_POST, post.getId());
                map.put("likeCount", likeCount);
                handlerPosts.add(map);
            }
        }
        // 通过model将数据共享到request域中
        model.addAttribute("discussPosts", handlerPosts);
        model.addAttribute("orderMode", orderMode);
        return "index";
    }

    // 处理错误请求
    @GetMapping("/error")
    public String getErrorPage() {
        return "/error/500";
    }

    // 处理错误页面请求
    @GetMapping("/denied")
    public String getDeniedPage() {
        return "/error/404";
    }
}
