package com.wyli.community;

import com.wyli.community.dao.DiscussPostMapper;
import com.wyli.community.dao.UserMapper;
import com.wyli.community.entity.DiscussPost;
import com.wyli.community.entity.User;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.Date;
import java.util.List;

@RunWith(SpringRunner.class)
@SpringBootTest
@ContextConfiguration(classes = CommunityApplication.class)
public class MapperTests {
    @Autowired
    private UserMapper userMapper;

    @Test
    public void testSelectUser() {
        User user = userMapper.selectById(101);
        System.out.println(user);

        User user2 = new User();
        user2.setUsername("Emily");
        user2.setPassword("23453");
        user2.setEmail("emily12@163.com");
        user2.setSalt("aaaaa");
        user2.setHeaderUrl("http://nowcoder/emily.com");
        user2.setActivationCode("");
        user2.setCreateTime(new Date());
        userMapper.insertUser(user2);
        System.out.println("new id:" + user2.getId());

        userMapper.updatePassword(150, "renew1234");
        System.out.println(userMapper.selectById(150).getPassword());
    }

    @Autowired
    private DiscussPostMapper discussPostMapper;

    @Test
    public void testSelectDiscussPosts() {
        List<DiscussPost> discussPosts = discussPostMapper.selectDiscussPosts(101, 0, 10);
        for (DiscussPost discussPost : discussPosts) {
            System.out.println(discussPost);
        }

        int count = discussPostMapper.selectDiscussPostRows(101);
        System.out.println(count);
    }
}
