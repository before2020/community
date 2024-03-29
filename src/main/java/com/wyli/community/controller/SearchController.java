package com.wyli.community.controller;

import com.wyli.community.entity.DiscussPost;
import com.wyli.community.entity.Page;
import com.wyli.community.service.ElasticsearchService;
import com.wyli.community.service.LikeService;
import com.wyli.community.service.UserService;
import com.wyli.community.util.CommunityConstants;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Controller
public class SearchController implements CommunityConstants {
    @Autowired
    private ElasticsearchService elasticService;
    @Autowired
    private UserService userService;
    @Autowired
    private LikeService likeService;

    @RequestMapping(path = "/search", method = RequestMethod.GET)
    public String search(String keyword, Page page, Model model) {
        org.springframework.data.domain.Page<DiscussPost> searchResult =
                elasticService.searchDiscussposts(keyword, page.getCurrent() - 1, page.getLimit());

        List<Map<String, Object>> list = new ArrayList<>();
        if (searchResult != null) {
              for (DiscussPost post : searchResult) {
                  Map<String, Object> map = new HashMap<>();
                  map.put("post", post);
                  map.put("user", userService.findUserById(post.getUserId()));
                  map.put("likeCount", likeService.findEntityLikeCount(ENTITY_TYPE_POST, post.getId()));
                  list.add(map);
              }
        }
        model.addAttribute("discussPostList", list);
        model.addAttribute("keyword", keyword);

        page.setPath("/search?keyword=" + keyword);
        page.setRows(searchResult == null ? 0 : (int) searchResult.getTotalElements());
        return "/site/search";
    }
}
