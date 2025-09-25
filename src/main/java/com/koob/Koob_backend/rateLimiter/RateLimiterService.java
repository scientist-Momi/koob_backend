package com.koob.Koob_backend.rateLimiter;

import io.github.bucket4j.Bandwidth;
import io.github.bucket4j.Bucket;
import io.github.bucket4j.Refill;
import org.springframework.stereotype.Component;

import java.time.Duration;
import java.time.LocalDate;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Component
public class RateLimiterService {
    private final Map<Long, Bucket> userBuckets = new ConcurrentHashMap<>();
    private final Bucket globalBucket;

    // Track daily quota per user
    private final Map<Long, DailyUsage> dailyUsage = new ConcurrentHashMap<>();

    public RateLimiterService() {
        this.globalBucket = Bucket.builder()
                .addLimit(limit -> limit.capacity(200)
                        .refillGreedy(200, Duration.ofMinutes(1)))
                .build();
    }

    public Bucket resolveUserBucket(Long userId) {
        return userBuckets.computeIfAbsent(userId, this::newUserBucket);
    }

    private Bucket newUserBucket(Long userId) {
        // Max 20 requests per minute
        return Bucket.builder()
                .addLimit(limit -> limit.capacity(20)
                        .refillGreedy(20, Duration.ofMinutes(1)))
                .build();
    }

    public boolean tryConsumeGlobal() {
        return globalBucket.tryConsume(1);
    }

    public synchronized boolean tryConsumeDaily(Long userId) {
        LocalDate today = LocalDate.now();
        DailyUsage usage = dailyUsage.computeIfAbsent(userId, id -> new DailyUsage(today, 0));

        // Reset counter if it's a new day
        if (!usage.date.equals(today)) {
            usage.date = today;
            usage.count = 0;
        }

        if (usage.count < 200) {
            usage.count++;
            return true;
        }

        return false; // Quota exceeded
    }

    private static class DailyUsage {
        LocalDate date;
        int count;

        DailyUsage(LocalDate date, int count) {
            this.date = date;
            this.count = count;
        }
    }
}
