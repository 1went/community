package com.ywt.community.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import java.io.Serializable;
import java.util.Date;
import lombok.Data;

/**
 * 
 * @TableName user
 */
@TableName(value ="user")
@Data
public class User implements Serializable {
    /**
     * 主键id
     */
    @TableId(type = IdType.AUTO)
    private Integer id;

    /**
     * 用户昵称
     */
    private String username;

    /**
     * 用户密码
     */
    private String password;

    /**
     * 盐值，混淆密码
     */
    private String salt;

    /**
     * 用户邮箱
     */
    private String email;

    /**
     * 0-普通用户; 1-超级管理员; 2-版主;
     */
    private Integer type;

    /**
     * 0-未激活; 1-已激活;
     */
    private Integer status;

    /**
     * 激活码
     */
    private String activationCode;

    /**
     * 用户头像
     */
    private String headerUrl;

    /**
     * 用户创建时间
     */
    private Date createTime;

    @TableField(exist = false)
    private static final long serialVersionUID = 1L;
}