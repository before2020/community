package com.wyli.community.controller;

import com.wyli.community.entity.Event;
import com.wyli.community.entity.User;
import com.wyli.community.event.EventProducer;
import com.wyli.community.service.LikeService;
import com.wyli.community.util.CommunityConstants;
import com.wyli.community.util.CommunityUtil;
import com.wyli.community.util.HostHolder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.HashMap;
import java.util.Map;

@Controller
public class LikeController implements CommunityConstants {
    @Autowired
    private HostHolder hostHolder;
    @Autowired
    private LikeService likeService;
    @Autowired
    private EventProducer eventProducer;

    @RequestMapping(path = "/like", method = RequestMethod.POST)
    @ResponseBody
    public String like(int entityType, int entityId, int entityUserId, int postId) {
        User user = hostHolder.getUser();
        likeService.like(entityType, entityId, user.getId(), entityUserId);
        long likeCount = likeService.findEntityLikeCount(entityType, entityId);
        int likeStatus = likeService.findUserLikeEntityStatus(entityType, entityId, user.getId());
        Map<String, Object> map = new HashMap<>();
        map.put("likeCount", likeCount);
        map.put("likeStatus", likeStatus);

        if (likeStatus == 1) {
            Event event = new Event()
                    .setTopic(TOPIC_LIKE)
                    .setUserId(user.getId())
                    .setEntityType(entityType)
                    .setEntityId(entityId)
                    .setEntityUserId(entityUserId)
                    .setOthers("postId", postId);
            eventProducer.fireEvent(event);
        }

        return CommunityUtil.getJSONString(0, null, map);
    }
}
