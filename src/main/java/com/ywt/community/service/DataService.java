package com.ywt.community.service;

import java.time.LocalDate;

/**
 * 做数据统计
 * @author yiwt
 * @Date 2022/5/21 14:11
 */
public interface DataService {

    /**
     * 统计单日UV
     * @param ip 用户ip
     */
    void recordUV(String ip);

    /**
     * 统计区间UV
     * @param start 开始时间
     * @param end   结束时间
     */
    long calculateUV(LocalDate start, LocalDate end);

    /**
     * 统计单日DAU
     * @param userId 用户id
     */
    void recordDAU(int userId);

    /**
     * 统计区间DAU
     * @param start 开始时间
     * @param end   结束时间
     */
    long calculateDAU(LocalDate start, LocalDate end);

}
