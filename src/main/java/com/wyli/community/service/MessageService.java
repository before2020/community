package com.wyli.community.service;

import com.wyli.community.dao.MessageMapper;
import com.wyli.community.entity.Message;
import com.wyli.community.util.SensitiveWordsFilter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.util.HtmlUtils;

import java.util.ArrayList;
import java.util.List;

@Service
public class MessageService {
    @Autowired
    private MessageMapper messageMapper;
    @Autowired
    private SensitiveWordsFilter sensitiveWordsFilter;
    public List<Message> findConversations(int userId, int offset, int limit) {
        return messageMapper.selectConversations(userId, offset, limit);
    }

    public int findConversationCount(int userId) {
        return messageMapper.selectConversationCount(userId);
    }

    public List<Message> findMessages(String conversationId, int offset, int limit) {
        return messageMapper.selectMessages(conversationId, offset, limit);
    }

    public int findMessageCount(String conversationId) {
        return messageMapper.selectMessageCount(conversationId);
    }

    public int findUnreadMessageCount(int userId, String conversationId) {
        return messageMapper.selectUnreadMessageCount(userId, conversationId);
    }

    public int addMessage(Message message) {
        // 转义 过滤敏感词
        message.setContent(HtmlUtils.htmlEscape(message.getContent()));
        message.setContent(sensitiveWordsFilter.filter(message.getContent()));
        return messageMapper.insertMessage(message);
    }

    public int readMessage(List<Integer> ids) {
        // 修改状态为已读
        return messageMapper.updateStatus(ids, 1);
    }

    public int deleteMessage(int id) {
        ArrayList<Integer> list = new ArrayList<>();
        list.add(id);
        return messageMapper.updateStatus(list, 2);
    }

    public Message findLatestNotice(int userId, String topic) {
        return messageMapper.selectLatestNotice(userId, topic);
    }

    public int findNoticeCount(int userId, String topic) {
        return messageMapper.selectNoticeCount(userId, topic);
    }

    public int findUnreadNoticeCount(int userId, String topic) {
        return messageMapper.selectUnreadNoticeCount(userId, topic);
    }

    public List<Message> findNotices(int userId, String topic, int offset, int limit) {
        return messageMapper.selectNotices(userId, topic, offset, limit);
    }
}
