package com.pulseguard.fallback;

import org.springframework.data.redis.core.RedisTemplate;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

public class RedisFallbackHandler {

    private final RedisTemplate<String, Object> redisTemplate;

    // Simple in-memory fallback counter
    private final ConcurrentMap<String, Integer> localCounter = new ConcurrentHashMap<>();

    public RedisFallbackHandler(RedisTemplate<String, Object> redisTemplate) {
        this.redisTemplate = redisTemplate;
    }

    // Check if Redis is alive
    public boolean isRedisAvailable() {
        try {
            redisTemplate.getConnectionFactory()
                    .getConnection()
                    .ping();
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    // Redis-backed increment
    public long incrementRedis(String key, int windowSeconds) {
        Long count = redisTemplate.opsForValue().increment(key);

        if (count != null && count == 1) {
            redisTemplate.expire(key, java.time.Duration.ofSeconds(windowSeconds));
        }

        return count == null ? 0 : count;
    }

    // In-memory fallback increment
    public int incrementLocal(String key) {
        return localCounter.merge(key, 1, Integer::sum);
    }

    // Reset fallback 
    public void resetLocal(String key) {
        localCounter.remove(key);
    }
}
