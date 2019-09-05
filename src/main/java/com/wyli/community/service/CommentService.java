package com.wyli.community.service;

import com.wyli.community.dao.CommentMapper;
import com.wyli.community.dao.DiscussPostMapper;
import com.wyli.community.entity.Comment;
import com.wyli.community.entity.DiscussPost;
import com.wyli.community.util.CommunityConstants;
import com.wyli.community.util.SensitiveWordsFilter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.util.HtmlUtils;

import java.util.List;

@Service
public class CommentService implements CommunityConstants {
    @Autowired
    private CommentMapper commentMapper;
    @Autowired
    private DiscussPostMapper discussPostMapper;
    @Autowired
    private SensitiveWordsFilter sensitiveWordsFilter;

    public List<Comment> findCommentsByEntity(int entityType, int entityId, int offset, int limit) {
        return commentMapper.selectCommentsByEntity(entityType, entityId, offset, limit);
    }

    public int findCountByEntity(int entityType, int entityId) {
        return commentMapper.selectCountByEntity(entityType, entityId);
    }

    @Transactional(isolation = Isolation.READ_COMMITTED, propagation = Propagation.REQUIRED)
    public int addComment(Comment comment) {
        if (comment == null) {
            throw new IllegalArgumentException("参数不能为空");
        }
        // 转义，过滤敏感词
        comment.setContent(HtmlUtils.htmlEscape(comment.getContent()));
        comment.setContent(sensitiveWordsFilter.filter(comment.getContent()));
        // 新增评论
        int rows = commentMapper.insertComment(comment);
        // 若评论对象为帖子，要更新帖子的评论数属性
        if (comment.getEntityType() == ENTITY_TYPE_POST) {
            int count = commentMapper.selectCountByEntity(comment.getEntityType(), comment.getEntityId());
            discussPostMapper.updateCommentCount(comment.getEntityId(), count);
        }
        return rows;
    }

    public Comment findCommentById(int id) {
        return commentMapper.selectCommentById(id);
    }
}
