package com.ywt.community.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import java.io.Serializable;
import java.util.Date;
import lombok.Data;

/**
 * 评论实体类
 * @TableName comment
 */
@TableName(value ="comment")
@Data
public class Comment implements Serializable {
    /**
     * 评论id
     */
    @TableId(type = IdType.AUTO)
    private Integer id;

    /**
     * 评论所属的用户id
     */
    private Integer userId;

    /**
     * 评论的对象
     * 1-帖子  2-评论本身
     */
    private Integer entityType;

    /**
     * 评论对象的id
     */
    private Integer entityId;

    /**
     * 回复某个用户的评论时，该用户的id
     */
    private Integer targetId = 0;

    /**
     * 评论内容
     */
    private String content;

    /**
     * 评论状态  0-正常 1-不可用
     */
    private Integer status;

    /**
     * 评论时间
     */
    private Date createTime;

    @TableField(exist = false)
    private static final long serialVersionUID = 1L;
}