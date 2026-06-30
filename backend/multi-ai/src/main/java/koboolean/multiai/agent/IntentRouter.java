package koboolean.multiai.agent;

import koboolean.multiai.domain.WorkerType;
import lombok.extern.slf4j.Slf4j;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

/**
 * AI를 활용하여 각 Agent로 분기 처리하도록 결정하는 Router
 */
@Component
@Slf4j
public class IntentRouter {

    private final ChatClient chatClient;
    private final String routerPrompt;


    public IntentRouter(ChatClient.Builder builder, @Qualifier("routerSystemPrompt") String routerPrompt) {
        this.chatClient = builder.build();
        this.routerPrompt = routerPrompt;
    }

    private record RoutingResponse(String reasoning, WorkerType selection){}

    public WorkerType determineWorker(String message){
        RoutingResponse entity = chatClient.prompt()
                .system(routerPrompt)
                .user(message)
                .call()
                .entity(RoutingResponse.class);

        log.info("Router 분석 : {}을 판단한 Reason {}", entity.selection(), entity.reasoning());

        return entity.selection();
    }
}
