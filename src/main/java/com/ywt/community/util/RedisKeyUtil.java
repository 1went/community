package com.ywt.community.util;

/**
 * redis的key工具类
 * @author yiwt
 * @Date 2022/5/15 11:33
 */
public class RedisKeyUtil {
    private static final String SPLIT = ":";
    private static final String PREFIX_ENTITY_LIKE = "like:entity";  // 点赞实体在redis中的key
    private static final String PREFIX_USER_LIKE = "like:user";      // 点赞用户在redis中的key
    private static final String PREFIX_FOLLOWEE_LIKE = "followee";   // 某用户关注的实体在redis中的key
    private static final String PREFIX_FOLLOWER_LIKE = "follower";   // 某实体拥有粉丝数量在redis中的key
    private static final String PREFIX_KAPTCHA = "kaptcha";          // 验证码存在redis中的key
    private static final String PREFIX_TICKET = "ticket";            // 登录凭证存在redis中的key
    private static final String PREFIX_USER = "user";                // 缓存用户信息在redis中的key
    private static final String PREFIX_UV = "uv";                    // 独立访问量
    private static final String PREFIX_DAU = "dau";                  // 活跃用户
    private static final String PREFIX_POST = "post";                // 待计算得分的帖子

    /**
     * 生成点赞的key like:entity:entityType:entityId
     * @param entityType 点赞实体类型
     * @param entityId   实体id
     * @return           该点赞在redis中的key
     */
    public static String getEntityLikeKey(int entityType, int entityId) {
        return PREFIX_ENTITY_LIKE + SPLIT + entityType + SPLIT + entityId;
    }

    /**
     * 生成用户被点赞的数量的key  like:user:userId
     * @param userId 用户id
     */
    public static String getUserLikeKey(int userId) {
        return PREFIX_USER_LIKE + SPLIT + userId;
    }

    /**
     * 某个用户关注的实体key  followee:userId:entityType  ---> zset(entityId, nowDate)
     * @param userId       用户id
     * @param entityType   用户关注的实体类型
     */
    public static String getFolloweeKey(int userId, int entityType) {
        return PREFIX_FOLLOWEE_LIKE + SPLIT + userId + SPLIT + entityType;
    }

    /**
     * 生成 某个实体拥有的粉丝 的key      follower:entityType:entityId
     * @param entityType  实体类型
     * @param entityId    实体id
     */
    public static String getFollowerKey(int entityType, int entityId) {
        return PREFIX_FOLLOWER_LIKE + SPLIT + entityType + SPLIT + entityId;
    }

    /**
     * 登录验证码key
     * @param owner ..
     * @return ..
     */
    public static String getKaptchaKey(String owner) {
        return PREFIX_KAPTCHA + SPLIT + owner;
    }

    /**
     * 登录凭证的key
     * @param ticket 登录凭证
     */
    public static String getTicketKey(String ticket) {
        return PREFIX_TICKET + SPLIT + ticket;
    }

    /**
     * 缓存用户信息的key
     * @param userId ..
     * @return ..
     */
    public static String getUserKey(int userId) {
        return PREFIX_USER + SPLIT + userId;
    }

    /**
     * 得到单日UV的key
     * @param date 日期格式的字符串
     */
    public static String getUvKey(String date) {
        return PREFIX_UV + SPLIT + date;
    }

    /**
     * 某个区间 UV的key
     * @param startDate 开启时间
     * @param endDate   结束时间
     */
    public static String getUvKey(String startDate, String endDate) {
        return PREFIX_UV + SPLIT + startDate +SPLIT + endDate;
    }

    /**
     * 得到单日DAU的key
     * @param date 日期格式的字符串
     */
    public static String getDauKey(String date) {
        return PREFIX_DAU + SPLIT + date;
    }

    /**
     * 某个区间 DAU的key
     * @param startDate 开启时间
     * @param endDate   结束时间
     */
    public static String getDauKey(String startDate, String endDate) {
        return PREFIX_DAU + SPLIT + startDate +SPLIT + endDate;
    }

    /**
     * 返回待计算得分的帖子存储在redis中的key
     */
    public static String getPostScoreKey() {
        return PREFIX_POST + SPLIT + "score";
    }
}
