package com.koob.Koob_backend;

import com.koob.Koob_backend.ai.BookTools;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.model.ChatModel;
import org.springframework.ai.chat.model.ChatResponse;
import org.springframework.ai.chat.prompt.Prompt;
import org.springframework.ai.openai.OpenAiChatOptions;
import org.springframework.ai.tool.ToolCallback;
import org.springframework.ai.tool.ToolCallbacks;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

@SpringBootApplication
public class KoobBackendApplication {

//    private final ChatModel chatModel;
//
//    public KoobBackendApplication(ChatModel chatModel) {
//        this.chatModel = chatModel;
//    }


    public static void main(String[] args) {
		SpringApplication.run(KoobBackendApplication.class, args);
	}

}
