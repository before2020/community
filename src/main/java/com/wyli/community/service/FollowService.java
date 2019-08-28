package com.wyli.community.service;

import com.wyli.community.entity.User;
import com.wyli.community.util.CommunityConstants;
import com.wyli.community.util.CommunityUtil;
import com.wyli.community.util.HostHolder;
import com.wyli.community.util.RedisKeyUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.data.redis.core.RedisOperations;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.SessionCallback;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
public class FollowService implements CommunityConstants {
    @Autowired
    private RedisTemplate redisTemplate;
    @Autowired
    private UserService userService;
    @Autowired
    private HostHolder hostHolder;

    // 用户关注某实体
    public void follow(int userId, int entityType, int entityId) {
        redisTemplate.execute(new SessionCallback() {
            @Override
            public Object execute(RedisOperations redisOperations) throws DataAccessException {
                String followeeKey = RedisKeyUtil.getFolloweeKey(userId, entityType);
                String followerKey = RedisKeyUtil.getFollowerKey(entityType, entityId);

                redisOperations.multi();
                // 有序集合，用关注时间排序
                redisTemplate.opsForZSet().add(followeeKey, entityId, System.currentTimeMillis());
                redisTemplate.opsForZSet().add(followerKey, userId, System.currentTimeMillis());
                return redisOperations.exec();
            }
        });
    }

    public void unfollow(int userId, int entityType, int entityId) {
        redisTemplate.execute(new SessionCallback() {
            @Override
            public Object execute(RedisOperations redisOperations) throws DataAccessException {
                String followeeKey = RedisKeyUtil.getFolloweeKey(userId, entityType);
                String followerKey = RedisKeyUtil.getFollowerKey(entityType, entityId);

                redisOperations.multi();
                // 有序集合，用关注时间排序
                redisTemplate.opsForZSet().remove(followeeKey, entityId);
                redisTemplate.opsForZSet().remove(followerKey, userId);
                return redisOperations.exec();
            }
        });
    }

    // 实体有多少粉丝
    public long findFollowerCount(int entityType, int entityId) {
        String followerKey = RedisKeyUtil.getFollowerKey(entityType, entityId);
        return redisTemplate.opsForZSet().zCard(followerKey);
    }

    // 用户关注的实体数量
    public long findFolloweeCount(int userId, int entityType) {
        String followeeKey = RedisKeyUtil.getFolloweeKey(userId, entityType);
        return redisTemplate.opsForZSet().zCard(followeeKey);
    }

    // 用户是否关注某实体
    public boolean hasFollowedEntity(int userId, int entityType, int entityId) {
        String followeeKey = RedisKeyUtil.getFolloweeKey(userId, entityType);
        // 如果分数不为空，说明用户关注的集合里有这个实体
        return redisTemplate.opsForZSet().score(followeeKey, entityId) != null;
    }

    // 查询用户关注的人
    public List<Map<String, Object>> findFolloweeList(int userId, int offset, int limit) {
        String followeeKey = RedisKeyUtil.getFolloweeKey(userId, ENTITY_TYPE_USER);
        // 用户关注的人的id集合，按照最近加入的顺序
        Set<Integer> ids = redisTemplate.opsForZSet().reverseRange(followeeKey, offset, offset + limit - 1);
        List<Map<String, Object>> list = new ArrayList<>();
        if (ids != null) {
            for (int id : ids) {
                Map<String, Object> map = new HashMap<>();
                User followee = userService.findUserById(id);
                Double score = redisTemplate.opsForZSet().score(followeeKey, id);
                // host user有没有关注这个人
                boolean hasFollowed = hostHolder.getUser() != null && hasFollowedEntity(hostHolder.getUser().getId(), ENTITY_TYPE_USER, id);
                map.put("followee", followee);
                map.put("followTime", new Date(score.longValue()));
                map.put("hasFollowed", hasFollowed);
                list.add(map);
            }
            return list;
        }
        return null;
    }

    // 查询用户的粉丝
    public List<Map<String, Object>> findFollowerList(int userId, int offset, int limit) {
        String followerKey = RedisKeyUtil.getFollowerKey(ENTITY_TYPE_USER, userId);
        Set<Integer> ids = redisTemplate.opsForZSet().reverseRange(followerKey, offset, offset + limit - 1);
        List<Map<String, Object>> list = new ArrayList<>();
        if (ids != null) {
            for (int id : ids) {
                Map<String, Object> map = new HashMap<>();
                User follower = userService.findUserById(id);
                Double score = redisTemplate.opsForZSet().score(followerKey, id);
                boolean hasFollowed = hostHolder.getUser() != null && hasFollowedEntity(hostHolder.getUser().getId(), ENTITY_TYPE_USER, id);

                map.put("follower", follower);
                map.put("followTime", new Date(score.longValue()));
                map.put("hasFollowed", hasFollowed);
                list.add(map);
            }
            return list;
        }
        return null;
    }


}
