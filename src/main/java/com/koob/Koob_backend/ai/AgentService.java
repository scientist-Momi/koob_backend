package com.koob.Koob_backend.ai;

import com.koob.Koob_backend.rateLimiter.RateLimiterService;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.prompt.PromptTemplate;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.Map;

@Service
public class AgentService {

    private final ChatClient chatClient;
    private final RateLimiterService rateLimiterService;

    public AgentService(ChatClient chatClient, RateLimiterService rateLimiterService) {
        this.chatClient = chatClient;
        this.rateLimiterService = rateLimiterService;
    }

    public String chat(Long userId, String message, Long libraryId) {
        PromptTemplate promptTemplate = new PromptTemplate("""
                You are a helpful book assistant. Use tools to search and save books based on user requests.
                For example, if asked to find and save books, search first, then save.
                Always respond conversationally after actions.
                
                The user is working with library ID: {libraryId}.
                """);
        String systemPrompt = promptTemplate.render(Map.of("libraryId", libraryId));

        if (!rateLimiterService.tryConsumeGlobal()) {
            throw new ResponseStatusException(
                    HttpStatus.TOO_MANY_REQUESTS,
                    "Global rate limit exceeded. Please try again later."
            );
        }

        // 👤 Per-user limit
        if (!rateLimiterService.resolveUserBucket(userId).tryConsume(1)) {
            throw new ResponseStatusException(
                    HttpStatus.TOO_MANY_REQUESTS,
                    "You are sending too many requests. Please slow down."
            );
        }

        // 📅 Daily quota
        if (!rateLimiterService.tryConsumeDaily(userId)) {
            throw new ResponseStatusException(
                    HttpStatus.TOO_MANY_REQUESTS,
                    "You have reached your daily limit of 200 requests."
            );
        }

        return chatClient.prompt()
                .user(message)
                .system(systemPrompt)
                .call()
                .content();
    }
}
