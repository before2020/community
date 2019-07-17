package com.wyli.community.dao;

import com.wyli.community.entity.User;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface UserMapper {
    User selectById(int id);
    User selectByUsername(String username);
    User selectByEmail(String email);
    int insertUser(User user);
    int updateStatus(int id, String status);
    int updateHeader(int id, String headerUrl);
    int updatePassword(int id, String password);
}
