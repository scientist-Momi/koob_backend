package com.koob.Koob_backend.ai;

import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.model.ChatModel;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class AgentConfig {

    @Bean
    public ChatClient chatClient(ChatModel chatModel, AgentTools agentTools) {
        return ChatClient.builder(chatModel)
                .defaultTools(agentTools)
                .build();
    }
}
