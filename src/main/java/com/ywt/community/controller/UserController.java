package com.ywt.community.controller;

import com.ywt.community.annotation.LoginRequired;
import com.ywt.community.entity.Comment;
import com.ywt.community.entity.DiscussPost;
import com.ywt.community.entity.MyPage;
import com.ywt.community.entity.User;
import com.ywt.community.service.*;
import com.ywt.community.util.CommunityConstant;
import com.ywt.community.util.CommunityUtil;
import com.ywt.community.util.HostHolder;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.multipart.MultipartFile;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author yiwt
 * @Date 2022/5/11 15:32
 */
@Slf4j
@Controller
@RequestMapping("/user")
public class UserController implements CommunityConstant {

    @Value("${community.path.upload}")
    private String uploadPath;

    @Value("${community.path.domain}")
    private String domain;

    @Value("${server.servlet.context-path}")
    private String contextPath;

    @Resource
    private UserService userService;

    @Resource
    private LikeService likeService;

    @Resource
    private FollowService followService;

    @Resource
    private DiscussPostService discussPostService;

    @Resource
    private CommentService commentService;

    @Resource
    private HostHolder hostHolder;  // 取出当前登录用户

    // 获取账号设置页面
    @LoginRequired
    @GetMapping("/setting")
    public String getSettingPage() {
        return "/site/setting";
    }

    // 处理上传文件请求
    @LoginRequired
    @PostMapping("/upload")
    public String uploadHeader(MultipartFile headerImage, Model model) {
        if (headerImage == null || headerImage.getOriginalFilename() == null) {
            model.addAttribute("error", "您还没有选择图片");
            return "/site/setting";
        }
        String filename = headerImage.getOriginalFilename();
        String suffix = filename.substring(filename.lastIndexOf("."));
        if (StringUtils.isBlank(suffix)) {
            model.addAttribute("error", "文件格式不正确");
            return "/site/setting";
        }
        // 生成随机文件名
        filename = CommunityUtil.generateUUID() + suffix;
        File dest = new File(uploadPath + "/" + filename);
        try {
            headerImage.transferTo(dest);
        } catch (IOException e) {
            log.error("上传文件失败：{}", e.getMessage());
            throw new RuntimeException("上传文件失败，服务器发生异常!" + e);
        }
        // 更新用户头像路径(不是文件硬盘位置，而是web访问路径)
        // 类似于：http://localhost:8080/community/user/header/xxx.xx
        String headerUrl = domain + contextPath + "/user/header/" + filename;
        // 得到当前用户
        User user = hostHolder.getUser();
        userService.updateHeader(user.getId(), headerUrl);

        return "redirect:/index";
    }

    // 获取头像图片
    @GetMapping("/header/{fileName}")
    public void getHeader(@PathVariable("fileName") String fileName, HttpServletResponse response) {
        // 在服务器上查找文件
        fileName = uploadPath + "/" + fileName;
        // 获取文件格式，即文件后缀,  '.'之后的内容
        String suffix = fileName.substring(fileName.lastIndexOf(".") + 1);
        // 响应图片
        response.setContentType("image/" + suffix);
        try (
                FileInputStream fis = new FileInputStream(fileName);
                OutputStream os = response.getOutputStream()
        ) {
            byte[] buffer = new byte[1024];
            int len;
            while ((len = fis.read(buffer)) != -1) {
                os.write(buffer, 0, len);
            }
        } catch (IOException e) {
            log.error("读取头像失败，{}", e.getMessage());
        }
    }

    // 处理修改密码请求
    @PostMapping("/modify")
    public String modifyPwd(String originalPwd, String modifyPwd, String confirmPwd, Model model) {
        if (StringUtils.isBlank(originalPwd)) {
            model.addAttribute("originalMsg", "原始密码不能为空");
            return "/site/setting";
        }
        if (StringUtils.isBlank(modifyPwd)) {
            model.addAttribute("modifyMsg", "新密码不能为空");
            return "/site/setting";
        }
        if (StringUtils.isBlank(confirmPwd)) {
            model.addAttribute("confirmMsg", "确认密码不能为空");
            return "/site/setting";
        }
        if (!modifyPwd.equals(confirmPwd)) {
            model.addAttribute("confirmMsg", "两次密码输入不一致");
            return "/site/setting";
        }
        User user = hostHolder.getUser();
        originalPwd = CommunityUtil.MD5(originalPwd + user.getSalt());
        if (!user.getPassword().equals(originalPwd)) {
            model.addAttribute("originalMsg", "原始密码不正确");
            return "/site/setting";
        }
        modifyPwd = CommunityUtil.MD5(modifyPwd + user.getSalt());
        userService.modifyPwd(user.getId(), modifyPwd);
        return "redirect:/login";
    }

