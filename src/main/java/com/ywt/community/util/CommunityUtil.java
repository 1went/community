package com.ywt.community.util;

import com.alibaba.fastjson.JSONObject;
import org.apache.commons.lang3.StringUtils;
import org.springframework.util.DigestUtils;

import java.util.Map;
import java.util.UUID;

/**
 * @author yiwt
 * @Date 2022/5/9 9:48
 */
public class CommunityUtil {
    // 生成随机字符串,用于激活码生成、头像默认名等
    public static String generateUUID() {
        return UUID.randomUUID().toString().replaceAll("-","");
    }

    // MD5加密
    public static String MD5(String key) {
        if (StringUtils.isBlank(key)) {
            return null;
        }
        return DigestUtils.md5DigestAsHex(key.getBytes());  // 加密为16进制
    }

    /**
     * @param code 编号
     * @param message 提示信息
     * @param map 业务数据
     * @return JSON格式的字符串
     */
    public static String getJSONString(int code, String message, Map<String, Object> map) {
        JSONObject json = new JSONObject();
        json.put("code", code);
        json.put("msg", message);
        if (map != null) {
            for (String key : map.keySet()) {
                json.put(key, map.get(key));
            }
        }
        return json.toJSONString();
    }

    public static String getJSONString(int code, String message) {
        return getJSONString(code, message, null);
    }

    public static String getJSONString(int code) {
        return getJSONString(code, null, null);
    }
}
