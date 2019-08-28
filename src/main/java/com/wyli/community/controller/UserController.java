package com.wyli.community.controller;

import com.wyli.community.annotation.LoginRequired;
import com.wyli.community.entity.DiscussPost;
import com.wyli.community.entity.Page;
import com.wyli.community.entity.User;
import com.wyli.community.service.DiscussPostService;
import com.wyli.community.service.FollowService;
import com.wyli.community.service.LikeService;
import com.wyli.community.service.UserService;
import com.wyli.community.util.CommunityConstants;
import com.wyli.community.util.CommunityUtil;
import com.wyli.community.util.HostHolder;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Controller
public class UserController implements CommunityConstants {

    private static final Logger logger = LoggerFactory.getLogger(UserController.class);

    @Autowired
    private HostHolder hostHolder;
    @Autowired
    private UserService userService;
    @Autowired
    private LikeService likeService;
    @Autowired
    private FollowService followService;
    @Autowired
    private DiscussPostService discussPostService;

    @Value("${community.path.domain}")
    private String domain;
    @Value("${server.servlet.context-path}")
    private String contextPath;
    @Value("${community.path.upload}")
    private String uploadPath;

    @LoginRequired
    @RequestMapping(path = "/setting", method = RequestMethod.GET)
    public String getSettingPage() {
        return "/site/setting";
    }

    @LoginRequired
    @RequestMapping(path = "/upload", method = RequestMethod.POST)
    public String uploadHeader(MultipartFile headerImage, Model model) {
        if (headerImage == null) {
            model.addAttribute("error", "上传文件为空");
            return "/site/setting";
        }

        // 获取文件后缀名
        String filename = headerImage.getOriginalFilename();
        String suffix = filename.substring(filename.lastIndexOf('.'));
        if (StringUtils.isBlank(suffix)) {
            model.addAttribute("error", "文件格式不正确");
            return "/site/setting";
        }

        // 修改文件名，避免文件名重复
        filename = CommunityUtil.generateUUID() + suffix;
        // 存放位置
        File dest = new File(uploadPath, filename);
        try {
            headerImage.transferTo(dest);
        } catch (IOException e) {
            logger.error("文件上传失败" + e.getMessage());
            throw new RuntimeException("文件上传失败");
        }

        User user = hostHolder.getUser();
        String headerUrl = domain + contextPath + "/header/" + filename;
        userService.updateHeader(user.getId(), headerUrl);
        return "redirect:/index";
    }

    @RequestMapping(path = "/header/{filename}", method = RequestMethod.GET)
    public void getHeader(@PathVariable("filename") String filename, HttpServletResponse response) {
        filename = uploadPath + "/" + filename;
        String suffix = filename.substring(filename.lastIndexOf('.') + 1);
        response.setContentType("image/" + suffix);
        try {
            OutputStream os = response.getOutputStream();
            FileInputStream fis = new FileInputStream(filename);
            byte[] buffer = new byte[1024];
            int b = 0;
            while((b = fis.read(buffer)) != -1) {
                os.write(buffer, 0, b);
            }
        } catch (IOException e) {
            logger.error("读取头像失败");
        }
    }

    @RequestMapping(path = "/update_password", method = RequestMethod.POST)
    public String updatePassword(Model model, String old_password, String new_password, String confirm_password) {
        User loginUser = hostHolder.getUser();
        // 老密码是否不空
        if (StringUtils.isBlank(old_password)) {
            model.addAttribute("oldPasswordMsg", "密码不能为空");
            return "/site/setting";
        }
        // 老密码是否正确
        if (!CommunityUtil.md5(old_password + loginUser.getSalt()).equals(loginUser.getPassword())) {
            model.addAttribute("oldPasswordMsg", "密码不正确");
            return "/site/setting";
        }
        // 新密码是否不空
        if (StringUtils.isBlank(new_password)) {
            model.addAttribute("oldPasswordMsg", "新密码不能为空");
            return "/site/setting";
        }
        // 确认密码与新密码是否相同
        if (!confirm_password.equals(new_password)) {
            model.addAttribute("confirmPasswordMsg", "两次密码不一致");
            return "/site/setting";
        }
        int res = userService.updatePassword(loginUser.getEmail(), new_password);
        if (res == 200) {
            return "redirect:/logout";
        } else {
            logger.error("密码修改失败");
            return "/site/setting";
        }
    }

