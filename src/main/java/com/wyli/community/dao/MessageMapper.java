package com.wyli.community.dao;

import com.wyli.community.entity.Message;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface MessageMapper {
    // 用户的全部会话，每个会话只返回最新的一条消息
    List<Message> selectConversations(int userId, int offset, int limit);

    // 用户的会话数量
    int selectConversationCount(int userId);

    // 某个会话的全部消息
    List<Message> selectMessages(String conversationId, int offset, int limit);

    // 某个会话的消息总数
    int selectMessageCount(String conversationId);

    // 某个会话的未读消息数量
    int selectUnreadMessageCount(int userId, String conversationId);

    int insertMessage(Message message);

    int updateStatus(List<Integer> ids, int status);

    Message selectLatestNotice(int userId, String topic);

    int selectNoticeCount(int userId, String topic);

    int selectUnreadNoticeCount(int userId, String topic);

    List<Message> selectNotices(int userId, String topic, int offset, int limit);
}
