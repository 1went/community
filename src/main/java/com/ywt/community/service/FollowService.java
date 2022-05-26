package com.ywt.community.service;

import java.util.List;
import java.util.Map;

/**
 * @author yiwt
 * @Date 2022/5/16 14:09
 */
public interface FollowService {

    /**
     * 关注:两个动作
     *   1、将触发关注的用户的关注实体存入redis, zset(实体id，时间(ms))
     *   2、被关注者的新增一个粉丝,需要添加到redis，zset(粉丝id，时间(ms))
     * @param userId     触发关注动作的用户id
     * @param entityType 被关注的实体类型
     * @param entityId   被关注的实体id
     */
    void follow(int userId, int entityType, int entityId);

    /**
     * 取关：与关注相反，删除redis中key对应的value即可
     * @param userId     触发取消关注动作的用户id
     * @param entityType 被取消关注的实体类型
     * @param entityId   被取消关注的实体id
     */
    void unFollow(int userId, int entityType, int entityId);

    /**
     * 查找某个用户关注的实体数量
     * @param userId     用户id
     * @param entityType 该用户关注的实体
     * @return           被该用户关注的该实体的数量
     */
    long findFolloweeCount(int userId, int entityType);

    /**
     * 查询某个实体的粉丝数量
     * @param entityType  实体类型
     * @param entityId    实体id
     * @return            该实体的粉丝数量
     */
    long findFollowerCount(int entityType, int entityId);

    /**
     * 某用户是否关注某个具体的实体
     * @param userId     用户id
     * @param entityType 实体类型
     * @param entityId   实体id
     * @return           用户是否关注实体
     */
    boolean hasFollow(int userId, int entityType, int entityId);

    /**
     * 查询某用户关注的人
     * @param userId  用户
     * @param offset  分页条件，当前行
     * @param limit   每页显示数量
     * @return        该用户关注的人的集合，集合中包含了关注的人名和关注时间
     */
    List<Map<String, Object>> findFollowee(int userId, int offset, int limit);

    /**
     * 查询某用户的粉丝
     * @param userId  用户
     * @param offset  分页条件，当前行
     * @param limit   每页显示数量
     * @return        该用户关注的粉丝的集合，集合中包含了粉丝的人名和关注时间
     */
    List<Map<String, Object>> findFollowers(int userId, int offset, int limit);
}
