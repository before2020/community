package com.wyli.community.util;

import com.wyli.community.entity.User;
import org.springframework.stereotype.Component;

/**
 * 用来在一个请求中保存用户信息，代替Session
 */
@Component
public class HostHolder {
    private ThreadLocal<User> users = new ThreadLocal<>();

    public void setUser(User user) {
        users.set(user);
    }

    public User getUser() {
        return users.get();
    }

    public void clearUser() {
        users.remove();
    }
}
