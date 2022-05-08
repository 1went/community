package com.ywt.community.entity;

import lombok.Getter;

/**
 * 封装分页相关信息
 *
 * @author yiwt
 * @Date 2022/5/8 9:54
 */
@Getter
public class MyPage {
    // 当前页
    private int current = 1;
    // 每页最多显示数据
    private int limit = 10;
    // 总共多少条数据
    private int rows;
    // 查询路径（用于复用分页链接
    private String path;

    public void setCurrent(int current) {
        // 处理数据有误
        if (current >= 1) {
            this.current = current;
        }
    }

    public void setLimit(int limit) {
        // 每页显示的数据不能大于100
        if (limit >= 1 && limit <= 100) {
            this.limit = limit;
        }
    }

    public void setRows(int rows) {
        if (rows >= 0) {
            this.rows = rows;
        }
    }

    public void setPath(String path) {
        this.path = path;
    }

    /**
     * @return 当前页的起始行
     */
    public int getOffset() {
        return (current - 1) * limit;
    }

    /**
     * @return 获取总页数
     */
    public int getTotal() {
        int total = rows / limit;
        return total == 0 ? total : total + 1;
    }

    /**
     * @return 页码显示的起始页码
     */
    public int getFrom() {
        int from = current - 2;
        // 当起始页码小于1，算第一页
        return Math.max(from, 1);
    }

    /**
     * @return 页面显示的结束页码
     */
    public int getTo() {
        int to = current + 2;
        // 当结束页码大于总页数，算最后一页
        return Math.min(to, getTotal());
    }

}
