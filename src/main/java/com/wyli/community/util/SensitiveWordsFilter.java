package com.wyli.community.util;

import org.apache.commons.lang3.CharUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.io.*;
import java.util.HashMap;
import java.util.Map;

@Component
public class SensitiveWordsFilter {

    private static final Logger logger = LoggerFactory.getLogger(SensitiveWordsFilter.class);
    private static final String REPLACE_WORD = "***";
    private TrieNode root = new TrieNode();

    @PostConstruct
    public void init() {
        try {
            // 从敏感词文件中读取敏感词，添加到前缀树中
            InputStream is = this.getClass().getClassLoader().getResourceAsStream("sensitive-words.txt");
            BufferedReader reader = new BufferedReader(new InputStreamReader(is));
            String keyword = null;
            while ((keyword = reader.readLine()) != null) {
                this.addKeyword(keyword);
            }
        } catch (IOException e) {
            logger.error("敏感词文件读取失败");
        }
    }

    private void addKeyword(String keyword) {
        TrieNode tempNode = root;
        for (int i = 0; i < keyword.length(); i++) {
            char c = keyword.charAt(i);
            TrieNode subNode = tempNode.getSubNode(c);
            if (subNode == null) {
                subNode = new TrieNode();
                tempNode.addSubNode(c, subNode);
                tempNode.setKeywordEnd(false);
            }
            tempNode = subNode;
            if (i == keyword.length() - 1) {
                tempNode.setKeywordEnd(true);
            }
        }
    }

    // 遇到敏感词用***替换，返回替换后的字符串
    public String filter(String word) {
        if (StringUtils.isBlank(word)) return null;

        // 指向可能的敏感词的开头和结尾
        int begin = 0, position = 0;

        // 最后返回的字符串
        StringBuilder sb = new StringBuilder();

        while (begin < word.length()) {
            char beginChar = word.charAt(begin);
            TrieNode firstNode = root.getSubNode(beginChar);
            if (firstNode == null) { // 以字符c开头的不可能是敏感词
                begin++;
                sb.append(beginChar);
            } else {                 // 可能是敏感词
                if (firstNode.isKeywordEnd()) { // 字符c本身就是敏感词
                    sb.append(REPLACE_WORD);
                    begin++;
                    continue;
                }
                position = begin + 1;
                TrieNode tempNode = firstNode;
                while (position < word.length()) {
                    char p = word.charAt(position);
                    if (isSymbol(p)) position++;
                    TrieNode subNode = tempNode.getSubNode(p);
                    if (subNode == null) {
                        sb.append(beginChar);
                        begin++;
                    } else {
                        if (subNode.isKeywordEnd()) {
                            sb.append(REPLACE_WORD);
                            begin = position + 1;
                            break;
                        } else {
                            position++;
                            tempNode = subNode;
                        }
                    }
                }
            }
        }
        return sb.toString();
    }

    private boolean isSymbol(char c) {
        return !CharUtils.isAsciiAlphanumeric(c) && (c < 0x2E80 || c > 0x9FFF);
    }

    private class TrieNode {
        // 敏感词的结尾
        private boolean isKeywordEnd = false;
        // 下级字符和下级节点
        private Map<Character, TrieNode> subNodes = new HashMap<>();

        public boolean isKeywordEnd() {
            return isKeywordEnd;
        }

        public void setKeywordEnd(boolean keywordEnd) {
            isKeywordEnd = keywordEnd;
        }

        public Map<Character, TrieNode> getSubNodes() {
            return subNodes;
        }

        public TrieNode getSubNode(Character c) {
            return subNodes.get(c);
        }

        public void addSubNode(Character c, TrieNode node) {
            subNodes.put(c, node);
        }
    }


}