    // 处理个人主页请求
    @GetMapping("/profile/{userId}")
    public String getProfilePage(@PathVariable("userId") int userId, Model model) {
        User user = userService.findUserById(userId);
        if (user == null) {
            throw new RuntimeException("该用户不存在");
        }
        model.addAttribute("user", user);
        // 点赞数量
        int likeCount = likeService.findUserLikeCount(userId);
        model.addAttribute("likeCount", likeCount);
        // 关注数量
        long followeeCount = followService.findFolloweeCount(userId, ENTITY_TYPE_USER);
        model.addAttribute("followeeCount", followeeCount);
        // 粉丝数量
        long followerCount = followService.findFollowerCount(ENTITY_TYPE_USER, userId);
        model.addAttribute("followerCount", followerCount);
        // 是否已关注
        boolean hasFollowed = false;
        if (hostHolder.getUser() != null) {
            hasFollowed = followService.hasFollow(hostHolder.getUser().getId(), ENTITY_TYPE_USER, userId);
        }
        model.addAttribute("hasFollowed", hasFollowed);
        return "/site/profile";
    }

    // 处理主页我的帖子请求
    @GetMapping("/mypost/{userId}")
    public String getMyPostPage(@PathVariable("userId") int userId, Model model, MyPage myPage) {
        User user = userService.findUserById(userId);
        if (user == null) {
            throw new RuntimeException("该用户不存在");
        }
        model.addAttribute("user", user);
        myPage.setLimit(5);
        myPage.setPath("/user/mypost/" + userId);
        // 该用户帖子数量
        int count = discussPostService.findDiscussPortRows(userId);
        model.addAttribute("count", count);
        myPage.setRows(count);
        List<DiscussPost> posts = discussPostService.findDiscussPosts(userId, myPage.getCurrent(), myPage.getLimit());
        List<Map<String, Object>> postList = new ArrayList<>();
        for (DiscussPost post : posts) {
            Map<String, Object> map = new HashMap<>();
            // 帖子id
            map.put("id", post.getId());
            // 帖子的标题
            map.put("title", post.getTitle());
            // 帖子内容
            map.put("content", post.getContent());
            // 发帖时间
            map.put("createTime", post.getCreateTime());
            // 帖子的赞
            map.put("like", likeService.findEntityLikeCount(ENTITY_TYPE_POST, post.getId()));
            postList.add(map);
        }
        model.addAttribute("postList", postList);
        return "/site/my-post";
    }

    // 处理我的回复列表请求
    @GetMapping("/myreply/{userId}")
    public String getMyReplyPage(@PathVariable("userId") int userId, MyPage myPage, Model model) {
        User user = userService.findUserById(userId);
        if (user == null) {
            throw new RuntimeException("该用户不存在");
        }
        model.addAttribute("user", user);
        myPage.setPath("/user/myreply/" + userId);
        // 用户评论数量
        int count = commentService.findCommentCountByUser(ENTITY_TYPE_POST, userId);
        model.addAttribute("count", count);
        myPage.setRows(count);
        List<Comment> comments = commentService.findCommentByUser(ENTITY_TYPE_POST, userId, myPage.getCurrent(), myPage.getLimit());
        List<Map<String, Object>> commentList = new ArrayList<>();
        for (Comment comment : comments) {
            Map<String, Object> map = new HashMap<>();
            // 回复内容
            map.put("content", comment.getContent());
            // 回复时间
            map.put("createTime", comment.getCreateTime());
            DiscussPost post = discussPostService.findDiscussPostById(comment.getEntityId());
            // 回复的帖子标题
            map.put("title", post.getTitle());
            // 回复帖子id
            map.put("postid", post.getId());
            commentList.add(map);
        }
        model.addAttribute("commentList", commentList);
        return "/site/my-reply";
    }
}
 