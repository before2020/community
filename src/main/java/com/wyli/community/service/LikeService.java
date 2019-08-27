package com.wyli.community.service;

import com.wyli.community.util.RedisKeyUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.data.redis.core.RedisOperations;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.SessionCallback;
import org.springframework.stereotype.Service;

@Service
public class LikeService {
    @Autowired
    private RedisTemplate redisTemplate;

    // 用户点赞
    public void like(int entityType, int entityId, int userId, int entityUserId) {
        // 事务模式
        redisTemplate.execute(new SessionCallback() {
            @Override
            public Object execute(RedisOperations redisOperations) throws DataAccessException {
                String entityLikeKey = RedisKeyUtil.getEntityLikeKey(entityType, entityId);
                String userLikeKey = RedisKeyUtil.getUserLikeKey(entityUserId);
                // 用户之前是否已经点赞，要在事务开始前查询
                boolean hasLiked = redisTemplate.opsForSet().isMember(entityLikeKey, userId);
                // 事务开始
                redisOperations.multi();
                if (!hasLiked) {
                    redisTemplate.opsForSet().add(entityLikeKey, userId);
                    // 之前没赞过，被赞用户收到的总赞数加一
                    redisTemplate.opsForValue().increment(userLikeKey);
                } else {
                    redisTemplate.opsForSet().remove(entityLikeKey, userId);
                    // 之前赞过，被赞用户收到的总赞数减一
                    redisTemplate.opsForValue().decrement(userLikeKey);
                }
                // 提交事务
                return redisOperations.exec();
            }
        });
    }

    // 实体有多少个赞
    public long findEntityLikeCount(int entityType, int entityId) {
        String entityLikeKey = RedisKeyUtil.getEntityLikeKey(entityType, entityId);
        return redisTemplate.opsForSet().size(entityLikeKey);
    }

    // 用户是否点赞过某实体
    public int findUserLikeEntityStatus(int entityType, int entityId, int userId) {
        String entityLikeKey = RedisKeyUtil.getEntityLikeKey(entityType, entityId);
        return redisTemplate.opsForSet().isMember(entityLikeKey, userId) ? 1 : 0;
    }

    // 用户被他人点赞的总数
    public int findUserLikeCount(int userId) {
        String userLikeKey = RedisKeyUtil.getUserLikeKey(userId);
        Integer likeCount = (Integer)redisTemplate.opsForValue().get(userLikeKey);
        return likeCount != null ? likeCount : 0;
    }
}
