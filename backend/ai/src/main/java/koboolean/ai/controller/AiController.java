package koboolean.ai.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.memory.ChatMemory;
import org.springframework.web.bind.annotation.*;

import java.util.Map;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/chat")
@RequiredArgsConstructor
@CrossOrigin("http://localhost:5173")
public class AiController {

    private final ChatClient chatClient;

    @PostMapping
    public String chat(@RequestBody Map<String, String> requestBody,
                @RequestHeader(value = "Conversation-Id", required = false) String conversationId){

        String userMessage = requestBody.get("message");

        String currentConversationId = (conversationId != null) ? conversationId : UUID.randomUUID().toString();

        return chatClient.prompt()
                .user(userMessage)
                .advisors(a -> a.param(ChatMemory.CONVERSATION_ID, currentConversationId))
                .call()
                .content();
    }
}
