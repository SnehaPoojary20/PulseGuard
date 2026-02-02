package com.pulseguard.limiter;

import org.nailyourinterview.lld.rate_limiter.model.RateLimitConfig;
import org.nailyourinterview.lld.rate_limiter.enums.RateLimitType;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ListOperations;

import java.time.Duration;

public class SlidingWindowLogRateLimiter extends RateLimiter {

    private final RedisTemplate<String, Object> redisTemplate;

    public SlidingWindowLogRateLimiter(RedisTemplate<String, Object> redisTemplate, RateLimitConfig config) {
        super(config, RateLimitType.SLIDING_WINDOW_LOG);
        this.redisTemplate = redisTemplate;
    }

    @Override
    public boolean allowRequest(String userId) {
        String redisKey = "slidingwindow:" + userId;
        ListOperations<String, Object> listOps = redisTemplate.opsForList();

        long now = System.currentTimeMillis();
        long windowMillis = config.getWindowInSeconds() * 1000L;

        // Remove expired timestamps
        listOps.remove(redisKey, 0, now - windowMillis);

        Long count = listOps.size(redisKey);

        if (count < config.getMaxRequests()) {
            // Add current timestamp
            listOps.rightPush(redisKey, now);
            // Set TTL for safety
            redisTemplate.expire(redisKey, Duration.ofSeconds(config.getWindowInSeconds()));
            return true;
        } else {
            return false; // Rate limit exceeded
        }
    }
}
