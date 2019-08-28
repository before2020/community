package com.wyli.community.controller;

import com.wyli.community.annotation.LoginRequired;
import com.wyli.community.service.FollowService;
import com.wyli.community.util.CommunityUtil;
import com.wyli.community.util.HostHolder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller
public class FollowController {
    @Autowired
    private HostHolder hostHolder;
    @Autowired
    private FollowService followService;

    @RequestMapping(path = "/follow", method = RequestMethod.GET)
    @ResponseBody
    public String follow(int entityType, int entityId) {
        if (hostHolder.getUser() == null) {
            return CommunityUtil.getJSONString(1, "请先登录！");
        }
        followService.follow(hostHolder.getUser().getId(), entityType, entityId);
        return CommunityUtil.getJSONString(0);
    }

    @RequestMapping(path = "/unfollow", method = RequestMethod.GET)
    @ResponseBody
    public String unfollow(int entityType, int entityId) {
        if (hostHolder.getUser() == null) {
            return CommunityUtil.getJSONString(1, "请先登录！");
        }
        followService.unfollow(hostHolder.getUser().getId(), entityType, entityId);
        return CommunityUtil.getJSONString(0);
    }
}
