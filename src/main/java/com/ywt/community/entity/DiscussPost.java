package com.ywt.community.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import java.io.Serializable;
import java.util.Date;
import lombok.Data;

/**
 * 帖子的实体类
 *
 * @TableName discuss_post
 */
@TableName(value ="discuss_post")
@Data
public class DiscussPost implements Serializable {
    /**
     * 自增 id
     */
    @TableId(type = IdType.AUTO)
    private Integer id;

    /**
     * 发帖人
     */
    private Integer userId;

    /**
     * 帖子的标题
     */
    private String title;

    /**
     * 帖子内容
     */
    private String content;

    /**
     * 帖子类型
     * 0-普通; 1-置顶;
     */
    private Integer type;

    /**
     * 帖子状态
     * 0-正常; 1-精华; 2-拉黑;
     */
    private Integer status;

    /**
     * 发帖时间
     */
    private Date createTime;

    /**
     * 该条帖子被评论的次数
     */
    private Integer commentCount;

    /**
     * 帖子热度，用来排名
     */
    private Double score;

    @TableField(exist = false)
    private static final long serialVersionUID = 1L;
}