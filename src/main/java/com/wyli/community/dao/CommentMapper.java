package com.wyli.community.dao;

import com.wyli.community.entity.Comment;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface CommentMapper {

    // entityType可以是POST,COMMENT,COURSE等

    // 某entity下面的评论列表
    List<Comment> selectCommentsByEntity(int entityType, int entityId, int offset, int limit);

    // 某entity下面的评论数
    int selectCountByEntity(int entityType, int entityId);

    int insertComment(Comment comment);
}