    @RequestMapping(value = "/profile/{userId}", method = RequestMethod.GET)
    public String getProfile(@PathVariable("userId") int userId, Model model) {
        // 要查看的用户
        model.addAttribute("user", userService.findUserById(userId));
        // 用户收到的点赞数
        model.addAttribute("likeCount", likeService.findUserLikeCount(userId));
        // 用户关注的人数
        model.addAttribute("followeeCount", followService.findFolloweeCount(userId, ENTITY_TYPE_USER));
        // 用户的粉丝数
        model.addAttribute("followerCount", followService.findFollowerCount(ENTITY_TYPE_USER, userId));
        // 是否已经关注该用户
        boolean hasFollowed = hostHolder.getUser() != null && followService.hasFollowedEntity(hostHolder.getUser().getId(), ENTITY_TYPE_USER, userId);
        model.addAttribute("hasFollowed", hasFollowed);
        return "/site/profile";
    }

    /**
     * 查询某用户发表的所有帖子
     * @param userId
     * @param page
     * @param model
     * @return
     */
    @RequestMapping(path = "/profile/{userId}/myposts", method = RequestMethod.GET)
    public String getMyPosts(@PathVariable("userId") int userId, Page page, Model model) {
        User user = userService.findUserById(userId);
        if (user == null) {
            throw new RuntimeException("用户不存在");
        }
        page.setRows(discussPostService.findDiscussPostRows(userId));
        page.setPath("/profile/" + userId + "/myposts");
        page.setLimit(5);
        List<DiscussPost> discussPosts = discussPostService.findDiscussPosts(userId, page.getOffset(), page.getLimit());
        List<Map<String, Object>> discussPostList = new ArrayList<>();
        for (DiscussPost post : discussPosts) {
            HashMap<String, Object> map = new HashMap<>();
            map.put("post", post);
            map.put("likeCount", likeService.findEntityLikeCount(ENTITY_TYPE_POST, post.getId()));
            discussPostList.add(map);
        }
        model.addAttribute("user", user);
        model.addAttribute("postCount", page.getRows());
        model.addAttribute("discussPostList", discussPostList);
        return "/site/my-post";
    }

    @RequestMapping(path = "/followees/{userId}", method = RequestMethod.GET)
    public String getFolloweeList(@PathVariable("userId") int userId, Page page, Model model) {
        User user = userService.findUserById(userId);
        if (user == null) {
            throw new RuntimeException("该用户不存在");
        }
        page.setLimit(5);
        page.setPath("/followees/" + userId);
        page.setRows((int) followService.findFolloweeCount(userId, ENTITY_TYPE_USER));
        List<Map<String, Object>> followeeList = followService.findFolloweeList(userId, page.getOffset(), page.getLimit());
        model.addAttribute("user", user);
        model.addAttribute("followeeList", followeeList);
        return "/site/followee";
    }

    @RequestMapping(path = "/followers/{userId}", method = RequestMethod.GET)
    public String getFollowerList(@PathVariable("userId") int userId, Page page, Model model) {
        User user = userService.findUserById(userId);
        if (user == null) {
            throw new RuntimeException("该用户不存在");
        }
        page.setLimit(5);
        page.setPath("/followers/" + userId);
        page.setRows((int) followService.findFollowerCount(userId, ENTITY_TYPE_USER));

        List<Map<String, Object>> followerList = followService.findFollowerList(userId, page.getOffset(), page.getLimit());
        model.addAttribute("user", user);
        model.addAttribute("followerList", followerList);
        return "/site/follower";
    }
}
