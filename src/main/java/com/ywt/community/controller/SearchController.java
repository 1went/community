package com.ywt.community.controller;

import com.ywt.community.entity.DiscussPost;
import com.ywt.community.entity.MyPage;
import com.ywt.community.service.ElasticSearchService;
import com.ywt.community.service.LikeService;
import com.ywt.community.service.UserService;
import com.ywt.community.util.CommunityConstant;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author yiwt
 * @Date 2022/5/20 9:04
 */
@Controller
public class SearchController implements CommunityConstant {
    @Resource
    private ElasticSearchService elasticSearchService;
    @Resource
    private UserService userService;
    @Resource
    private LikeService likeService;

    @GetMapping("/search")
    public String search(String keyword, MyPage myPage, Model model) {
        // 搜索
        Page<DiscussPost> searchResult =
                elasticSearchService.searchDiscussPost(keyword, myPage.getCurrent() - 1, myPage.getLimit());
        List<Map<String, Object>> posts = new ArrayList<>();
        if (searchResult != null) {
            for (DiscussPost post : searchResult) {
                Map<String, Object> map = new HashMap<>();
                map.put("post", post);
                map.put("user", userService.findUserById(post.getUserId()));
                map.put("likeCount", likeService.findEntityLikeCount(ENTITY_TYPE_POST, post.getId()));
                posts.add(map);
            }
        }
        model.addAttribute("posts", posts);
        model.addAttribute("keyword", keyword);
        myPage.setPath("/search?keyword=" + keyword);
        myPage.setRows(searchResult == null ? 0 : (int) searchResult.getTotalElements());

        return "site/search";
    }
}
