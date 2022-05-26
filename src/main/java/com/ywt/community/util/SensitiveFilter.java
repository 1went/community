package com.ywt.community.util;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.CharUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.Map;

/**
 * 敏感词过滤
 *
 * @author yiwt
 * @Date 2022/5/12 9:37
 */
@Slf4j
@Component
public class SensitiveFilter {
    // 敏感词替换词
    private static final String REPLACEMENT = "***";

    // 根节点
    private TrieNode rootNode = new TrieNode();

    // 在服务启动时就要初始化前缀树
    @PostConstruct
    public void init() {
        try (   // 在类路径下读取敏感词文本
                InputStream is = this.getClass().getClassLoader().getResourceAsStream("sensitive-word.txt");
                BufferedReader reader = new BufferedReader(new InputStreamReader(is));
        ) {
            String keyword;
            while ((keyword = reader.readLine()) != null) {  // 每读一个敏感词就加入树中
                this.addKeyword(keyword);
            }
        } catch (IOException e) {
            log.error("加载敏感词文本失败: {}", e.getMessage());
        }
    }

    /**
     * 过滤敏感词
     * @param text 待过滤的文本
     * @return 过滤后的文本
     */
    public String filter (String text) {
        if (StringUtils.isBlank(text)) {
            return null;
        }
        // 指针1最开始指向树的根节点
        TrieNode tmp = rootNode;
        // 判断text中以begin开始到position结尾的单词是否是一个敏感词
        int begin = 0, position = 0;
        // 存放结果
        StringBuilder sbd = new StringBuilder();
        while (position < text.length()) {
            char c = text.charAt(position);
            if (isSymbol(c)) {
                // 若此时指针1处于根节点，指针2需要往后走，同时将字符加入结果
                if (tmp == rootNode) {
                    sbd.append(c);
                    begin++;
                }
                position++;
                continue;
            }
            // 检查下级节点
            tmp = tmp.getSubNode(c);
            if (tmp == null) {  // 说明以begin开头的字符串不是敏感词
                sbd.append(text.charAt(begin));
                position = ++begin;
                tmp = rootNode;
            } else if (tmp.isSensitive()) {  // begin~position的是敏感词
                sbd.append(REPLACEMENT);
                begin = ++position;
                tmp = rootNode;
            } else {  // 继续检查下一个字符
                position++;
            }
        }
        // 特殊情况，当指针3结束，但指针2未结束
        sbd.append(text.substring(begin));

        return sbd.toString();
    }

    /**
     * @param c 判断c是否是一个符号
     */
    private boolean isSymbol(Character c) {
        // 0x2E80 ~ 0x9FFF 是东亚文字，不属于符号
        return !CharUtils.isAsciiAlphanumeric(c) && (c < 0x2E80 || c > 0x9FFF);
    }

    /**
     * @param keyword 将keyword敏感词加入前缀树
     */
    private void addKeyword(String keyword) {
        TrieNode tmp = rootNode;
        int len = keyword.length();
        for (int i = 0; i < len; i++) {
            char c = keyword.charAt(i);
            // 尝试获取以c为字符的节点
            TrieNode subNode = tmp.getSubNode(c);
            if (subNode == null) {  // 如果subNode为空，说明之前没有初始化该节点
                subNode = new TrieNode();
                tmp.addSubNode(c, subNode);
            }
            tmp = subNode;
            if (i == len - 1) {  // 走到结尾，标记为敏感词
                tmp.setSensitive(true);
            }
        }
    }

    // 前缀树节点
    private static class TrieNode {
        // 标记以当前节点结尾的单词是否是敏感词
        private boolean isSensitive = false;
        // 子节点,key是下一级节点对应的字符，value是下一级节点
        Map<Character, TrieNode> subNodes = new HashMap<>();

        public boolean isSensitive() {
            return isSensitive;
        }

        public void setSensitive(boolean sensitive) {
            isSensitive = sensitive;
        }

        // 添加子节点
        public void addSubNode(Character c, TrieNode node) {
            subNodes.put(c, node);
        }

        // 获取子节点
        public TrieNode getSubNode(Character c) {
            return subNodes.get(c);
        }
    }
}
