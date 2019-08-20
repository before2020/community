package com.wyli.community.controller;

import com.wyli.community.entity.DiscussPost;
import com.wyli.community.entity.Page;
import com.wyli.community.entity.User;
import com.wyli.community.service.DiscussPostService;
import com.wyli.community.service.UserService;
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
public class HomeController {

    @Autowired
    private DiscussPostService discussPostService;

    @Autowired
    private UserService userService;

    @RequestMapping(path = "/index", method = RequestMethod.GET)
    public String getIndexPage(Model model, Page page) {

        page.setRows(discussPostService.findDiscussPostRows(0));
        page.setPath("/index");

        List<DiscussPost> discussPosts = discussPostService.findDiscussPosts(0, page.getOffset(), page.getLimit());
        List<Map<String, Object>> list = new ArrayList<>();
        for (DiscussPost post : discussPosts) {
            Map<String, Object> map = new HashMap<>();
            User user = userService.findUserById(post.getUserId());
            map.put("post", post);
            map.put("user", user);
            list.add(map);
        }
        model.addAttribute("discussPosts", list);
        return "index";
    }

    @RequestMapping(path = "/error", method = RequestMethod.GET)
    public String getErrorPage() {
        return "/error/404";
    }
}
