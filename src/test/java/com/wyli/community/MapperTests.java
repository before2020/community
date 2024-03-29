package com.wyli.community;

import com.wyli.community.dao.DiscussPostMapper;
import com.wyli.community.dao.LoginTicketMapper;
import com.wyli.community.dao.MessageMapper;
import com.wyli.community.dao.UserMapper;
import com.wyli.community.entity.DiscussPost;
import com.wyli.community.entity.LoginTicket;
import com.wyli.community.entity.Message;
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
    @Autowired
    private LoginTicketMapper loginTicketMapper;
    @Autowired
    private MessageMapper messageMapper;

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


    @Test
    public void testInsertLoginTicket() {
        LoginTicket loginTicket = new LoginTicket();
        loginTicket.setUserId(101);
        loginTicket.setStatus(1);
        loginTicket.setTicket("world");
        loginTicket.setExpired(new Date(System.currentTimeMillis() + 1000 * 60 * 10));
        loginTicketMapper.insertLoginTicket(loginTicket);
        System.out.println(loginTicket.getId());
    }

    @Test
    public void testSelectByTicket() {
        LoginTicket loginTicket = loginTicketMapper.selectByTicket("world");
        System.out.println(loginTicket);
    }

    @Test
    public void testMessageMapper() {
        List<Message> messages1 = messageMapper.selectConversations(111, 0, 10);
        for (Message message : messages1) {
            System.out.println(message);
        }
        List<Message> messages2 = messageMapper.selectMessages("111_131", 0, 10);
        for (Message message : messages2) {
            System.out.println(message);
        }
    }
}
