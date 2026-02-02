package com.pulseguard.limiter;

import org.nailyourinterview.lld.rate_limiter.enums.RateLimitType;
import org.nailyourinterview.lld.rate_limiter.model.RateLimitConfig;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ValueOperations;

import java.time.Duration;

public class TokenBucketRateLimiter extends RateLimiter {

    private final RedisTemplate<String, Object> redisTemplate;

    public TokenBucketRateLimiter(RedisTemplate<String, Object> redisTemplate, RateLimitConfig config) {
        super(config, RateLimitType.TOKEN_BUCKET);
        this.redisTemplate = redisTemplate;
    }

    @Override
    public boolean allowRequest(String userId) {
        ValueOperations<String, Object> ops = redisTemplate.opsForValue();
        String redisKey = "tokenbucket:" + userId;

        // Increment the token count atomically in Redis
        Long currentTokens = ops.increment(redisKey, 1);

        if (currentTokens == 1) {
            // First request → set expiry based on window
            redisTemplate.expire(redisKey, Duration.ofSeconds(config.getWindowInSeconds()));
        }

        // Allow request if tokens <= max
        return currentTokens <= config.getMaxRequests();
    }
}

