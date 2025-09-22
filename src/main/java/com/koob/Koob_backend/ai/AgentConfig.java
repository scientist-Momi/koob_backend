package com.koob.Koob_backend.ai;

import dev.langchain4j.model.chat.ChatLanguageModel;
import dev.langchain4j.service.AiServices;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class AgentConfig {
    @Bean
    public BookAgent bookAgent(ChatLanguageModel model, BookTools tools) {
        return AiServices.builder(BookAgent.class)
                .chatModel(model)
                .tools(tools)
                .build();
    }
}
