package com.wyli.community.dao;

import com.wyli.community.entity.DiscussPost;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface DiscussPostMapper {

    List<DiscussPost> selectDiscussPosts(@Param("userId") int userId, int offset, int limit);

    int selectDiscussPostRows(@Param("userId") int userId);

    int insertDiscussPost(DiscussPost post);

    DiscussPost selectDiscussPostById(int id);
}
