package com.wyli.community.controller;

import com.wyli.community.entity.Comment;
import com.wyli.community.entity.DiscussPost;
import com.wyli.community.entity.Event;
import com.wyli.community.entity.User;
import com.wyli.community.event.EventProducer;
import com.wyli.community.service.CommentService;
import com.wyli.community.service.DiscussPostService;
import com.wyli.community.util.CommunityConstants;
import com.wyli.community.util.HostHolder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import java.util.Date;

@Controller
@RequestMapping("/comment")
public class CommentController implements CommunityConstants {

    @Autowired
    private HostHolder hostHolder;
    @Autowired
    private CommentService commentService;
    @Autowired
    private EventProducer eventProducer;
    @Autowired
    private DiscussPostService discussPostService;

    @RequestMapping(path="/add/{discussPostId}", method= RequestMethod.POST)
    public String addComment(@PathVariable("discussPostId") int discussPostId, Comment comment) {
        User user = hostHolder.getUser();
        if (user == null) {
            return "redirect:/login";
        }
        comment.setUserId(user.getId());
        comment.setStatus(0);
        comment.setCreateTime(new Date());
        commentService.addComment(comment);

        Event event = new Event()
                .setTopic(TOPIC_COMMENT)
                .setUserId(user.getId())
                .setEntityType(comment.getEntityType())
                .setEntityId(comment.getId())
                .setOthers("postId", discussPostId);

        // 找出评论的对象的作者
        // 如果用户评论的是评论
        if (comment.getEntityType() == ENTITY_TYPE_COMMENT) {
            if (comment.getTargetId() != 0) {
                event.setEntityUserId(comment.getTargetId());
            } else {
                Comment target = commentService.findCommentById(comment.getEntityId());
                event.setEntityUserId(target.getUserId());
            }
        }
        // 如果用户评论的是帖子
        else if (comment.getEntityType() == ENTITY_TYPE_POST) {
            DiscussPost target = discussPostService.findDiscussPostById(comment.getEntityId());
            event.setEntityUserId(target.getUserId());
        }
        eventProducer.fireEvent(event);

        // 帖子的话还要修改elasticsearch里
        if (comment.getEntityType() == ENTITY_TYPE_POST) {
            event = new Event()
                    .setTopic(TOPIC_PUBLISH)
                    .setEntityId(discussPostId)
                    .setEntityType(ENTITY_TYPE_POST)
                    .setUserId(comment.getUserId());
            eventProducer.fireEvent(event);
        }

        return "redirect:/discuss/detail/" + discussPostId;
    }
}
