package com.wyli.community;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.dao.DataAccessException;
import org.springframework.data.redis.core.*;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit4.SpringRunner;

@RunWith(SpringRunner.class)
@SpringBootTest
@ContextConfiguration(classes = CommunityApplication.class)
public class RedisTests {
    @Autowired
    private RedisTemplate<String, Object> redisTemplate;

    @Test
    public void testStrings() {
        String key = "test:count";
        redisTemplate.opsForValue().set(key, 1);
        System.out.println(redisTemplate.opsForValue().get(key));
        System.out.println(redisTemplate.opsForValue().increment(key));
        System.out.println(redisTemplate.opsForValue().decrement(key));
    }

    @Test
    public void testHashes() {
        String key = "test:user";
        redisTemplate.opsForHash().put(key, "id", 101);
        redisTemplate.opsForHash().put(key, "name", "Casey");
        System.out.println(redisTemplate.opsForHash().get(key, "id"));
        System.out.println(redisTemplate.opsForHash().get(key, "name"));
    }

    @Test
    public void testLists() {
        String key = "ids";
        redisTemplate.opsForList().leftPush(key, 101);
        redisTemplate.opsForList().leftPush(key, 102);
        redisTemplate.opsForList().leftPush(key, 103);
        redisTemplate.opsForList().rightPush(key, 104);
        System.out.println(redisTemplate.opsForList().leftPop(key)); //103
        System.out.println(redisTemplate.opsForList().rightPop(key));//104
    }

    @Test
    public void testSets() {
        String key = "test:hobbies";
        redisTemplate.opsForSet().add(key, "soccer");
        redisTemplate.opsForSet().add(key, "piano", "dance", "draw");
        System.out.println(redisTemplate.opsForSet().members(key));
    }
    // 重复对同一个key的操作
    @Test
    public void testBoundValueOps() {
        String key = "test:count";
        BoundValueOperations<String, Object> operations = redisTemplate.boundValueOps(key);
        System.out.println(operations.get());
        System.out.println(operations.increment());
        System.out.println(operations.increment());
    }

    @Test
    public void testBound() {
        String key = "test:user";
        BoundHashOperations<String, Object, Object> operations = redisTemplate.boundHashOps(key);
        operations.put("age", 23);
        operations.put("school", "ZJU");
        System.out.println(operations.get("school"));
        System.out.println(operations.get("age"));
    }

    @Test
    public void testTransactional() {
        Object obj = redisTemplate.execute(new SessionCallback<Object>() {
            @Override
            public Object execute(RedisOperations operations) throws DataAccessException {
                String key = "test:tx";
                //启用事务，以下语句不会立刻执行
                operations.multi();
                operations.opsForSet().add(key, "aaa");
                operations.opsForSet().add(key, "bbb");
                // 不能查出刚刚加入集合的数据，因为上面的语句还没执行
                System.out.println(operations.opsForSet().members(key));
                //提交事务
                return operations.exec();
            }
        });
    }
}
