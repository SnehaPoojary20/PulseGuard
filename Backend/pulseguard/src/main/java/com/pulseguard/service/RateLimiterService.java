package org.nailyourinterview.lld.rate_limiter.service;

import org.nailyourinterview.lld.rate_limiter.enums.RateLimitType;
import org.nailyourinterview.lld.rate_limiter.enums.UserTier;
import org.nailyourinterview.lld.rate_limiter.factory.RateLimiterFactory;
import org.nailyourinterview.lld.rate_limiter.limiter.RateLimiter;
import org.nailyourinterview.lld.rate_limiter.model.RateLimitConfig;
import org.nailyourinterview.lld.rate_limiter.model.User;

import java.util.HashMap;
import java.util.Map;

public class RateLimiterService {  
     private final Map<UserTier, RateLimiter> rateLimiters = new HashMap<>();

     public RateLimiterService() {
        rateLimiters.put(
            UserTier.FREE,
            RateLimiterFactory.createRateLimiter(
                RateLimitType.TOKEN_BUCKET,
                new RateLimitConfig(10, 60) // 10 req/min for free user
            )
        );

        rateLimiters.put(
            UserTier.PREMIUM,
            RateLimiterFactory.createRateLimiter(
                RateLimitType.FIXED_WINDOW,
                new RateLimitConfig(100, 60) // 100 req/min for premium users
            )
        );
     }

     public boolean allowRequest(User user) {
        RateLimiter limiter = rateLimiters.get(user.getTier());

        if (limiter == null) {
             throw new IllegalArgumentException("No limiter configured for tier: " + user.getTier());
        }
        return limiter.allowRequest(user.getUserId());
     }
}
