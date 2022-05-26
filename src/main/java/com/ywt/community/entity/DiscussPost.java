package com.ywt.community.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import java.io.Serializable;
import java.util.Date;
import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.elasticsearch.annotations.Document;
import org.springframework.data.elasticsearch.annotations.Field;
import org.springframework.data.elasticsearch.annotations.FieldType;

/**
 * 帖子的实体类
 *
 * @TableName discuss_post
 */
@Document(indexName = "discusspost", type = "_doc", shards = 6, replicas = 3)
@TableName(value ="discuss_post")
@Data
public class DiscussPost implements Serializable {
    /**
     * 自增 id
     */
    @TableId(type = IdType.AUTO)
    @Id
    private Integer id;

    /**
     * 发帖人
     */
    @Field(type = FieldType.Integer)
    private Integer userId;

    /**
     * 帖子的标题
     */
    @Field(type = FieldType.Text, analyzer = "ik_max_word", searchAnalyzer = "ik_smart")
    private String title;

    /**
     * 帖子内容
     */
    @Field(type = FieldType.Text, analyzer = "ik_max_word", searchAnalyzer = "ik_smart")
    private String content;

    /**
     * 帖子类型
     * 0-普通; 1-置顶;
     */
    @Field(type = FieldType.Integer)
    private Integer type = 0;

    /**
     * 帖子状态
     * 0-正常; 1-精华; 2-拉黑;
     */
    @Field(type = FieldType.Integer)
    private Integer status = 0;

    /**
     * 发帖时间
     */
    @Field(type = FieldType.Date)
    private Date createTime;

    /**
     * 该条帖子被评论的次数
     */
    @Field(type = FieldType.Integer)
    private Integer commentCount = 0;

    /**
     * 帖子热度，用来排名
     */
    @Field(type = FieldType.Double)
    private Double score = 0.0;

    @TableField(exist = false)
    private static final long serialVersionUID = 1L;
}