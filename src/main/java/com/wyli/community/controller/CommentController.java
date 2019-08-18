package com.wyli.community.controller;

import com.wyli.community.entity.Comment;
import com.wyli.community.service.CommentService;
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

    @RequestMapping(path="/add/{discussPostId}", method= RequestMethod.POST)
    public String addComment(@PathVariable("discussPostId") int discussPostId, Comment comment) {
        if (hostHolder.getUser() == null) {
            return "redirect:/login";
        }
        comment.setUserId(hostHolder.getUser().getId());
        comment.setStatus(0);
        comment.setCreateTime(new Date());
        commentService.addComment(comment);
        return "redirect:/discuss/detail/" + discussPostId;
    }
}
