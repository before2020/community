package com.wyli.community.controller;

import com.wyli.community.entity.*;
import com.wyli.community.event.EventProducer;
import com.wyli.community.service.CommentService;
import com.wyli.community.service.DiscussPostService;
import com.wyli.community.service.LikeService;
import com.wyli.community.service.UserService;
import com.wyli.community.util.CommunityConstants;
import com.wyli.community.util.CommunityUtil;
import com.wyli.community.util.HostHolder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.*;

@Controller
@RequestMapping("/discuss")
public class DiscussPostController implements CommunityConstants {
    @Autowired
    private DiscussPostService discussPostService;
    @Autowired
    private UserService userService;
    @Autowired
    private CommentService commentService;
    @Autowired
    private LikeService likeService;
    @Autowired
    private HostHolder hostHolder;
    @Autowired
    private EventProducer eventProducer;

    @RequestMapping(path = "/add", method = RequestMethod.POST)
    @ResponseBody
    public String addDiscussPost(String title, String content) {
        User user = hostHolder.getUser();
        if (user == null) {
            return CommunityUtil.getJSONString(403, "请先登录");
        }

        DiscussPost post = new DiscussPost();
        post.setUserId(user.getId());
        post.setTitle(title);
        post.setContent(content);
        post.setCreateTime(new Date());
        discussPostService.addDiscussPost(post);

        // 保存到elasticsearch中
        Event event = new Event()
                .setTopic(TOPIC_PUBLISH)
                .setEntityId(post.getId())
                .setEntityType(ENTITY_TYPE_POST)
                .setUserId(hostHolder.getUser().getId());
        eventProducer.fireEvent(event);

        return CommunityUtil.getJSONString(0, "发布成功！");
    }

    @RequestMapping(path = "/detail/{discussPostId}", method = RequestMethod.GET)
    public String getDiscussPost(@PathVariable("discussPostId") int discussPostId, Model model, Page page) {
        User user = hostHolder.getUser();
        // 帖子
        DiscussPost post = discussPostService.findDiscussPostById(discussPostId);
        model.addAttribute("post", post);
        model.addAttribute("postLikeCount", likeService.findEntityLikeCount(ENTITY_TYPE_POST, post.getId()));
        model.addAttribute("postLikeStatus", user == null ? 0 : likeService.findUserLikeEntityStatus(ENTITY_TYPE_POST, post.getId(), user.getId()));
        // 楼主
        User postUser = userService.findUserById(post.getUserId());
        model.addAttribute("user", postUser);
        // 帖子的所有评论
        page.setLimit(5);
        page.setPath("/discuss/detail/" + discussPostId);
        page.setRows(post.getCommentCount());
        List<Comment> commentList = commentService.findCommentsByEntity(ENTITY_TYPE_POST, discussPostId, page.getOffset(), page.getLimit());

        // 所有评论以及对评论的回复
        List<Map<String, Object>> commentVoList = new ArrayList<>();
        if (commentList != null) {
            for (Comment comment : commentList) {
                Map<String, Object> commentVo = new HashMap<>();
                commentVo.put("comment", comment);
                commentVo.put("author", userService.findUserById(comment.getUserId()));
                commentVo.put("commentLikeCount", likeService.findEntityLikeCount(ENTITY_TYPE_COMMENT, comment.getId()));
                commentVo.put("commentLikeStatus", user == null ? 0 : likeService.findUserLikeEntityStatus(ENTITY_TYPE_COMMENT, comment.getId(), user.getId()));

                // 对本条评论的回复列表
                List<Comment> replyList = commentService.findCommentsByEntity(ENTITY_TYPE_COMMENT, comment.getId(), 0, Integer.MAX_VALUE);
                List<Map<String, Object>> replyVoList = new ArrayList<>();
                if (replyList != null) {
                    for (Comment reply : replyList) {
                        Map<String, Object> replyVo = new HashMap<>();
                        replyVo.put("reply", reply);
                        replyVo.put("author", userService.findUserById(reply.getUserId()));
                        replyVo.put("replyLikeCount", likeService.findEntityLikeCount(ENTITY_TYPE_COMMENT, reply.getId()));
                        replyVo.put("replyLikeStatus", user == null ? 0 : likeService.findUserLikeEntityStatus(ENTITY_TYPE_COMMENT, reply.getId(), user.getId()));
                        User target = reply.getTargetId() == 0 ? null : userService.findUserById(reply.getTargetId());
                        replyVo.put("target", target);
                        replyVoList.add(replyVo);
                    }
                }
                commentVo.put("replys", replyVoList);
                commentVo.put("replyCount", commentService.findCountByEntity(ENTITY_TYPE_COMMENT, comment.getId()));
                commentVoList.add(commentVo);
            }
        }
        model.addAttribute("comments", commentVoList);

        return "/site/discuss-detail";
    }
}
