package com.ywt.community.quartz;

import com.ywt.community.entity.DiscussPost;
import com.ywt.community.service.DiscussPostService;
import com.ywt.community.service.ElasticSearchService;
import com.ywt.community.service.LikeService;
import com.ywt.community.util.CommunityConstant;
import com.ywt.community.util.RedisKeyUtil;
import lombok.extern.slf4j.Slf4j;
import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.springframework.data.redis.core.BoundSetOperations;
import org.springframework.data.redis.core.RedisTemplate;

import javax.annotation.Resource;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.time.temporal.TemporalAccessor;

/**
 * @author yiwt
 */
@Slf4j
public class PostScoreRefreshJob implements Job, CommunityConstant {
    @Resource
    private RedisTemplate<String, Object> redisTemplate;
    @Resource
    private DiscussPostService discussPostService;
    @Resource
    private LikeService likeService;
    @Resource
    private ElasticSearchService searchService;

    // 初始时间
    private static final LocalDateTime epoch;

    static {
        TemporalAccessor parse = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss").parse("2022-05-02 00:00:00");
        epoch = LocalDateTime.from(parse);
    }

    @Override
    public void execute(JobExecutionContext jobExecutionContext) throws JobExecutionException {
        String key = RedisKeyUtil.getPostScoreKey();
        BoundSetOperations<String, Object> boundSetOps = redisTemplate.boundSetOps(key);
        if (boundSetOps.size() == 0) {
            log.info("任务取消，没有要刷新的帖子");
            return;
        }
        log.info("任务开始，刷新帖子分数：{}", boundSetOps.size());
        while (boundSetOps.size() > 0) {
            this.refresh((Integer)boundSetOps.pop());
        }
        log.info("任务结束，刷新完成");
    }

    // 刷新分数
    private void refresh(int postId) {
        DiscussPost post = discussPostService.findDiscussPostById(postId);
        if (post == null) {
            log.error("该帖子不存在：id={}", postId);
            return;
        }
        // 是否精华
        boolean wonderful = post.getStatus() == 1;
        // 评论数量
        int commentCount = post.getCommentCount();
        // 点赞数
        long likeCount = likeService.findEntityLikeCount(ENTITY_TYPE_POST, postId);

        // 将Date转为LocalDateTime
        LocalDateTime postDate = post.getCreateTime().toInstant().atZone(ZoneId.systemDefault()).toLocalDateTime();
        // 分数 = log(精华(75)+评论*10+点赞*10) + (当前时间(天) - 初始时间(天))
        double w = (wonderful ? 75 : 0) + commentCount * 10 + likeCount * 2;
        double score = Math.log10(Math.max(w, 1))
                + (epoch.until(postDate, ChronoUnit.DAYS));
        // 更新分数
        discussPostService.updateScore(postId, score);
        // 同步es
        post.setScore(score);
        searchService.saveDiscussPost(post);
    }
}
