package com.ywt.community.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import java.io.Serializable;
import java.util.Date;
import lombok.Data;

/**
 * 登录凭证
 *
 * @TableName login_ticket
 */
@TableName(value ="login_ticket")
@Data
public class LoginTicket implements Serializable {
    /**
     * 
     */
    @TableId(type = IdType.AUTO)
    private Integer id;

    /**
     * 所属用户
     */
    private Integer userId;

    /**
     * 凭证的key
     */
    private String ticket;

    /**
     * 凭证是否有效。0-有效; 1-无效;
     */
    private Integer status;

    /**
     * 凭证过期时间
     */
    private Date expired;

    @TableField(exist = false)
    private static final long serialVersionUID = 1L;
}