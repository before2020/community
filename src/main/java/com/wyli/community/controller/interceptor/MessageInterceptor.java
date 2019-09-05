package com.wyli.community.controller.interceptor;

import com.wyli.community.entity.User;
import com.wyli.community.service.MessageService;
import com.wyli.community.util.HostHolder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@Component
public class MessageInterceptor implements HandlerInterceptor {
    @Autowired
    private MessageService messageService;
    @Autowired
    private HostHolder hostHolder;

    @Override
    public void postHandle(HttpServletRequest request, HttpServletResponse response, Object handler, ModelAndView modelAndView) throws Exception {
        // 统计所有未读对话和未读系统通知的和
        User user = hostHolder.getUser();
        if (user != null && modelAndView != null) {
            int count1 = messageService.findUnreadMessageCount(user.getId(), null);
            int count2 = messageService.findUnreadNoticeCount(user.getId(), null);
            modelAndView.addObject("totalUnreadCount", count1 + count2);
        }
    }
}
