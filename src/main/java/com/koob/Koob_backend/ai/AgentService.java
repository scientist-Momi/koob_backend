package com.koob.Koob_backend.ai;

import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.prompt.PromptTemplate;
import org.springframework.stereotype.Service;

import java.util.Map;

@Service
public class AgentService {

    private final ChatClient chatClient;

    public AgentService(ChatClient chatClient) {
        this.chatClient = chatClient;
    }

    public String chat(String message) {
        PromptTemplate promptTemplate = new PromptTemplate("""
                You are a helpful book assistant. Use tools to search and save books based on user requests.
                For example, if asked to find and save books, search first, then save.
                Always respond conversationally after actions.
                """);
        String systemPrompt = promptTemplate.render(Map.of());

        return chatClient.prompt()
                .user(message)
                .system(systemPrompt)
                .call()
                .content();
    }
}
