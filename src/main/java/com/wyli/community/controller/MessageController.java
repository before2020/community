package com.wyli.community.controller;

import com.wyli.community.annotation.LoginRequired;
import com.wyli.community.entity.Comment;
import com.wyli.community.entity.Message;
import com.wyli.community.entity.Page;
import com.wyli.community.entity.User;
import com.wyli.community.service.MessageService;
import com.wyli.community.service.UserService;
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
public class MessageController {

    @Autowired
    private HostHolder hostHolder;
    @Autowired
    private MessageService messageService;
    @Autowired
    private UserService userService;

    // 会话列表
    @LoginRequired
    @RequestMapping(path = "/conversations/all", method = RequestMethod.GET)
    public String getConversations(Model model, Page page) {
        User user = hostHolder.getUser();
        page.setLimit(5);
        page.setPath("/conversations/all");
        page.setRows(messageService.findConversationCount(user.getId()));
        // 由每个对话的最新一条消息组成的列表
        List<Message> cons = messageService.findConversations(user.getId(), page.getOffset(), page.getLimit());
        List<Map<String, Object>> conVoList = new ArrayList<>();
        for (Message con : cons) {
            Map<String, Object> conVo = new HashMap<>();
            conVo.put("conversation", con);
            // 对话里另一个用户
            User otherUser = userService.findUserById(user.getId() == con.getFromId() ? con.getToId() : con.getFromId());
            conVo.put("otherUser", otherUser);
            // 总共几条消息
            conVo.put("messageCount", messageService.findMessageCount(con.getConversationId()));
            // 几条未读消息
            conVo.put("unreadCount", messageService.findUnreadMessageCount(user.getId(), con.getConversationId()));

            conVoList.add(conVo);
        }
        model.addAttribute("conversations", conVoList);
        model.addAttribute("totalUnreadCount", messageService.findUnreadMessageCount(user.getId(), null));
        return "/site/conversation";
    }

    // 获得某会话的消息列表
    @LoginRequired
    @RequestMapping(path = "/conversations/detail/{otherUserId}", method = RequestMethod.GET)
    public String getMessages(@PathVariable("otherUserId") int otherUserId, Model model, Page page) {
        User user = hostHolder.getUser();
        User otherUser = userService.findUserById(otherUserId);
        String conversationId = user.getId() < otherUserId
                ? user.getId() + "_" + otherUserId
                : otherUserId + "_" + user.getId();
        page.setLimit(5);
        page.setPath("/conversations/detail/" + otherUserId);
        page.setRows(messageService.findMessageCount(conversationId));

        List<Message> messages = messageService.findMessages(conversationId, page.getOffset(), page.getLimit());
        List<Map<String, Object>> messageList = new ArrayList<>();
        for (Message message : messages) {
            Map<String, Object> map = new HashMap<>();
            map.put("message", message);
            map.put("fromUser", message.getFromId() == user.getId() ? user : otherUser);
            messageList.add(map);
        }

        // 把用户看到的消息设为已读
        List<Integer> ids = new ArrayList<>();
        for (Message message : messages) {
            if (message.getStatus() == 0 && message.getToId() == user.getId()) {
                ids.add(message.getId());
            }
        }
        if (!ids.isEmpty()) messageService.readMessage(ids);

        model.addAttribute("messages", messageList);
        model.addAttribute("otherUser", otherUser);
        return "/site/conversation-detail";
    }

    @LoginRequired
    @RequestMapping(path = "/message/send", method = RequestMethod.POST)
    @ResponseBody
    public String sendMessage(String toName, String content) {
        Integer.valueOf("def");
        Message message = new Message();
        message.setFromId(hostHolder.getUser().getId());
        User otherUser = userService.findUserByUsername(toName);
        if (otherUser == null) {
            return CommunityUtil.getJSONString(1, "找不到发信对象！");
        }
        int toId = otherUser.getId();
        message.setToId(toId);
        message.setContent(content);
        String conversationId = message.getFromId() < toId
                ? message.getFromId() + "_" + toId
                : toId + "_" + message.getFromId();
        message.setConversationId(conversationId);
        message.setCreateTime(new Date());
        messageService.addMessage(message);
        return CommunityUtil.getJSONString(0);
    }

    @LoginRequired
    @RequestMapping(path = "/message/delete", method = RequestMethod.GET)
    @ResponseBody
    public String deleteMessage(int id) {
        messageService.deleteMessage(id);
        return CommunityUtil.getJSONString(0);
    }
}
