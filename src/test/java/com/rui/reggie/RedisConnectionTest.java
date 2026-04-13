package com.rui.reggie;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.redis.core.StringRedisTemplate;

@SpringBootTest
class RedisConnectionTest {

    @Autowired
    private StringRedisTemplate redisTemplate;

    @Test
    void testRedisConnection() {
        String result = redisTemplate.getConnectionFactory()
                .getConnection()
                .ping();
        System.out.println("Redis 返回: " + result);
        // 如果输出 "PONG"，说明连接成功
    }
}