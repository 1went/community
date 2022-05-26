package com.ywt.community.service;

/**
 * @author yiwt
 * @Date 2022/5/15 11:38
 */
public interface LikeService {

    /**
     * 点赞功能。第一个是，当一个用户第一次点赞成功插入一条数据，第二次点赞即为取消点赞删除一条数据
     *       以set存储 key(like:entity:entityType:entityId) : value(userId)
     * 另一个步骤是：统计被点赞用户的点赞数量。点赞成功数量+1，取消点赞数量-1
     *       用string存储  key(like:user:userId)  value(点赞数量)
     *
     * @param userId       点赞的用户id
     * @param entityType   点赞的实体对象
     * @param entityId     对象id
     * @param entityUserId 被点赞的用户id
     */
    void like(int userId, int entityType, int entityId, int entityUserId);

    /**
     * 查询某个实体的点赞总数
     * @param entityType 点赞的实体对象
     * @param entityId   对象id
     * @return           点赞总数
     */
    long findEntityLikeCount(int entityType, int entityId);

    /**
     * 查询某人对某实体的点赞状态
     * @param userId     用户id
     * @param entityType 实体对象
     * @param entityId   对象id
     * @return           1：点赞  0：未点赞
     */
    int findEntityLikeStatus(int userId, int entityType, int entityId);

    /**
     * @param userId 用户id
     * @return       该用户收到的点赞数量
     */
    int findUserLikeCount(int userId);
}
