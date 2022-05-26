package com.ywt.community.util;

/**
 * 常量接口
 * @author yiwt
 * @Date 2022/5/9 15:39
 */
public interface CommunityConstant {
    /**
     * 账号激活成功
     */
    int ACTIVATION_SUCCESS = 0;

    /**
     * 重复激活
     */
    int ACTIVATION_REPEAT = 1;

    /**
     * 账号激活失败
     */
    int ACTIVATION_FAILURE = 2;

    /**
     * 默认状态下，登录凭证的超时时间 --> 12h
     */
    int DEFAULT_EXPIRED_SECONDS = 3600 * 12;

    /**
     * 用户勾选记住我选项时，登录凭证的超时时间 --> 30天
     */
    int REMEMBER_EXPIRED_SECONDS = 3600 * 24 * 30;

    /**
     * 实体类型：帖子
     */
    int ENTITY_TYPE_POST = 1;

    /**
     * 实体类型：评论
     */
    int ENTITY_TYPE_COMMENT = 2;

    /**
     * 实体类型：用户
     */
    int ENTITY_TYPE_USER = 3;

    /**
     * 事件主题：评论
     */
    String TOPIC_COMMENT = "comment";

    /**
     * 事件主题：点赞
     */
    String TOPIC_LIKE = "like";

    /**
     * 事件主题：关注
     */
    String TOPIC_FOLLOW = "follow";

    /**
     * 事件主题：发帖
     */
    String TOPIC_PUBLISH = "publish";

    /**
     * 事件主题：删帖
     */
    String TOPIC_DELETE = "delete";

    /**
     * 系统用户id
     */
    int SYSTEM_USER_ID = 1;

    /**
     * 权限：普通用户
     */
    String AUTHORITY_USER = "user";

    /**
     * 权限：管理员
     */
    String AUTHORITY_ADMIN = "admin";

    /**
     * 权限：版主
     */
    String AUTHORITY_MODERATOR = "moderator";

}
