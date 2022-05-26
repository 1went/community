package com.ywt.community.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import java.io.Serializable;
import java.util.Date;
import lombok.Data;

/**
 * 消信的实体类
 * @TableName message
 */
@TableName(value ="message")
@Data
public class Message implements Serializable {
    /**
     * 
     */
    @TableId(type = IdType.AUTO)
    private Integer id;

    /**
     * 消息的发送方
     *  1-表示系统
     *  其他则是正常用户
     */
    private Integer fromId;

    /**
     * 消息的接收方
     */
    private Integer toId;

    /**
     * 如果是私信，就表示会话id，比如用户111和用户112的之间的会话id就是 111_112（小id在前）
     * 如果是通知，则分别用comment、like、follow表示
     */
    private String conversationId;

    /**
     * 消息内容
     * 1、对于一条私信来说，内容是一句话
     * 2、对于系统通知来说，是JSON字符串
     */
    private String content;

    /**
     * 0-未读;1-已读;2-删除;
     */
    private Integer status = 0;

    /**
     * 创建时间
     */
    private Date createTime;

    @TableField(exist = false)
    private static final long serialVersionUID = 1L;
}