package com.pulseguard.limiter;

import org.nailyourinterview.lld.rate_limiter.model.RateLimitConfig;
import org.nailyourinterview.lld.rate_limiter.enums.RateLimitType;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ValueOperations;

import java.time.Duration;

public class FixedWindowRateLimiter extends RateLimiter {

    private final RedisTemplate<String, Object> redisTemplate;

    public FixedWindowRateLimiter(RedisTemplate<String, Object> redisTemplate, RateLimitConfig config) {
        super(config, RateLimitType.FIXED_WINDOW);
        this.redisTemplate = redisTemplate;
    }

    @Override
    public boolean allowRequest(String userId) {
        ValueOperations<String, Object> ops = redisTemplate.opsForValue();
        String redisKey = "fixedwindow:" + userId;

        // Increment the request count
        Long count = ops.increment(redisKey, 1);

        if (count == 1) {
            // First request → set expiration equal to window
            redisTemplate.expire(redisKey, Duration.ofSeconds(config.getWindowInSeconds()));
        }

        // Allow request if within maxRequests
        return count <= config.getMaxRequests();
    }
}

