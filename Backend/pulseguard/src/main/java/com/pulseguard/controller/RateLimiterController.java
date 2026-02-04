package com.pulseguard.controller;

import com.pulseguard.service.RateLimiterService;
import org.nailyourinterview.lld.rate_limiter.enums.UserTier;
import org.nailyourinterview.lld.rate_limiter.model.User;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/rate-limit")
public class RateLimiterController {

    private final RateLimiterService rateLimiterService;

    public RateLimiterController(RateLimiterService rateLimiterService) {
        this.rateLimiterService = rateLimiterService;
    }

 
    @GetMapping("/check")
    public String checkLimit(
            @RequestParam String userId,
            @RequestParam UserTier tier
    ) {
        User user = new User(userId, tier);

        boolean allowed = rateLimiterService.allowRequest(user);

        if (allowed) {
            return "✅ Request allowed";
        } else {
            return "⛔ Rate limit exceeded";
        }
    }
}
