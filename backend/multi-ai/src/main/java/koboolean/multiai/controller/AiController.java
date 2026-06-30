package koboolean.multiai.controller;

import koboolean.multiai.agent.IntentRouter;
import koboolean.multiai.agent.Orchestrator;
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

    private final Orchestrator orchestrator;

    @PostMapping
    public String chat(@RequestBody Map<String, String> requestBody,
                @RequestHeader(value = "Conversation-Id", required = false) String conversationId){

        String userMessage = requestBody.get("message");

        String currentConversationId = (conversationId != null) ? conversationId : UUID.randomUUID().toString();

        return orchestrator.chat(userMessage, currentConversationId);
    }
}
