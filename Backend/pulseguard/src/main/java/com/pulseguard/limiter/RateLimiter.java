package com.pulseguard.limiter;

import org.nailyourinterview.lld.rate_limiter.enums.RateLimitType;
import org.nailyourinterview.lld.rate_limiter.model.RateLimitConfig;


public abstract class RateLimiter {

    protected final RateLimitConfig config;
    protected final RateLimitType type;

    public RateLimiter(RateLimitConfig config, RateLimitType type) {
        this.config = config;
        this.type = type;
    }

  
    //  Check if a user is allowed to make a request.
   
    public abstract boolean allowRequest(String userId);

   
    public RateLimitConfig getConfig() {
        return config;
    }

    public RateLimitType getType() {
        return type;
    }
}
