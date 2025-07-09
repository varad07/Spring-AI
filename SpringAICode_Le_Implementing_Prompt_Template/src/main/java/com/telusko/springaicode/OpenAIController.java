package com.telusko.springaicode;

import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.model.ChatResponse;
import org.springframework.ai.chat.prompt.Prompt;
import org.springframework.ai.chat.prompt.PromptTemplate;
import org.springframework.ai.openai.OpenAiChatModel;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
public class OpenAIController {
    private ChatClient chatClient;


    public OpenAIController(OpenAiChatModel chatModel) {
        this.chatClient = ChatClient.create(chatModel);
    }
//    ChatMemory chatMemory = MessageWindowChatMemory.builder().build();
//
//    public OpenAIController(ChatClient.Builder builder) {
//        this.chatClient = builder
//                .defaultAdvisors(MessageChatMemoryAdvisor.builder(chatMemory)
//                        .build())
//                .build();
//
//    }

    @GetMapping("/api/{message}")
    public ResponseEntity<String> getAnswer(@PathVariable String message) {
        ChatResponse chatResponse = chatClient.prompt(message)
                .call()
                .chatResponse();

        System.out.println(chatResponse.getMetadata().getModel());


        String response = chatResponse
                .getResult()
                .getOutput()
                .getText();
        return ResponseEntity.ok(response);
    }


    @PostMapping("/api/recommend")
    public String recommend(@RequestParam String type, @RequestParam String year, @RequestParam String lang) {

        String temp = """
                I want to watch a {type} movie tonight with good rating, 
                looking  for movies around this year {year}. 
                The  language im looking for is {lang}.
                Suggest one specific movie and tell me the cast and length of the movie.
                
                
                response format should be:
                1. Movie Name
                2. basic plot
                3. cast
                4. length
                5. IMDB rating
                """;
        PromptTemplate promptTemplate = new PromptTemplate(temp);


        Prompt prompt = promptTemplate.create(Map.of(
                "type", type,
                "year", year,
                "lang", lang
        ));


        String response = chatClient
                .prompt(prompt)
                .call()
                .content();
        return response;


    }
}

