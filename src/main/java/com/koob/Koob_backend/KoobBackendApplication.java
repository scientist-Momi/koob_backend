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

//    @Bean
//    public CommandLineRunner runner(ChatClient.Builder builder) {
//        return args -> {
//            ChatClient chatClient = builder.build();
//
//            ToolCallback[] bookTools = ToolCallbacks.from(new BookTools());
//            ChatResponse response = chatModel.call(
//                    new Prompt(
//                            "Use the tool to search for books about world war 2 and save the result to a file. Also show me the query you provided to the tools",
//                            OpenAiChatOptions.builder()
//                                    .toolCallbacks(bookTools)
//                                    .model("gpt-4o-mini")
//                                    .temperature(0.4)
//                                    .build()
//                    ));
//
//            System.out.println(response.getResults());
//        };
//    }

}
